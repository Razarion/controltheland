package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.dialogs.incentive.FriendInvitationBonus;
import com.btxtech.game.services.utg.UserGuidanceService;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * User: beat
 * Date: 25.07.13
 * Time: 23:52
 */
@Entity(name = "USER_FRIEND_INVITATION_BONUS")
public class DbFriendInvitationBonus {
    @Id
    @GeneratedValue
    private Integer id;
    private Date timeStamp;
    @OneToOne(fetch = FetchType.LAZY)
    private User host;
    @OneToOne(fetch = FetchType.LAZY)
    private User invitee;
    private int bonus;

    /**
     * Used by Hibernate
     */
    protected DbFriendInvitationBonus() {
    }

    public DbFriendInvitationBonus(User host, User invitee) {
        timeStamp = new Date();
        this.host = host;
        this.invitee = invitee;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbFriendInvitationBonus)) {
            return false;
        }

        DbFriendInvitationBonus that = (DbFriendInvitationBonus) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public int getBonus() {
        return bonus;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }


    public User getHost() {
        return host;
    }

    public User getInvitee() {
        return invitee;
    }

    public void addBonus(int bonus) {
        timeStamp = new Date();
        this.bonus += bonus;
    }

    public FriendInvitationBonus createFriendInvitationBonus(UserGuidanceService userGuidanceService) {
        FriendInvitationBonus friendInvitationBonus = new FriendInvitationBonus();
        friendInvitationBonus.setRazarionBonus(bonus);
        friendInvitationBonus.setUserName(invitee.getUsername());
        friendInvitationBonus.setLevel(userGuidanceService.getDbLevel(invitee).getNumber());
        return friendInvitationBonus;
    }
}
