package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user_data")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private String bio;
    private LocalDateTime createdAt;
    private String passwordHash;
    private EPrivacySetting privacySetting;

    public User(String username, String displayName, String email, LocalDateTime createdAt, String passwordHash) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.createdAt = createdAt;
        this.passwordHash = passwordHash;

        this.bio = "";
        this.privacySetting = EPrivacySetting.PUBLIC;
    }

    public User() {

    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public EPrivacySetting getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(EPrivacySetting privacySetting) {
        this.privacySetting = privacySetting;
    }
}
