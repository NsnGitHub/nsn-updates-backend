package com.nsn.nsnupdatesbackend.notification;

public record NotificationCountDto(
        int unreadFollowCount,
        int unreadUpdateCount
) {
}
