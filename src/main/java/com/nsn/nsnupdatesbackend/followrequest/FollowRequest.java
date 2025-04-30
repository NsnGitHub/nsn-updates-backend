package com.nsn.nsnupdatesbackend.followrequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserDto;
import com.nsn.nsnupdatesbackend.user.AppUserMapper;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="follow_request")
@SequenceGenerator(name = "follow_req_seq", sequenceName = "follow_req_seq", allocationSize = 1)
public class FollowRequest implements Serializable {
    @JsonIgnore
    private static final long serializableVersionUID = 1L;

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

    public EFollowRequestStatus getStatus() {
        return this.status;
    }

    public String getRequesterUsername() { return this.requester.getUsername(); }

    public String getTargetUsername() { return this.target.getUsername(); }
}
