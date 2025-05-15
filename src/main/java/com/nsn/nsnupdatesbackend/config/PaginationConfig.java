package com.nsn.nsnupdatesbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="pagination")
public class PaginationConfig {
    public int getPageSize() {
        return 5;
    }
}
