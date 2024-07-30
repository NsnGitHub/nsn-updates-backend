package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.PrivacySetting;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(UserDto user) {
        User newUser = new User(user.username(), user.displayName(), user.email(), LocalDateTime.now(), user.password(), "123");
        userRepository.save(newUser);
    }
}
