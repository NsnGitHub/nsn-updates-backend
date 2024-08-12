package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;

@Entity
@SequenceGenerator(name = "follow_seq", sequenceName = "follow_seq", allocationSize = 1)
public class Follow {
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
}
