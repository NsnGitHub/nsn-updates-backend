package com.nsn.nsnupdatesbackend.notificationbatch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.interfaces.INotification;
import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@SequenceGenerator(name = "notification_batch_seq", sequenceName = "notification_batch_seq", allocationSize = 1)
public class NotificationBatch implements INotification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_batch_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @JsonManagedReference
    @OneToMany(mappedBy = "notificationBatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @ManyToOne
    @JoinColumn(name = "update_id")
    private Update updateForBatch;

    private ZonedDateTime createdAt;
    private ENotificationType notificationType;
    private boolean isRead;

    public NotificationBatch() {

    }

    @Size(min = 1, max = 100)
    private String message;

    @Override
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ENotificationType getNotificationType() {
        return notificationType;
    }

    @Override
    public boolean getIsRead() {
        return isRead;
    }

    public Integer getId() {
        return id;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @JsonInclude
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNotificationType(ENotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdate(Update updateForBatch) {
        this.updateForBatch = updateForBatch;
    }

}
