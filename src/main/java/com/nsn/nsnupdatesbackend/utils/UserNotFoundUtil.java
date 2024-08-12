package com.nsn.nsnupdatesbackend.utils;

import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundUtil {
    public static void throwIfRequesterAndTargetUserNotFound(AppUser requester, AppUser target) {
        if (requester == null) {
            throw new EntityNotFoundException("Requesting user does not exist");
        }

        if (target == null) {
            throw new EntityNotFoundException("Target user does not exist");
        }
    }
}
