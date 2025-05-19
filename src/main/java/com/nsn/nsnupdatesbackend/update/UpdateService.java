package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.config.PaginationConfig;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.notification.NotificationService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UpdateService {

    private final UpdateRepository updateRepository;
    private final UpdateMapper updateMapper;
    private final AppUserService appUserService;
    private final FollowService followService;
    private final NotificationService notificationService;
    private final PaginationConfig paginationConfig;

    @Autowired
    public UpdateService(UpdateRepository updateRepository, UpdateMapper updateMapper, AppUserService appUserService,
                         FollowService followService, NotificationService notificationService, PaginationConfig paginationConfig) {
            this.updateRepository = updateRepository;
            this.updateMapper = updateMapper;
            this.appUserService = appUserService;
            this.followService = followService;
            this.notificationService = notificationService;
        this.paginationConfig = paginationConfig;
    }

    public List<UpdateDto> getAllUpdates() {
        return updateRepository.findAll().stream().map(update -> updateMapper.toUpdateDto(update, false)).collect(Collectors.toList());
    }

    public Update getUpdateById(Integer id) {
        Optional<Update> update = updateRepository.findById(id);
        if (update.isPresent()) {
            return update.get();
        } else {
            throw new EntityNotFoundException("Update with id " + id + " not found");
        }
    }

    public UpdateDto putPost(String username, UpdatePostReqDto updatePostReqDto) {
        AppUser appUser = appUserService.getUserByUsername(username);

        Update returnedUpdate;

        if (updatePostReqDto.id() == null) {
            Update newUpdate = new Update(updatePostReqDto.content(), appUser);
            updateRepository.save(newUpdate);
            asyncAddUpdateToAllFollowersInbox(appUser, newUpdate);

            returnedUpdate = newUpdate;
        } else {
            Update update = getUpdateById(updatePostReqDto.id());
            update.setContent(updatePostReqDto.content());
            update.setIsEdited(true);
            update.setEditedAt(ZonedDateTime.now(ZoneId.of("UTC")));
            updateRepository.save(update);

            returnedUpdate = update;
        }
        return updateMapper.toUpdateDto(returnedUpdate, false);
    }

    public void saveUpdate(Update update) {
        updateRepository.save(update);
    }

    public void deleteUpdate(Update update) {
        updateRepository.delete(update);
    }

    public void deleteUpdateById(String username, Integer id) {
        AppUser user = appUserService.getUserByUsername(username);
        Update update = getUpdateById(id);

        if (update == null) {
            throw new EntityNotFoundException("Update with id " + id + " not found");
        }

        if (!update.getAppUser().equals(user)) {
            throw new AccessDeniedException("You do not have permission to delete this update");
        }

        updateRepository.delete(update);
    }

    @Async
    protected void asyncAddUpdateToAllFollowersInbox(AppUser user, Update update) {
        List<AppUser> followers = followService.getFollowersForUser(user);

        for (AppUser follower : followers) {
            List<Update> inbox = follower.getInbox();
            inbox.add(update);
            appUserService.saveUser(follower);
            notificationService.createNotificationFromUserAndTarget(user, follower,
                    ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.of(update));
        }
    }

    public List<UpdateDto> getUpdatesByUsername(String requesterUsername, String targetUsername) {
        AppUser targetAppUser = appUserService.getUserByUsername(targetUsername);

        validateAccess(requesterUsername, targetAppUser);

        return updateRepository.findAllByAppUser(targetAppUser).stream().map(update -> updateMapper.toUpdateDto(
                update, hasRequestingUsernameLikedUpdate(requesterUsername, update)
            )
        ).toList();
    }

    public List<UpdateDto> getUpdatesByUsernamePaginated(String requesterUsername, String targetUsername, String page) {
        AppUser targetAppUser = appUserService.getUserByUsername(targetUsername);
        AppUser requestingUser = appUserService.getUserByUsername(requesterUsername);

        validateAccess(requesterUsername, targetAppUser);

        Pageable pageable = PageRequest.of(Integer.parseInt(page), paginationConfig.getPageSize(), Sort.by("createdAt").descending());

        List<Update> updateList = updateRepository.findAllByAppUser(targetAppUser, pageable);

        return updateList.stream().map(update -> {
            boolean isLikedByRequestingUser = update.hasUserLiked(requestingUser);
            return updateMapper.toUpdateDto(update, isLikedByRequestingUser);
        }).toList();
    }


    public UpdateDto getUpdateDtoById(String requesterUsername, Integer id) {
        Update update = updateRepository.findById(id).orElseThrow();
        AppUser targetAppUser = update.getAppUser();

        validateAccess(requesterUsername, targetAppUser);

        boolean isUpdateLikedByRequestingUser = hasRequestingUsernameLikedUpdate(requesterUsername, update);

        return updateMapper.toUpdateDto(update, isUpdateLikedByRequestingUser);
    }

    public List<UpdateDto> getUpdatesFromInboxByUsername(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Update> inbox = user.getInbox();

        // Sort
        inbox.sort(Comparator.comparing(Update::getCreatedAt).reversed());

        return inbox.stream().map(update -> {
            boolean isLikedByRequestingUser = update.hasUserLiked(user);
            return updateMapper.toUpdateDto(update, isLikedByRequestingUser);
        }).toList();
    }

    public List<UpdateDto> getUpdatesFromInboxByUsernamePaginated(int page, String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Update> inbox = user.getInbox();

        int size = paginationConfig.getPageSize();

        // Sort
        inbox.sort(Comparator.comparing(Update::getCreatedAt).reversed());

        // Implement pagination with stream skip and limit, then map to UpdateDto
        return inbox.stream().skip((long) page * size).limit(size).map(update -> {
            boolean isLikedByRequestingUser = update.hasUserLiked(user);
            return updateMapper.toUpdateDto(update, isLikedByRequestingUser);
        }).toList();
    }

    private boolean hasRequestingUsernameLikedUpdate(String requesterUsername, Update update) {
        boolean isLikedByRequestingUser = false;

        if (update != null) {
            try {
                AppUser requestingUser = appUserService.getUserByUsername(requesterUsername);
                isLikedByRequestingUser = update.hasUserLiked(requestingUser);
            } catch (EntityNotFoundException ignored) {
                // User not found, therefore the user cannot have liked the update.
            }
        }

        return isLikedByRequestingUser;
    }

    private boolean validateAccess(String requesterUsername, AppUser targetAppUser) {
        if (targetAppUser.getPrivacySetting() == EPrivacySetting.PUBLIC) {
            return true;
        } else if (targetAppUser.getPrivacySetting() == EPrivacySetting.FOLLOWER) {
            try {
                AppUser requestingUser = appUserService.getUserByUsername(requesterUsername);

                if (requestingUser.equals(targetAppUser)) {
                    return true;
                }

                if (followService.getIsFollowing(requesterUsername, targetAppUser.getUsername())) {
                    return true;
                } else {
                    throw new AccessDeniedException("Target user's profile is for followers only");
                }
            } catch (EntityNotFoundException e) {
                throw new AccessDeniedException("Target user's profile is for followers only");
            }
        } else {
            try {
                AppUser requestingUser = appUserService.getUserByUsername(requesterUsername);
                if (requestingUser.equals(targetAppUser)) {
                    return true;
                } else {
                    throw new AccessDeniedException("Target user's profile is private");
                }
            } catch (Exception ignoredException) {

            }
            throw new AccessDeniedException("Target user's profile is private");
        }
    }
}
