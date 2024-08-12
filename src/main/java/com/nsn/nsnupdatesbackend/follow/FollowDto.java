package com.nsn.nsnupdatesbackend.follow;

public record FollowDto(
        String followerUsername,
        String followeeUsername
) {
}
