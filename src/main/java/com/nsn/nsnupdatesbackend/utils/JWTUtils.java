package com.nsn.nsnupdatesbackend.utils;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.enums.EUserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class JWTUtils {
    private final String jwtSecret;
    private final long jwtAccessTokenDuration;
    private final long jwtRefreshTokenDuration;

    public JWTUtils(@Value("${JWT_SECRET}") String jwtSecret,
        @Value("${JWT_ACCESS_TOKEN_TIME}") String jwtAccessTokenDuration,
        @Value("${JWT_REFRESH_TOKEN_TIME}") String jwtRefreshTokenDuration) {
            this.jwtSecret = jwtSecret;
            this.jwtAccessTokenDuration = Long.parseLong(jwtAccessTokenDuration);
            this.jwtRefreshTokenDuration = Long.parseLong(jwtRefreshTokenDuration);
    }

    private Key getKey() {
        byte[] bytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public long getJwtTokenDuration(EJwtToken jwtTokenType) {
        return switch (jwtTokenType) {
            case ACCESS_TOKEN -> jwtAccessTokenDuration;
            case REFRESH_TOKEN -> jwtRefreshTokenDuration;
        };
    }

    public String createToken(String username, EJwtToken tokenType, EUserRole role) {
        HashMap<String, Object> claims = new HashMap<>();

        claims.put("token_role", tokenType);
        claims.put("roles", role);

        Key key = getKey();

        long duration = jwtAccessTokenDuration;

        if (tokenType == EJwtToken.REFRESH_TOKEN) {
            duration = jwtRefreshTokenDuration;
        }

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + duration))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        Key key = getKey();
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }

    public boolean isAccessToken(String token) {
        String tokenRole = extractClaims(token).get("token_role").toString();
        return tokenRole.equals(EJwtToken.ACCESS_TOKEN.toString());
    }

    public boolean isRefreshToken(String token) {
        String tokenRole = extractClaims(token).get("token_role").toString();
        return tokenRole.equals(EJwtToken.REFRESH_TOKEN.toString());
    }

    public boolean isGuestToken(String token) {
        String userRole = extractClaims(token).get("roles").toString();
        return userRole.equals(EUserRole.ROLE_GUEST.toString());
    }

    public String getUsername(String token) {
        return extractClaims(token).getSubject();
    }

}
