package com.btxtech.game.services.user;

import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * User: beat
 * Date: 24.07.13
 * Time: 16:29
 */
@Entity(name = "USER_FACEBOOK_INVITATION")
public class DbFacebookInvitation {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    private User host;
    @Index(name = "USER_FACEBOOK_INVITATION_REQUEST")
    private String fbRequestId;
    @Column(length = 1000)
    private String fbInvitedUserIds;
    private Date timeStamp;

    public Integer getId() {
        return id;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public String getFbRequestId() {
        return fbRequestId;
    }

    public void setFbRequestId(String fbRequestId) {
        this.fbRequestId = fbRequestId;
    }

    public String getFbInvitedUserIds() {
        return fbInvitedUserIds;
    }

    public void setFbInvitedUserIds(String fbInvitedUserIds) {
        this.fbInvitedUserIds = fbInvitedUserIds;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        timeStamp = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DbFacebookInvitation that = (DbFacebookInvitation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }

}
