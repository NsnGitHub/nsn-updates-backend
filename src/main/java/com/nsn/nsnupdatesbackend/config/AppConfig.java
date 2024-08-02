package com.nsn.nsnupdatesbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:env.properties")
public class AppConfig {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_ACCESS_TOKEN_TIME}")
    private String jwtAccessTokenTime;

    @Value("${JWT_REFRESH_TOKEN_TIME}")
    private String jwtRefreshTokenTime;

    @Bean
    public String getJwtSecret() {
        return jwtSecret;
    }

    @Bean
    public String getJwtAccessTokenTime() {
        return jwtAccessTokenTime;
    }

    @Bean
    public String getJwtRefreshTokenTime() {
        return jwtRefreshTokenTime;
    }
}
