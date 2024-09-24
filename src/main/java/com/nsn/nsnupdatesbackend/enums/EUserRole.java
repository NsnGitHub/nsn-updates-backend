package com.nsn.nsnupdatesbackend.enums;

import org.springframework.security.core.GrantedAuthority;

public enum EUserRole implements GrantedAuthority {
    ROLE_GUEST,
    ROLE_USER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
