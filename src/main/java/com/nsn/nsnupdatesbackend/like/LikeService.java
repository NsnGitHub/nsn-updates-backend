package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.update.UpdateService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void like(String username, Integer updateId) {
        AppUser user = appUserService.getUserByUsername(username);
        Update update = updateService.getUpdateById(updateId);

        Like like = new Like(user, update);
        likeRepository.save(like);
    }
}
