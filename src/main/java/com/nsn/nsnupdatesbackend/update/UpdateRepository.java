package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UpdateRepository extends JpaRepository<Update, Integer> {
    List<Update> findAllByAppUser(AppUser appUser);
}
