package com.nsn.nsnupdatesbackend.auth;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.enums.EUserRole;
import com.nsn.nsnupdatesbackend.exception.APIException;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;


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

    public void login(String username, String password, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
            username, password
        );
        Authentication authRes = authenticationManager.authenticate(authRequest);
        User user = (User) authRes.getPrincipal();

        String accessToken = jwtUtils.createToken(user.getUsername(), EJwtToken.ACCESS_TOKEN, EUserRole.ROLE_USER);
        String refreshToken = jwtUtils.createToken(user.getUsername(), EJwtToken.REFRESH_TOKEN, EUserRole.ROLE_USER);

        Cookie accessCookie = createHttpOnlyCookie(EJwtToken.ACCESS_TOKEN.toString(), accessToken, jwtUtils.getJwtTokenDuration(EJwtToken.ACCESS_TOKEN));
        Cookie refreshCookie = createHttpOnlyCookie(EJwtToken.REFRESH_TOKEN.toString(), refreshToken, jwtUtils.getJwtTokenDuration(EJwtToken.REFRESH_TOKEN));

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public void guestLogin(HttpServletResponse response) {
        AppUser user = new AppUser();
        user.setUsername("guest" + UUID.randomUUID());

        String accessToken = jwtUtils.createToken(user.getUsername(), EJwtToken.ACCESS_TOKEN, EUserRole.ROLE_GUEST);
        Cookie accessCookie = createHttpOnlyCookie(EJwtToken.ACCESS_TOKEN.toString(), accessToken, jwtUtils.getJwtTokenDuration(EJwtToken.ACCESS_TOKEN));

        response.addCookie(accessCookie);
    }

    public void handleRefreshToken(HttpServletRequest request, HttpServletResponse response) throws APIException {
        Cookie[] cookies = request.getCookies();
        String jwtToken = null;

        if (cookies == null) {
            throw new APIException(request.getServletPath(), HttpStatus.BAD_REQUEST, "Invalid Token");
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(EJwtToken.REFRESH_TOKEN.toString())) {
                jwtToken = cookie.getValue();
            }
        }

        if (!jwtUtils.isRefreshToken(jwtToken)) {
            throw new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED, "Invalid Token");
        }
        String username = jwtUtils.getUsername(jwtToken);
        AppUser user = userService.getUserByUsername(username);

        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        String newAccessToken = jwtUtils.createToken(user.getUsername(), EJwtToken.ACCESS_TOKEN, EUserRole.ROLE_USER);

        Cookie accessCookie = createHttpOnlyCookie(EJwtToken.ACCESS_TOKEN.toString(), newAccessToken, jwtUtils.getJwtTokenDuration(EJwtToken.ACCESS_TOKEN));

        response.addCookie(accessCookie);
    }

    private Cookie createHttpOnlyCookie(String cookieName, String jwtToken, long cookieAge) {
        Cookie cookie = new Cookie(cookieName, jwtToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");

        if (cookieAge > Integer.MAX_VALUE) {
            cookieAge = Integer.MAX_VALUE;
        }

        cookie.setMaxAge((int) cookieAge);

        return cookie;
    }
}
