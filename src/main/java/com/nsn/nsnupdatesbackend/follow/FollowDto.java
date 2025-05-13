package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;

public record FollowDto(
        EFollowRequestStatus status
) {
}
