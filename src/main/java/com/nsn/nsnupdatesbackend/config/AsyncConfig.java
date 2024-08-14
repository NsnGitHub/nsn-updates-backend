package com.nsn.nsnupdatesbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Can configure thread pool here, but a simple one will work in this application.
}
