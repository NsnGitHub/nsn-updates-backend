package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notification.NotificationService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserDto;
import com.nsn.nsnupdatesbackend.user.AppUserMapper;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.UserNotFoundUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final AppUserService appUserService;
    private final AppUserMapper appUserMapper;
    private final NotificationService notificationService;

    @Autowired
    public FollowService(FollowRepository followRepository, AppUserService appUserService,
                         AppUserMapper appUserMapper, NotificationService notificationService) {
        this.followRepository = followRepository;
        this.appUserService = appUserService;
        this.appUserMapper = appUserMapper;
        this.notificationService = notificationService;
    }

    public void follow(String followerUsername, String followeeUsername) {
        AppUser follower = appUserService.getUserByUsername(followerUsername);
        AppUser followee = appUserService.getUserByUsername(followeeUsername);

        UserNotFoundUtil.throwIfRequesterAndTargetUserNotFound(follower, followee);

        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);

        notificationService.createNotificationFromUserAndTarget(followee, follower,
                ENotificationType.NOTIFICATION_FOLLOW_ACCEPTED);
    }

    public List<AppUserDto> getFollowersDtoForUsername(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Follow> followers = followRepository.findByFollowee(user);

        return followers.stream().map(Follow::getFollower).map(appUserMapper::toUserDto).toList();
    }

    public List<AppUserDto> getFollowingDtoForUsername(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Follow> following = followRepository.findByFollower(user);

        return following.stream().map(Follow::getFollowee).map(appUserMapper::toUserDto).toList();
    }

    public List<AppUser> getFollowersForUser(AppUser user) {
        List<Follow> followers = followRepository.findByFollowee(user);
        return followers.stream().map(Follow::getFollower).toList();
    }
}
