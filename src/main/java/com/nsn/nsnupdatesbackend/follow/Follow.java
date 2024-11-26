package com.nsn.nsnupdatesbackend.follow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@SequenceGenerator(name = "follow_seq", sequenceName = "follow_seq", allocationSize = 1)
public class Follow implements Serializable {
    @JsonIgnore
    private static final long serializableVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "follow_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="follower_id")
    private AppUser follower;

    @ManyToOne
    @JoinColumn(name="followee_id")
    private AppUser followee;

    public Follow (AppUser follower, AppUser followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public Follow() {

    }

    public AppUser getFollower() {
        return this.follower;
    }

    public AppUser getFollowee() {
        return this.followee;
    }
}
