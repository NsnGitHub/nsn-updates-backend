package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    List<Follow> findByFollowee(AppUser user);
    List<Follow> findByFollower(AppUser user);
}
