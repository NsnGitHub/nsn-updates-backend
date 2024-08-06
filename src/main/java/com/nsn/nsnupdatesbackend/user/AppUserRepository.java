package com.nsn.nsnupdatesbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findUserByUsername(String username);
}
