package com.nsn.nsnupdatesbackend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/user")
public class AppUserController {
    private final AppUserService userService;

    @Autowired
    public AppUserController(AppUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<AppUserDto> getUserDetails(Principal principal) {

        // Implement when user profile details figured out.

        return ResponseEntity.ok().build();
    }

}
