package com.nsn.nsnupdatesbackend.followrequest;

import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
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

    public void saveFollowRequest(String requesterUsername, String targetUsername) throws BadRequestException {
        AppUser requester = appUserService.getUserByUsername(requesterUsername);
        AppUser target = appUserService.getUserByUsername(targetUsername);

        if (requester == target) {
            throw new BadRequestException("User cannot follow themself");
        }

        if (target.getPrivacySetting() == EPrivacySetting.PUBLIC) {
            followService.followFromAppUser(requester, target, true);

            return;
        }

        if (target.getPrivacySetting() == EPrivacySetting.PRIVATE) {
            throw new BadRequestException("Target user's privacy setting is on private");
        }

        if (followRequestRepository.existsFollowRequestByRequesterAndTargetAndStatus(requester, target,
                EFollowRequestStatus.FOLLOW_PENDING)) {
            throw new EntityExistsException("Request has already been made to target");
        }

        if (followRequestRepository.countFollowRequestsByRequesterAndTargetAndStatus(requester, target,
                EFollowRequestStatus.FOLLOW_REJECTED) >= 1) {
            // Hide fact that user's request has already been declined
            throw new EntityExistsException("Request has already been made to target");
        }

        FollowRequest followRequest = new FollowRequest();
        followRequest.setRequester(requester);
        followRequest.setTarget(target);
        followRequest.setStatus(EFollowRequestStatus.FOLLOW_PENDING);

        followRequestRepository.save(followRequest);

        notificationService.createNotificationFromUserAndTarget(requester, target,
                ENotificationType.NOTIFICATION_FOLLOW_REQUEST);
    }

    public void rejectFollowRequest(String requesterUsername, String targetUsername) {
        FollowRequest followRequest = getFollowRequest(requesterUsername, targetUsername);
        followRequest.setStatus(EFollowRequestStatus.FOLLOW_REJECTED);

        followRequestRepository.save(followRequest);
    }

    public void acceptFollowRequest(String requesterUsername, String targetUsername) {
        FollowRequest followRequest = getFollowRequest(requesterUsername, targetUsername);
        followRequest.setStatus(EFollowRequestStatus.FOLLOW_ACCEPTED);
        followRequestRepository.save(followRequest);
        followService.followFromUsername(requesterUsername, targetUsername, true);
    }

    private FollowRequest getFollowRequest(String requesterUsername, String targetUsername) {
        AppUser requester = appUserService.getUserByUsername(requesterUsername);
        AppUser target = appUserService.getUserByUsername(targetUsername);

        UserNotFoundUtil.throwIfRequesterAndTargetUserNotFound(requester, target);

        FollowRequest followRequest = followRequestRepository.getFollowRequestByRequesterAndTarget(requester, target);

        if (followRequest == null) {
            throw new EntityNotFoundException("Follow request does not exist for requesting user and target user.");
        }

        return followRequest;
    }
}
