package com.nsn.nsnupdatesbackend.auth;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import com.nsn.nsnupdatesbackend.user.UserDto;
import com.nsn.nsnupdatesbackend.user.UserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JWTUtils jwtUtils;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JWTUtils jwtUtils, UserService userService, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody AuthLogInReq logInReq) {
        try {
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(logInReq.username(), logInReq.password());
            Authentication authRes = authenticationManager.authenticate(authRequest);
            User user = (User) authRes.getPrincipal();

            String accessToken = jwtUtils.createToken(user.getUsername(), EJwtToken.ACCESS_TOKEN);
            String refreshToken = jwtUtils.createToken(user.getUsername(), EJwtToken.REFRESH_TOKEN);

            System.out.println(accessToken);
            System.out.println(refreshToken);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/refresh")
    public void refreshToken(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwtToken = token.substring(7);
                String username = jwtUtils.getUsername(jwtToken);

                UserDto user = userService.getUserByUsername(username);

                String newAccessToken = jwtUtils.createToken(user.username(), EJwtToken.ACCESS_TOKEN);

                response.setHeader("Access-Token", "Bearer " + newAccessToken);
                response.setHeader("Refresh-Token", token);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Bad Token");
            }
        } else {
            System.out.println("No Token");
        }
    }
}
