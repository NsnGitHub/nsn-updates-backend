package com.nsn.nsnupdatesbackend.interfaces;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;

import java.time.ZonedDateTime;

public interface INotification {
    ZonedDateTime getCreatedAt();
    String getMessage();
    ENotificationType getNotificationType();
    boolean getIsRead();
}
