package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@SequenceGenerator(name = "notification_seq", sequenceName = "notification_seq", allocationSize = 1)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private AppUser actor;

    @Enumerated(EnumType.STRING)
    private ENotificationType notificationType;

    private boolean isSentToUser;
    private boolean isRead;
    private ZonedDateTime createdAt;

    public Notification(AppUser appUser, AppUser actor, ENotificationType notificationType) {
        this.appUser = appUser;
        this.actor = actor;
        this.notificationType = notificationType;

        this.isSentToUser = false;
        this.isRead = false;
    }

    public Notification() {}

    public AppUser getAppUser() {
        return appUser;
    }

    public ENotificationType getNotificationType() {
        return notificationType;
    }
}
