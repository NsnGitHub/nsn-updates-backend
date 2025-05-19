package com.nsn.nsnupdatesbackend.notification;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.interfaces.INotification;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatch;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@SequenceGenerator(name = "notification_seq", sequenceName = "notification_seq", allocationSize = 1)
public class Notification implements INotification, Serializable {

    @JsonIgnore
    private static final long serializableVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private AppUser actor;

    @JsonBackReference
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

    public Notification(AppUser actor, AppUser appUser, ENotificationType notificationType) {
        this.appUser = appUser;
        this.actor = actor;
        this.notificationType = notificationType;

        this.update = null;
        this.isSentToUser = false;
        this.isRead = false;
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public Notification(AppUser actor, AppUser appUser, ENotificationType notificationType, Update update) {
        this.appUser = appUser;
        this.actor = actor;
        this.notificationType = notificationType;
        this.update = update;

        this.isSentToUser = false;
        this.isRead = false;
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public Notification() {}

    public Integer getId() {
        return id;
    }

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

    public void setNotificationBatch(NotificationBatch notificationBatch) {
        this.notificationBatch = notificationBatch;
    }

    public NotificationBatch getNotificationBatch() {
        return notificationBatch;
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
