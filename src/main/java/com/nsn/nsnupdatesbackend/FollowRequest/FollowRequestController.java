package com.nsn.nsnupdatesbackend.FollowRequest;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/follow")
public class FollowRequestController {

    private final FollowRequestService followRequestService;

    @Autowired
    public FollowRequestController(FollowRequestService followRequestService) {
        this.followRequestService = followRequestService;
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(Principal principal, @RequestBody FollowRequestDto followRequestDto) {
        followRequestService.saveFollowRequest(principal.getName(), followRequestDto.targetUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/respond")
    public void respond() {

    }
}
