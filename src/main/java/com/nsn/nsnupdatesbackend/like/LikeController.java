package com.nsn.nsnupdatesbackend.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public ResponseEntity<?> like(Principal principal, @RequestBody LikeReqDto likeReqDto) {
        likeService.like(principal.getName(), likeReqDto.updateId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> unlike(Principal principal, @RequestBody LikeReqDto likeReqDto) {
        likeService.unlike(principal.getName(), likeReqDto.updateId());
        return ResponseEntity.ok().build();
    }
}
