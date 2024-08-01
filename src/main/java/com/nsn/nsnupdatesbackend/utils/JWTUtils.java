package com.nsn.nsnupdatesbackend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

    public JWTUtils(@Value("${JWT_SECRET}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    private Key getKey() {
        byte[] bytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(String username) {
        Key key = getKey();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600 * 1000))
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
