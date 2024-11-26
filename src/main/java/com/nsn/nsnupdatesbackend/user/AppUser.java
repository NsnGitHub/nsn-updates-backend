package com.nsn.nsnupdatesbackend.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.enums.EUserRole;
import com.nsn.nsnupdatesbackend.follow.Follow;
import com.nsn.nsnupdatesbackend.followrequest.FollowRequest;
import com.nsn.nsnupdatesbackend.like.Like;
import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatch;
import com.nsn.nsnupdatesbackend.update.Update;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "app_user")
@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Integer id;

    @JsonIgnore
    private static final long serializableVersionUID = 1L;

    private String username;
    private String displayName;
    private String email;

    @Size(max = 100)
    private String bio;

    private ZonedDateTime createdAt;
    private String passwordHash;
    private EUserRole role;

    @Enumerated(EnumType.STRING)
    private EPrivacySetting privacySetting;

    @OneToMany(mappedBy="appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Update> updates;

    @OneToMany(mappedBy="appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Like> likes;

    @OneToMany(mappedBy="follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> following;

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followedBy;

    @OneToMany(mappedBy="requester", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowRequest> requestsSent;

    @OneToMany(mappedBy="target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowRequest> requestsReceived;

    @ManyToMany
    @JoinTable(
            name = "inbox",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "update_id")
    )
    private final List<Update> inboxedUpdates = new ArrayList<>();

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationsReceived;

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notificationsTriggered;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationBatch> notificationBatchesReceived;

    public AppUser(String username, String displayName, String email, String passwordHash) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.passwordHash = passwordHash;

        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
        this.bio = "";

        // default, though this will be set in the registration process.
        this.privacySetting = EPrivacySetting.FOLLOWER;
    }

    public AppUser() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Update> getInbox() {
        return inboxedUpdates;
    }

    public EUserRole getRole() {
        return role;
    }

    public void setRole(EUserRole role) {
        this.role = role;
    }

    public Integer getNumberOfFollowers() {
        if (this.followedBy == null) {
            return 0;
        }
        return this.followedBy.size();
    }

    public Integer getNumberFollowing() {
        if (this.following == null) {
            return 0;
        }
        return this.following.size();
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return username.equals(((AppUser) obj).username);
    }
}
