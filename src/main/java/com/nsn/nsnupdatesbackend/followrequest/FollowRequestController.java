package com.nsn.nsnupdatesbackend.followrequest;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/follow/request")
public class FollowRequestController {

    private final FollowRequestService followRequestService;

    @Autowired
    public FollowRequestController(FollowRequestService followRequestService) {
        this.followRequestService = followRequestService;
    }

    @PostMapping
    public ResponseEntity<?> request(Principal principal, @RequestBody FollowRequestDto followRequestDto) throws BadRequestException {
        followRequestService.saveFollowRequest(principal.getName(), followRequestDto.targetUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptRequest(Principal principal, @RequestBody FollowRequestDto followRequestDto) {
        followRequestService.acceptFollowRequest(followRequestDto.requesterUsername(), principal.getName());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/reject")
    public ResponseEntity<?> rejectRequest(Principal principal, @RequestBody FollowRequestDto followRequestDto) {
        followRequestService.rejectFollowRequest(followRequestDto.requesterUsername(), principal.getName());
        return ResponseEntity.ok().build();
    }
}
