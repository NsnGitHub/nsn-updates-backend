package com.nsn.nsnupdatesbackend.auth;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.user.User;
import com.nsn.nsnupdatesbackend.user.UserDto;
import com.nsn.nsnupdatesbackend.user.UserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.naming.AuthenticationException;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JWTUtils jwtUtils;
    private final UserService userService;

    @Autowired
    public AuthController(JWTUtils jwtUtils, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
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
