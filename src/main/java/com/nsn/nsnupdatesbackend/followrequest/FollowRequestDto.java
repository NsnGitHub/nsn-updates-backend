package com.nsn.nsnupdatesbackend.followrequest;

public record FollowRequestDto(
        String requesterUsername,
        String targetUsername
) {
}
