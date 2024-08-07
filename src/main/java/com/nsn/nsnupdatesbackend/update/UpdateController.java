package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.auth.AuthService;
import com.nsn.nsnupdatesbackend.exception.APIException;
import com.nsn.nsnupdatesbackend.user.*;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "api/v1/update")
public class UpdateController {

    private final UpdateService updateService;
    private final AppUserService appUserService;
    private final JWTUtils jwtUtils;

    @Autowired
    public UpdateController(UpdateService updateService, AppUserService appUserService, JWTUtils jwtUtils) {
        this.updateService = updateService;
        this.appUserService = appUserService;
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
        AppUser user = appUserService.getUserByUsername(username);
        updateService.savePost(updatePostReqDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
