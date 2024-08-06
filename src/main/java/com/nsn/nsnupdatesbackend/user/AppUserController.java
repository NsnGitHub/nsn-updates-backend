package com.nsn.nsnupdatesbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class AppUserController {
    private final AppUserService userService;

    @Autowired
    public AppUserController(AppUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AppUserDto>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    public ResponseEntity<AppUserDto> getUserDtoByUsername(String username) {
        return ResponseEntity.ok().body(userService.getUserDtoByUsername(username));
    }
}
