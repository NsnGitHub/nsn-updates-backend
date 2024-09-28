package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.interfaces.INotification;
import com.nsn.nsnupdatesbackend.update.UpdateDto;
import com.nsn.nsnupdatesbackend.update.UpdateMapper;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserDto;
import com.nsn.nsnupdatesbackend.user.AppUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {

    private final AppUserMapper appUserMapper;
    private final UpdateMapper updateMapper;

    @Autowired
    public NotificationMapper(AppUserMapper appUserMapper, UpdateMapper updateMapper) {
        this.appUserMapper = appUserMapper;
        this.updateMapper = updateMapper;
    }

    public NotificationDto toNotificationDto(Notification notification) {
        AppUserDto appUserDto = null;
        UpdateDto updateDto = null;

        if (notification.getActor() != null) {
            appUserDto = appUserMapper.toUserDto(notification.getActor());
        }

        if (notification.getUpdate() != null) {
            updateDto = updateMapper.toUpdateDto(notification.getUpdate(), false);
        }

        return new NotificationDto(
                appUserDto,
                updateDto,
                notification.getNotificationType(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }
}
