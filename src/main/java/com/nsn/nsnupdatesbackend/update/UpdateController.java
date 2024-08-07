package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping(path = "api/v1/update")
public class UpdateController {

    private final UpdateService updateService;
    private final JWTUtils jwtUtils;

    @Autowired
    public UpdateController(UpdateService updateService, JWTUtils jwtUtils) {
        this.updateService = updateService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<UpdateDto>> getAllUpdates() {
        return ResponseEntity.ok().body(updateService.getAllUpdates());
    }

    @PostMapping
    public ResponseEntity<?> postUpdate(@RequestHeader("Authorization") String token, @RequestBody UpdatePostReqDto updatePostReqDto, HttpServletRequest request) {
        String jwtToken = token.substring(7);
        String username = jwtUtils.getUsername(jwtToken);
        updateService.savePost(username, updatePostReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
