package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.interfaces.INotification;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatch;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@SequenceGenerator(name = "notification_seq", sequenceName = "notification_seq", allocationSize = 1)
public class Notification implements INotification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private AppUser actor;

    @ManyToOne
    @JoinColumn(name = "notification_batch_id")
    private NotificationBatch notificationBatch;

    @Enumerated(EnumType.STRING)
    private ENotificationType notificationType;

    private boolean isSentToUser;
    private boolean isRead;
    private ZonedDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "update_id")
    private Update update;

    @Size(min = 1, max = 1000)
    private String message;

    public Notification(AppUser appUser, AppUser actor, ENotificationType notificationType) {
        this.appUser = appUser;
        this.actor = actor;
        this.notificationType = notificationType;

        this.isSentToUser = false;
        this.isRead = false;
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public Notification() {}

    public AppUser getAppUser() {
        return appUser;
    }

    public AppUser getActor() {
        return actor;
    }

    public ENotificationType getNotificationType() {
        return notificationType;
    }

    public boolean getIsSentToUser() {
        return isSentToUser;
    }

    public void setIsSentToUser(boolean sentToUser) {
        isSentToUser = sentToUser;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }
//
//    public Integer getBatchId() {
//        return batchId;
//    }
//
//    public void setBatchId(Integer batchId) {
//        this.batchId = batchId;
//    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Update getUpdate() {
       return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean getIsRead() {
        return isRead;
    }
}
