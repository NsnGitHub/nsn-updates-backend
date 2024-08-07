package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;

@Entity
@Table(name = "update_like")
@SequenceGenerator(name = "like_seq", sequenceName = "like_seq", allocationSize = 1)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "like_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "update_id")
    private Update update;

    public Like (AppUser appUser, Update update) {
        this.appUser = appUser;
        this.update = update;
    }

    public Like() {}

    public AppUser getAppUser() {
        return appUser;
    }

    public Update getUpdate() {
        return update;
    }
}
