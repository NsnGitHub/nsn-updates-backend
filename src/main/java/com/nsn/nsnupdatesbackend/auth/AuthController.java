package com.nsn.nsnupdatesbackend.auth;

import com.nsn.nsnupdatesbackend.exception.APIException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto> signIn(@RequestBody AuthLogInReqDto logInReq) {
        return ResponseEntity.ok().body(authService.login(logInReq.username(), logInReq.password()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDto> refreshToken(@RequestHeader("Authorization") String token,
                                                HttpServletRequest request) throws APIException {
        return ResponseEntity.ok().body(authService.handleRefreshToken(token, request));
    }
}
