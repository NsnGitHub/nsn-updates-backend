package com.nsn.nsnupdatesbackend.utils;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

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

    public String createToken(String username, EJwtToken tokenType) {
        Key key = getKey();

        long duration = jwtAccessTokenDuration;

        if (tokenType == EJwtToken.REFRESH_TOKEN) {
            duration = jwtRefreshTokenDuration;
        }

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + duration))
                .signWith(key)
                .compact();
    }

    private Claims parseToken(String token) {
        Key key = getKey();

        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }

    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

}
