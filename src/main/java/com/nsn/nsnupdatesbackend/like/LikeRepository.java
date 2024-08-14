package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface LikeRepository extends JpaRepository<Like, Integer> {
    Like findLikeById(Integer id);
    Like findLikeByAppUserAndUpdate(AppUser user, Update update);
    boolean existsByAppUserAndUpdate(AppUser user, Update update);
}
