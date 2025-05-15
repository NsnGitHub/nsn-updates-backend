package com.nsn.nsnupdatesbackend.auth;

import com.nsn.nsnupdatesbackend.exception.APIException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody AuthLogInReqDto logInReq, HttpServletResponse response) {
        authService.login(logInReq.username(), logInReq.password(), response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login/guest")
    public ResponseEntity<?> signInGuest(HttpServletResponse response) {
        authService.guestLogin(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request,
                                          HttpServletResponse response) throws APIException {
        authService.handleRefreshToken(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping(Principal principal) {
        return ResponseEntity.ok().body(authService.ping(principal.getName()));
    }
}
