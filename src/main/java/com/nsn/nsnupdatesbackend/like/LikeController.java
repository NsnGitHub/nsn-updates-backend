package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;
    private final AppUserService appUserService;
    private final JWTUtils jwtUtils;

    @Autowired
    public LikeController(LikeService likeService, AppUserService appUserService, JWTUtils jwtUtils) {
        this.likeService = likeService;
        this.appUserService = appUserService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<?> like(@RequestHeader("Authorization") String token, @RequestBody LikeReqDto likeReqDto) {
        String jwtToken = token.substring(7);
        String username = jwtUtils.getUsername(jwtToken);
        likeService.like(username, likeReqDto.updateId());

        return ResponseEntity.ok().build();
    }
//
//    @PostMapping
//    public ResponseEntity<?> unLike(@RequestBody LikeReqDto likeReqDto){
//        return ResponseEntity.ok().build();
//    }

    @GetMapping ResponseEntity<?> getLikeById(@RequestParam Integer id) {
        return ResponseEntity.ok().body(likeService.getLikeById(id));
    }

}
