package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.UserNotFoundUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final AppUserService appUserService;

    @Autowired
    public FollowService(FollowRepository followRepository, AppUserService appUserService) {
        this.followRepository = followRepository;
        this.appUserService = appUserService;
    }

    public void follow(String followerUsername, String followeeUsername) {
        AppUser follower = appUserService.getUserByUsername(followerUsername);
        AppUser followee = appUserService.getUserByUsername(followeeUsername);

        UserNotFoundUtil.throwIfRequesterAndTargetUserNotFound(follower, followee);

        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);
    }
}
