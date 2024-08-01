package com.nsn.nsnupdatesbackend.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(user.getUsername(), user.getDisplayName(), user.getEmail(), user.getBio(), user.getPrivacySetting());
    }
}
