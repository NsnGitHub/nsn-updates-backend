package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;

@Entity
@SequenceGenerator(name = "update_seq", sequenceName = "update_seq", allocationSize = 1)
public class Update {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "update_seq")
    private Integer id;
    private ZonedDateTime createdAt;
    @Size(min = 1, max = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    public Update(String content, ZonedDateTime createdAt, AppUser appUser) {
        this.content = content;
        this.createdAt = createdAt;
        this.appUser = appUser;
    }

    public Update() {}

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
