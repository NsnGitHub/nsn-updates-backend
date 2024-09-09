package com.nsn.nsnupdatesbackend.notificationwebsocket;

import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.notification.NotificationDto;

public class NotificationMessage {

//    private NotificationDto notificationDto;
//
    public NotificationMessage() {}
//
//    public NotificationMessage(NotificationDto notificationDto) {
//        this.notificationDto = notificationDto;
//    }
//
//    public NotificationDto getNotificationDto() {
//        return notificationDto;
//    }
//
//    public void setNotificationDto(NotificationDto notificationDto) {
//        this.notificationDto = notificationDto;
//    }

    private String message;

    public NotificationMessage(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
