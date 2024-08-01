package com.nsn.nsnupdatesbackend.filters;

import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private JWTUtils jwtUtils;

    public JWTAuthorizationFilter(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/login")) {
            filterChain.doFilter(request, response);
        } else {
            String authorization = request.getHeader("Authorization");
            System.out.println(authorization);
            if (authorization != null && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                System.out.println(token);
                try {
                    String username = jwtUtils.getUsername(token);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                    if (auth != null && auth.isAuthenticated()) {
                        System.out.println("Authenticated");
                    }

                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
