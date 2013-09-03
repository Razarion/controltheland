package com.btxtech.game.services.user;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * User: beat
 * Date: 05.06.13
 * Time: 15:41
 */
@Entity(name = "USER_GUILD_INVITATION")
public class DbGuildInvitation {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbGuild dbGuild;
    private Date timeStamp;

    public Integer getId() {
        return id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp() {
        timeStamp = new Date();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DbGuild getDbGuild() {
        return dbGuild;
    }

    public void setDbGuild(DbGuild dbGuild) {
        this.dbGuild = dbGuild;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbGuildInvitation)) {
            return false;
        }

        DbGuildInvitation that = (DbGuildInvitation) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
