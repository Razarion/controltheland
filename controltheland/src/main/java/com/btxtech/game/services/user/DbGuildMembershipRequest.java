package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMembershipRequest;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * User: beat
 * Date: 07.06.13
 * Time: 15:38
 */
@Entity(name = "USER_GUILD_MEMBERSHIP_REQUEST")
public class DbGuildMembershipRequest {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbGuild dbGuild;
    @Column(length = 50000)
    private String text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public GuildMembershipRequest createGuildMembershipRequest(UserService userService) {
        return new GuildMembershipRequest(userService.createDetailedUser(user), text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbGuildMembershipRequest)) {
            return false;
        }

        DbGuildMembershipRequest that = (DbGuildMembershipRequest) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
