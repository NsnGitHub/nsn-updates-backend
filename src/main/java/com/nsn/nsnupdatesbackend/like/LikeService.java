package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.update.UpdateService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final AppUserService appUserService;
    private final UpdateService updateService;

    @Autowired
    public LikeService(LikeRepository likeRepository, AppUserService appUserService, UpdateService updateService) {
        this.likeRepository = likeRepository;
        this.appUserService = appUserService;
        this.updateService = updateService;
    }

    public Like getLikeById(Integer id) {
        return likeRepository.findLikeById(id);
    }

    private boolean isUpdateLikedByUser(AppUser user, Update update) {
        return likeRepository.existsByAppUserAndUpdate(user, update);
    }

    private boolean isUpdateMadeByUser(AppUser user, Update update) {
        return user == update.getAppUser();
    }

    public void like(String username, Integer updateId) {
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
    }
}
