package com.nsn.nsnupdatesbackend.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.enums.EUserRole;
import com.nsn.nsnupdatesbackend.exception.APIException;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;

    public JWTAuthorizationFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/auth/login")
                || request.getServletPath().equals("/api/v1/auth/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorization = request.getHeader(AUTHORIZATION);
            String token = null;

            boolean isAccessCookieFound = false;

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    System.out.println(cookie.getName() + ": " + cookie.getValue());
                    if (cookie.getName().equals(EJwtToken.ACCESS_TOKEN.toString())) {
                        token = cookie.getValue();
                    }
                }
            }

            // For Controller tests, as I generate a JWT token to include within the Authorization header.
            if (!isAccessCookieFound) {
                if (authorization != null && authorization.startsWith("Bearer ")) {
                    token = authorization.substring(7);
                }
            }

            if (token != null) {
                try {
                    if (!jwtUtils.isAccessToken(token)) {
                        throw new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED, "Invalid Token");
                    }

                    String username = jwtUtils.getUsername(token);

                    if (jwtUtils.isGuestToken(token)) {
                        System.out.println("GUEST TOKEN " + token);

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                                UsernamePasswordAuthenticationToken(username, null, List.of(EUserRole.ROLE_GUEST));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                        filterChain.doFilter(request, response);
                        return;
                    }

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                            UsernamePasswordAuthenticationToken(username, null, List.of(EUserRole.ROLE_USER));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    handleException(e, request, response);
                }
            } else {
                handleException(new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED, "Invalid Token"), request, response);
            }
        }
    }

    private void handleException(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(401);
        response.setContentType(APPLICATION_JSON_VALUE);
        APIException apiException = new APIException(
            request.getServletPath(), HttpStatus.UNAUTHORIZED, "Invalid Token"
        );
        new ObjectMapper().registerModule(new JavaTimeModule()).writeValue(response.getOutputStream(), apiException);
    }
}
