package com.nsn.nsnupdatesbackend.auth;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.exception.APIException;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final AppUserService userService;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(JWTUtils jwtUtils, AppUserService userService, AuthenticationManager authenticationManager) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    public AuthDto login(String username, String password) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password
        );
        Authentication authRes = authenticationManager.authenticate(authRequest);
        User user = (User) authRes.getPrincipal();

        String accessToken = jwtUtils.createToken(user.getUsername(), EJwtToken.ACCESS_TOKEN);
        String refreshToken = jwtUtils.createToken(user.getUsername(), EJwtToken.REFRESH_TOKEN);

        return new AuthDto(accessToken, refreshToken);
    }

    public AuthDto handleRefreshToken(String refreshToken, HttpServletRequest request) throws APIException {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            String jwtToken = refreshToken.substring(7);

            if (!jwtUtils.isRefreshToken(jwtToken)) {
                throw new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED, "Invalid Token");
            }
            String username = jwtUtils.getUsername(jwtToken);
            AppUser user = userService.getUserByUsername(username);

            if (user == null) {
                throw new EntityNotFoundException("User not found");
            }

            String newAccessToken = jwtUtils.createToken(user.getUsername(), EJwtToken.ACCESS_TOKEN);

            return new AuthDto(newAccessToken, jwtToken);
        } else {
            throw new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST, "Invalid Token");
        }
    }
}
