package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notification.NotificationService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserDto;
import com.nsn.nsnupdatesbackend.user.AppUserMapper;
import com.nsn.nsnupdatesbackend.user.AppUserService;
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

    public void followFromUsername(String followerUsername, String followeeUsername, boolean notify) {
        AppUser follower = appUserService.getUserByUsername(followerUsername);
        AppUser followee = appUserService.getUserByUsername(followeeUsername);

        follow(follower, followee);

        if (notify) {
            notificationService.createNotificationFromUserAndTarget(followee, follower,
                    ENotificationType.NOTIFICATION_FOLLOW_ACCEPTED);
        }

    }

    // Creating follow object from AppUser objects means the followees privacy setting was on public
    // so no further logic was needed.
    public void followFromAppUser(AppUser follower, AppUser followee, boolean notify) {
        follow(follower, followee);

        if (notify) {
            notificationService.createNotificationFromUserAndTarget(follower, followee,
                    ENotificationType.NOTIFICATION_FOLLOW_PUBLIC);
        }
    }

    public void unfollowFromUsername(String followerUsername, String followeeUsername, boolean notify) {
        AppUser follower = appUserService.getUserByUsername(followerUsername);
        AppUser followee = appUserService.getUserByUsername(followeeUsername);

        unfollow(follower, followee);
    }

    public void unfollowFromAppUser(AppUser follower, AppUser followee) {
        unfollow(follower, followee);
    }

    private void follow(AppUser follower, AppUser followee) {
        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);
    }

    private void unfollow(AppUser follower, AppUser followee) {
        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee);

        if (follow != null) {
            followRepository.delete(follow);
        }

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

    public boolean getIsFollowing(String requesterUsername, String targetUsername) {
        AppUser requester = appUserService.getUserByUsername(requesterUsername);
        AppUser target = appUserService.getUserByUsername(targetUsername);

        return followRepository.existsFollowByFollowerAndFollowee(requester, target);
    }
}
