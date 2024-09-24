package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.notification.NotificationService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    public UpdateService(UpdateRepository updateRepository, UpdateMapper updateMapper, AppUserService appUserService,
                         FollowService followService, NotificationService notificationService) {
            this.updateRepository = updateRepository;
            this.updateMapper = updateMapper;
            this.appUserService = appUserService;
            this.followService = followService;
            this.notificationService = notificationService;
    }

    public List<UpdateDto> getAllUpdates() {
        return updateRepository.findAll().stream().map(updateMapper::toUpdateDto).collect(Collectors.toList());
    }

    public Update getUpdateById(Integer id) {
        Optional<Update> update = updateRepository.findById(id);
        if (update.isPresent()) {
            return update.get();
        } else {
            throw new EntityNotFoundException("Update with id " + id + " not found");
        }
    }

    public UpdateDto getUpdateDtoById(Integer id) {
        Optional<Update> update = updateRepository.findById(id);
        if (update.isPresent()) {
            return updateMapper.toUpdateDto(update.get());
        } else {
            throw new EntityNotFoundException("Update with id " + id + " not found");
        }
    }

    public UpdateDto createPost(String username, UpdatePostReqDto updatePostReqDto) {
        AppUser appUser = appUserService.getUserByUsername(username);
        Update newUpdate = new Update(updatePostReqDto.content(), appUser);

        updateRepository.save(newUpdate);

        asyncAddUpdateToAllFollowersInbox(appUser, newUpdate);

        return updateMapper.toUpdateDto(newUpdate);
    }

    public void saveUpdate(Update update) {
        updateRepository.save(update);
    }

    public UpdateDto editUpdate(UpdateDto updateDto, UpdatePostReqDto updatePostReqDto) {
        Update update = getUpdateById(updateDto.id());
        update.setIsEdited(true);
        update.setContent(updatePostReqDto.content());
        saveUpdate(update);

        return updateMapper.toUpdateDto(update);
    }

    public void deleteUpdate(Update update) {
        updateRepository.delete(update);
    }

    public void deleteUpdateById(Integer id) {
        Update update = getUpdateById(id);
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
                    ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        }
    }

    public List<UpdateDto> getUpdatesByUsername(String requesterUsername, String targetUsername) {
        AppUser targetAppUser = appUserService.getUserByUsername(targetUsername);

        if (targetAppUser.getPrivacySetting() == EPrivacySetting.PUBLIC) {
            return updateRepository.findAllByAppUser(targetAppUser).stream().map(updateMapper::toUpdateDto).toList();
        } else if (targetAppUser.getPrivacySetting() == EPrivacySetting.PRIVATE) {
            throw new AccessDeniedException("Target user has a private profile");
        } else {
            // Only privacy setting left is if their profile is on following.
            if (followService.getIsFollowing(requesterUsername, targetAppUser.getUsername())) {
                return updateRepository.findAllByAppUser(targetAppUser).stream().map(updateMapper::toUpdateDto).toList();
            } else {
                throw new AccessDeniedException("You are not a follower of the target user");
            }
        }
    }

    public List<UpdateDto> getUpdatesFromInboxByUsername(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Update> inbox = user.getInbox();

        // Sort
        inbox.sort(Comparator.comparing(Update::getCreatedAt).reversed());

        return inbox.stream().map(updateMapper::toUpdateDto).toList();
    }

    public List<UpdateDto> getUpdatesFromInboxByUsernamePaginated(int page, int size, String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Update> inbox = user.getInbox();

        // Sort
        inbox.sort(Comparator.comparing(Update::getCreatedAt).reversed());

        // Implement pagination with stream skip and limit, then map to UpdateDto
        return inbox.stream().skip((long) page * size).limit(size).map(updateMapper::toUpdateDto).toList();
    }
}
