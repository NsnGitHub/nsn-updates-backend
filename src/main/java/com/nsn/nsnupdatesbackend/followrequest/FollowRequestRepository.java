package com.nsn.nsnupdatesbackend.followrequest;

import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Integer> {
    FollowRequest getFollowRequestByRequesterAndTarget(AppUser requester, AppUser target);
    boolean existsFollowRequestByRequesterAndTargetAndStatus(AppUser requester, AppUser target,
                                                             EFollowRequestStatus status);
    int countFollowRequestsByRequesterAndTargetAndStatus(AppUser requester, AppUser target,
                                                         EFollowRequestStatus status);
}
