package com.nsn.nsnupdatesbackend.like;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "update_like")
@SequenceGenerator(name = "like_seq", sequenceName = "like_seq", allocationSize = 1)
public class Like implements Serializable {
    @JsonIgnore
    private static final long serializableVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "like_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    @JsonBackReference
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "update_id")
    @JsonBackReference
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

    public Integer getId() {
        return id;
    }
}
