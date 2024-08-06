package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.registration.RegistrationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UserDetailsService {
    private final AppUserRepository userRepository;
    private final AppUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppUserService(AppUserRepository userRepository, AppUserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    public AppUser getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public AppUserDto getUserDtoByUsername(String username) {
        return userMapper.toUserDto(getUserByUsername(username));
    }

    public void registerUser(RegistrationRequestDTO user) {
        String encodedPassword = passwordEncoder.encode(user.password());

        AppUser newUser = new AppUser();
        newUser.setUsername(user.username());
        newUser.setDisplayName(user.displayName());
        newUser.setEmail(user.email());
        newUser.setPasswordHash(encodedPassword);
        newUser.setBio(null);
        newUser.setPrivacySetting(EPrivacySetting.PUBLIC);
        newUser.setCreatedAt(LocalDateTime.now());

        userRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with username '%s' not found.", username));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), new ArrayList<>());
    }
}
