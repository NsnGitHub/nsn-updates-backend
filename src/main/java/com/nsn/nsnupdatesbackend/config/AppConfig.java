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

    @Bean
    public String getJwtSecret() {
        return jwtSecret;
    }
}
