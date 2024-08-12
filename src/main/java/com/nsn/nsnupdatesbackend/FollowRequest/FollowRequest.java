package com.nsn.nsnupdatesbackend.FollowRequest;

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

    @Enumerated(EnumType.STRING)
    private EFollowRequestStatus status;

    public void setStatus(EFollowRequestStatus status) {
        this.status = status;
    }

    public void setRequester(AppUser requester) {
        this.requester = requester;
    }

    public void setTarget(AppUser target) {
        this.target = target;
    }
}
