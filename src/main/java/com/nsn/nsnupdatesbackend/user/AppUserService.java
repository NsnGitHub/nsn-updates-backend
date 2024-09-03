package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.registration.RegistrationRequestDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        AppUser user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        return user;
    }

    public AppUserDto getUserDtoByUsername(String username) {
        return userMapper.toUserDto(getUserByUsername(username));
    }

    private boolean isUsernameRegistered(String requestedUsername) {
        AppUser user = userRepository.findUserByUsername(requestedUsername);
        return user != null;
    }

    private boolean isEmailRegistered(String email) {
        AppUser user = userRepository.findUserByEmail(email);
        return user != null;
    }

    public void registerUser(RegistrationRequestDto user) {

        if (isUsernameRegistered(user.username())) {
            throw new EntityExistsException("Username is already in use");
        }

        if (isEmailRegistered(user.email())) {
            throw new EntityExistsException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(user.password());

        AppUser newUser = new AppUser();
        newUser.setUsername(user.username());
        newUser.setDisplayName(user.displayName());
        newUser.setEmail(user.email());
        newUser.setPasswordHash(encodedPassword);
        newUser.setBio(null);
        newUser.setPrivacySetting(EPrivacySetting.PUBLIC);
        newUser.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));

        userRepository.save(newUser);
    }

    public void saveUser(AppUser user) {
        userRepository.save(user);
    }

    public void deleteUser(AppUser user) {
        userRepository.delete(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with username '%s' not found.", username));
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),user.getPasswordHash(),new ArrayList<>()
        );
    }
}
