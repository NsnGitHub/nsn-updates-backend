package com.nsn.nsnupdatesbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    AppUser findUserByUsername(String username);
    AppUser findUserByEmail(String email);
}
