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

    @PostMapping("/{id}")
    public ResponseEntity<?> like(Principal principal, @PathVariable("id") Integer id) {
        likeService.like(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> unlike(Principal principal, @PathVariable("id") Integer id) {
        likeService.unlike(principal.getName(), id);
        return ResponseEntity.ok().build();
    }
}
