package com.nsn.nsnupdatesbackend.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nsn.nsnupdatesbackend.exception.APIException;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final JWTUtils jwtUtils;

    public JWTAuthorizationFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/auth/login") || request.getServletPath().equals("/api/v1/auth/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorization = request.getHeader(AUTHORIZATION);
            System.out.println(authorization);
            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                try {
                    String username = jwtUtils.getUsername(token);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    handleException(e, request, response);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    private void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType(APPLICATION_JSON_VALUE);
        APIException apiException = new APIException(request.getServletPath(), HttpStatus.UNAUTHORIZED, e.getMessage(),
            ZonedDateTime.now(ZoneId.of("UTC")));
        new ObjectMapper().registerModule(new JavaTimeModule()).writeValue(response.getOutputStream(), apiException);
    }
}
