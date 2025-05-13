package com.nsn.nsnupdatesbackend.followrequest;

import com.nsn.nsnupdatesbackend.follow.FollowDto;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> request(Principal principal, @Valid @RequestBody FollowRequestDto followRequestDto) throws BadRequestException {
        followRequestService.createFollowRequest(principal.getName(), followRequestDto.targetUsername(), true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptRequest(Principal principal, @Valid @RequestBody FollowRequestDto followRequestDto) {
        followRequestService.acceptFollowRequest(followRequestDto.requesterUsername(), principal.getName(), true);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/reject")
    public ResponseEntity<?> rejectRequest(Principal principal, @Valid @RequestBody FollowRequestDto followRequestDto) {
        followRequestService.rejectFollowRequest(followRequestDto.requesterUsername(), principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getPendingRequests(Principal principal) {
        return ResponseEntity.ok(followRequestService.getAllPendingFollowRequests(principal.getName()));
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<?> getIsFollowing(Principal principal, @PathVariable("username") String targetUsername) {
        return ResponseEntity.ok().body(new FollowDto(followRequestService.getStatus(principal.getName(), targetUsername)));
    }
}
