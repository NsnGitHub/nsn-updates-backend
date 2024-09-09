package com.nsn.nsnupdatesbackend.notificationbatch;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.interfaces.INotification;
import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.update.Update;
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

    @OneToMany(mappedBy = "notificationBatch")
    private List<Notification> notifications;

    @ManyToOne
    @JoinColumn(name = "update_id")
    private Update updateForBatch;

    private ZonedDateTime createdAt;
    private ENotificationType notificationType;
    private boolean isRead;

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
}
