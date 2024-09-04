package com.nsn.nsnupdatesbackend.followrequest;

import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.follow.Follow;
import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.notification.NotificationService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.UserNotFoundUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowRequestService {
    private final FollowRequestRepository followRequestRepository;
    private final AppUserService appUserService;
    private final FollowService followService;
    private final NotificationService notificationService;

    @Autowired
    public FollowRequestService(FollowRequestRepository followRequestRepository, AppUserService appUserService,
        FollowService followService, NotificationService notificationService) {
            this.followRequestRepository = followRequestRepository;
            this.appUserService = appUserService;
            this.followService = followService;
            this.notificationService = notificationService;
    }

    public void createFollowRequest(String requesterUsername, String targetUsername, boolean notify) throws BadRequestException {
        AppUser requester = appUserService.getUserByUsername(requesterUsername);
        AppUser target = appUserService.getUserByUsername(targetUsername);

        if (requester == target) {
            throw new BadRequestException("User cannot follow themself");
        }

        if (target.getPrivacySetting() == EPrivacySetting.PUBLIC) {
            followService.followFromAppUser(requester, target, notify);

            return;
        }

        if (target.getPrivacySetting() == EPrivacySetting.PRIVATE) {
            throw new BadRequestException("Target user's privacy setting is on private");
        }

        if (followRequestRepository.existsFollowRequestByRequesterAndTargetAndStatus(requester, target,
                EFollowRequestStatus.FOLLOW_ACCEPTED)) {
            throw new EntityExistsException("Already following target user");
        }

        // Do not allow spam of follow requests
        if (followRequestRepository.countFollowRequestsByRequesterAndTargetAndStatus(requester, target,
                EFollowRequestStatus.FOLLOW_REJECTED) >= 2) {
            // Hide fact that user's request has already been declined
            throw new EntityExistsException("Request has already been made to target user");
        }

        if (followRequestRepository.existsFollowRequestByRequesterAndTargetAndStatus(requester, target,
                EFollowRequestStatus.FOLLOW_PENDING)) {
            throw new EntityExistsException("Request has already been made to target user");
        }

        FollowRequest followRequest = new FollowRequest();
        followRequest.setRequester(requester);
        followRequest.setTarget(target);
        followRequest.setStatus(EFollowRequestStatus.FOLLOW_PENDING);

        followRequestRepository.save(followRequest);

        if (notify) {
            notificationService.createNotificationFromUserAndTarget(requester, target,
                    ENotificationType.NOTIFICATION_FOLLOW_REQUEST);
        }
    }

    public void rejectFollowRequest(String requesterUsername, String targetUsername) {
        FollowRequest followRequest = getPendingFollowRequest(requesterUsername, targetUsername);
        followRequest.setStatus(EFollowRequestStatus.FOLLOW_REJECTED);

        followRequestRepository.save(followRequest);
    }

    public void acceptFollowRequest(String requesterUsername, String targetUsername, boolean notify) {
        FollowRequest followRequest = getPendingFollowRequest(requesterUsername, targetUsername);
        followRequest.setStatus(EFollowRequestStatus.FOLLOW_ACCEPTED);

        followRequestRepository.save(followRequest);
        followService.followFromUsername(requesterUsername, targetUsername, notify);
    }

    private FollowRequest getPendingFollowRequest(String requesterUsername, String targetUsername) {
        AppUser requester = appUserService.getUserByUsername(requesterUsername);
        AppUser target = appUserService.getUserByUsername(targetUsername);

        FollowRequest followRequest = followRequestRepository.getFollowRequestByRequesterAndTargetAndStatus(
                requester, target, EFollowRequestStatus.FOLLOW_PENDING
        );

        if (followRequest == null) {
            throw new EntityNotFoundException("Pending follow request not found between requester and target.");
        }

        return followRequest;
    }
}
