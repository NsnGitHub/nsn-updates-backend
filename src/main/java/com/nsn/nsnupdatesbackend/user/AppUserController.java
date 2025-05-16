package com.nsn.nsnupdatesbackend.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @GetMapping(path = "/{username}")
    public ResponseEntity<AppUserDto> getUserDetails(@PathVariable("username") String targetUsername) {
        return ResponseEntity.ok().body(userService.getUserDtoByUsername(targetUsername));
    }

    @GetMapping(path = "/search/{usernameCriteria}")
    public ResponseEntity<List<AppUserDto>> getUserWithUsernameContaining(@PathVariable("usernameCriteria") String targetUsernameCriteria, Principal principal) {
        return ResponseEntity.ok().body(userService.getUserDtoListByUsernameSearch(targetUsernameCriteria, principal.getName()));
    }

    @PostMapping("/privacy")
    public ResponseEntity<?> updateUserPrivacySetting(Principal principal, @Valid @RequestBody AppUserDto appUserDto) {
        userService.updatePrivacySetting(principal.getName(), appUserDto.privacySetting());
        return ResponseEntity.ok().build();
    }

}
