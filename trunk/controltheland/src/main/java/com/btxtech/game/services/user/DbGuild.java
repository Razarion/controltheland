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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 04.06.13
 * Time: 15:38
 */
@Entity
@Table(name = "USER_GUILD")
public class DbGuild {
    @Id
    @GeneratedValue
    private Integer id;
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "dbGuild")
    private Collection<DbGuildMember> guildMembers;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dbGuild")
    private Collection<DbGuildMembershipRequest> dbGuildMembershipRequests;
    private String name;
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

    public void addMember(User user, GuildMemberInfo.Rank rank) {
        if (guildMembers == null) {
            guildMembers = new ArrayList<>();
        }
        DbGuildMember dbGuildMember = new DbGuildMember();
        dbGuildMember.setTimeStamp();
        dbGuildMember.setDbGuild(this);
        dbGuildMember.setUser(user);
        dbGuildMember.setRank(rank);
        guildMembers.add(dbGuildMember);
    }

    public void removeMember(User userToKick) {
        guildMembers.remove(getDbGuildMember(userToKick));
    }


    public boolean canKickMember(User user, User userToKick) {
        DbGuildMember member = getDbGuildMember(user);
        DbGuildMember memberToKick = getDbGuildMember(userToKick);
        return member.getRank().isHigher(memberToKick.getRank());
    }

    public DbGuildMember getDbGuildMember(User user) {
        for (DbGuildMember guildMember : guildMembers) {
            if (guildMember.getUser().equals(user)) {
                return guildMember;
            }
        }
        throw new IllegalArgumentException("No such user: " + user + " in guild " + this);
    }

    public Collection<DbGuildMember> getGuildMembers() {
        return guildMembers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SimpleGuild createSimpleGuild() {
        return new SimpleGuild(id, name);
    }

    public GuildInfo createGuildInfo() {
        return new GuildInfo(id, name, text);
    }

    public FullGuildInfo createFullGuildInfo(UserService userService) {
        FullGuildInfo fullGuildInfo = new FullGuildInfo();
        fullGuildInfo.setGuildInfo(createGuildInfo());
        for (DbGuildMember guildMember : guildMembers) {
            fullGuildInfo.addGuildMember(guildMember.createGuildMemberInfo(userService));
        }
        fullGuildInfo.setRequests(getGuildMembershipRequest(userService));
        return fullGuildInfo;
    }

    private List<GuildMembershipRequest> getGuildMembershipRequest(UserService userService) {
        List<GuildMembershipRequest> guildMembershipRequests = new ArrayList<>();
        if (dbGuildMembershipRequests != null) {
            for (DbGuildMembershipRequest dbGuildMembershipRequest : dbGuildMembershipRequests) {
                guildMembershipRequests.add(dbGuildMembershipRequest.createGuildMembershipRequest(userService));
            }
        }
        return guildMembershipRequests;
    }

    public GuildDetailedInfo createGuildDetailedInfo() {
        return new GuildDetailedInfo(id, name, text, guildMembers != null ? guildMembers.size() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DbGuild)) {
            return false;
        }

        DbGuild that = (DbGuild) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "DbGuild{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
