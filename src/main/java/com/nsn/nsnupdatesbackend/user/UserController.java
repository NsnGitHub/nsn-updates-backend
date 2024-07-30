package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.PrivacySetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public void registerUser(@RequestBody UserDto user) {
        userService.saveUser(user);
    }
}
