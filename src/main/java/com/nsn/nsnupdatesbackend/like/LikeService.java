package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notification.NotificationService;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.update.UpdateService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final AppUserService appUserService;
    private final UpdateService updateService;
    private final NotificationService notificationService;
    private final LikeMapper likeMapper;

    @Autowired
    public LikeService(LikeRepository likeRepository, AppUserService appUserService, UpdateService updateService,
                       NotificationService notificationService, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.appUserService = appUserService;
        this.updateService = updateService;
        this.notificationService = notificationService;
        this.likeMapper = likeMapper;
    }

    public LikeDto getLikeById(Integer id) {
        Like like = likeRepository.findLikeById(id);

        if (like == null) {
            throw new EntityNotFoundException(String.format("Like with id %s not found", id));
        }

        return likeMapper.toLikeDto(like);
    }

    private boolean isUpdateLikedByUser(AppUser user, Update update) {
        return likeRepository.existsByAppUserAndUpdate(user, update);
    }

    private boolean isUpdateMadeByUser(AppUser user, Update update) {
        return user == update.getAppUser();
    }

    public LikeDto like(String username, Integer updateId) {
        AppUser user = appUserService.getUserByUsername(username);
        Update update = updateService.getUpdateById(updateId);

        if (isUpdateLikedByUser(user, update)) {
            throw new EntityExistsException("User already liked this post");
        }

        if (isUpdateMadeByUser(user, update)) {
            throw new AccessDeniedException("User cannot like their own post");
        }

        Like like = new Like(user, update);
        likeRepository.save(like);

        notificationService.createNotificationFromUserAndTarget(user, update.getAppUser(),
                ENotificationType.NOTIFICATION_UPDATE_LIKED, Optional.of(update));

        return likeMapper.toLikeDto(like);
    }

    public void unlike(String username, Integer updateId) {
        AppUser user = appUserService.getUserByUsername(username);
        Update update = updateService.getUpdateById(updateId);

        if (isUpdateMadeByUser(user, update)) {
            throw new AccessDeniedException("User cannot unlike their own post");
        }

        if (!isUpdateLikedByUser(user, update)) {
            throw new EntityExistsException("User cannot unlike a post they did not like");
        }

        Like like = likeRepository.findLikeByAppUserAndUpdate(user, update);
        likeRepository.delete(like);
    }
}
