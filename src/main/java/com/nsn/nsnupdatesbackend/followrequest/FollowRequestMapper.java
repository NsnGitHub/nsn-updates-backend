package com.nsn.nsnupdatesbackend.followrequest;

import org.springframework.stereotype.Service;

@Service
public class FollowRequestMapper {
    public FollowRequestDto toFollowRequestDto(FollowRequest req) {
        return new FollowRequestDto(req.getRequesterUsername(), req.getTargetUsername());
    }
}
