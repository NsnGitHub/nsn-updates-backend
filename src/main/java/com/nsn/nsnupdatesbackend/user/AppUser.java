package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.update.Update;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name="app_user")
@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Integer id;

    private String username;
    private String displayName;
    private String email;
    private String bio;
    private ZonedDateTime createdAt;
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private EPrivacySetting privacySetting;

    @OneToMany(mappedBy="appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Update> updates;

    public AppUser(String username, String displayName, String email, ZonedDateTime createdAt, String passwordHash) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.createdAt = createdAt;
        this.passwordHash = passwordHash;

        this.bio = "";
        this.privacySetting = EPrivacySetting.PUBLIC;
    }

    public AppUser() {

    }

    public Integer getId() {
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
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
