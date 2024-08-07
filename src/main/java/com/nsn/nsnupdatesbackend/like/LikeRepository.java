package com.nsn.nsnupdatesbackend.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface LikeRepository extends JpaRepository<Like, Integer> {
    Like findLikeById(Integer id);
}
