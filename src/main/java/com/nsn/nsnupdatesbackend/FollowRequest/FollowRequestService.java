package com.nsn.nsnupdatesbackend.FollowRequest;

import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.UserNotFoundUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowRequestService {
    private final FollowRequestRepository followRequestRepository;
    private final AppUserService appUserService;
    private final FollowService followService;

    @Autowired
    public FollowRequestService(FollowRequestRepository followRequestRepository, AppUserService appUserService,
        FollowService followService) {
            this.followRequestRepository = followRequestRepository;
            this.appUserService = appUserService;
            this.followService = followService;
    }

    public void saveFollowRequest(String requesterUsername, String targetUsername) {
        FollowRequest followRequest = new FollowRequest();

        AppUser requester = appUserService.getUserByUsername(requesterUsername);
        AppUser target = appUserService.getUserByUsername(targetUsername);

        UserNotFoundUtil.throwIfRequesterAndTargetUserNotFound(requester, target);

        followRequest.setRequester(requester);
        followRequest.setTarget(target);
        followRequest.setStatus(EFollowRequestStatus.FOLLOW_PENDING);

        followRequestRepository.save(followRequest);
    }

    public void rejectFollowRequest() {

    }

    public void acceptFollowRequest() {

    }

    public void existsFollowRequest() {

    }
}
