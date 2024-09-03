package com.nsn.nsnupdatesbackend;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestConfig {

    static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer =  new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("nsn-updates-backend-test")
                .withUsername("nsn")
                .withPassword("password");

        postgreSQLContainer.start();
    }

}