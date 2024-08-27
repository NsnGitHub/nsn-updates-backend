package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotificationFromUserAndTarget(AppUser user, AppUser target,
                                                     ENotificationType  eNotificationType) {

       Notification notification = new Notification(target, user, eNotificationType);
       notificationRepository.save(notification);
    }

//    /**
//        Notifications only created when something happens with another service, their user and target AppUser objects
//        should be loaded, so it would have access to the ID
//    **/
//    public void createNotificationFromUserIdAndTargetId(Integer userId, Integer targetId,
//                                                     ENotificationType  eNotificationType) {
//        AppUser userDummy = new AppUser();
//        userDummy.setId(userId);
//
//        AppUser targetDummy = new AppUser();
//        targetDummy.setId(targetId);
//
//       Notification notification = new Notification(userDummy, targetDummy, eNotificationType);
//       notificationRepository.save(notification);
//    }
}
