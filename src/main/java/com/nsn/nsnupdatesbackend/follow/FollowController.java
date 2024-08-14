package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.user.AppUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/follow")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping("/followers")
    public ResponseEntity<List<AppUserDto>> getFollowers(Principal principal) {
        return ResponseEntity.ok().body(followService.getFollowersDtoForUsername(principal.getName()));
    }

    @GetMapping("/following")
    public ResponseEntity<List<AppUserDto>> getFollowing(Principal principal) {
        return ResponseEntity.ok().body(followService.getFollowingDtoForUsername(principal.getName()));
    }

}
