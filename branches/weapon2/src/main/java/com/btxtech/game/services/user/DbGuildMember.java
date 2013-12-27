package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.common.info.DetailedUser;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

/**
 * User: beat
 * Date: 04.06.13
 * Time: 15:38
 */
@Entity(name = "USER_GUILD_MEMBER")
public class DbGuildMember {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbGuild dbGuild;
    @Enumerated(EnumType.STRING)
    private GuildMemberInfo.Rank rank;
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

    public GuildMemberInfo.Rank getRank() {
        return rank;
    }

    public void setRank(GuildMemberInfo.Rank rank) {
        this.rank = rank;
    }

    public GuildMemberInfo createGuildMemberInfo(UserService userService) {
        return new GuildMemberInfo(userService.createDetailedUser(user), rank);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbGuildMember)) {
            return false;
        }

        DbGuildMember that = (DbGuildMember) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

}
