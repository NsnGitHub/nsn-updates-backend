package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.followrequest.FollowRequestDto;
import com.nsn.nsnupdatesbackend.followrequest.FollowRequestService;
import com.nsn.nsnupdatesbackend.user.AppUserDto;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/follow")
public class FollowController {

    private final FollowService followService;
    private final FollowRequestService followRequestService;

    @Autowired
    public FollowController(FollowService followService, FollowRequestService followRequestService) {
        this.followService = followService;
        this.followRequestService = followRequestService;
    }

    @GetMapping("/followers")
    public ResponseEntity<List<AppUserDto>> getFollowers(Principal principal) {
        return ResponseEntity.ok().body(followService.getFollowersDtoForUsername(principal.getName()));
    }

    @GetMapping("/following")
    public ResponseEntity<List<AppUserDto>> getFollowing(Principal principal) {
        return ResponseEntity.ok().body(followService.getFollowingDtoForUsername(principal.getName()));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> unfollow(Principal principal, @Valid @RequestBody FollowRequestDto followRequestDto) {
        followService.unfollowFromUsername(principal.getName(), followRequestDto.targetUsername(), false);
        followRequestService.deleteFollowRequest(principal.getName(), followRequestDto.targetUsername());
        return ResponseEntity.ok().build();
    }
}
