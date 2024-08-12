package com.nsn.nsnupdatesbackend.user;

import org.springframework.stereotype.Service;

@Service
public class AppUserMapper {
    public AppUserDto toUserDto(AppUser user) {
        return new AppUserDto(user.getUsername(), user.getDisplayName(), user.getBio(), user.getPrivacySetting());
    }
}
