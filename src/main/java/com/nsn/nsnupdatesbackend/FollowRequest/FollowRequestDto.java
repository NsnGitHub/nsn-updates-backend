package com.nsn.nsnupdatesbackend.FollowRequest;

public record FollowRequestDto(
        String requesterUsername,
        String targetUsername
) {
}
