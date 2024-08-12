package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.user.AppUser;
import jakarta.persistence.*;

@Entity
@Table(name="follow_request")
@SequenceGenerator(name = "follow_req_seq", sequenceName = "follow_req_seq", allocationSize = 1)
public class FollowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "follow_req_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="requester_id")
    private AppUser requester;

    @ManyToOne
    @JoinColumn(name="target_id")
    private AppUser target;

    private EFollowRequestStatus status;
}
