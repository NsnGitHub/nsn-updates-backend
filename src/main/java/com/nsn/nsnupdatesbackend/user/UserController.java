package com.nsn.nsnupdatesbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody UserDto user) {
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
