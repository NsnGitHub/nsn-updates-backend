package com.nsn.nsnupdatesbackend.notificationbatch;

import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationBatchRepository extends JpaRepository<NotificationBatch, Integer> {
    List<NotificationBatch> findNotificationBatchByAppUser(AppUser appUser);
}
