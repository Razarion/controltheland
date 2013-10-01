package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.common.info.SimpleUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 31.05.13
 * Time: 16:34
 */
public class FullGuildInfo implements Serializable {
    public GuildInfo guildInfo;
    private Collection<GuildMemberInfo> members = new ArrayList<GuildMemberInfo>();
    private List<GuildMembershipRequest> requests;

    public GuildInfo getGuildInfo() {
        return guildInfo;
    }

    public void setGuildInfo(GuildInfo guildInfo) {
        this.guildInfo = guildInfo;
    }

    public Collection<GuildMemberInfo> getMembers() {
        return members;
    }

    public void setMembers(Collection<GuildMemberInfo> members) {
        this.members = members;
    }

    public void addGuildMember(GuildMemberInfo guildMemberInfo) {
        members.add(guildMemberInfo);
    }

    public GuildMemberInfo getMember(SimpleUser simpleUser) {
        for (GuildMemberInfo member : members) {
            if (member.getDetailedUser().getSimpleUser().equals(simpleUser)) {
                return member;
            }
        }
        return null;
    }

    public List<GuildMembershipRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<GuildMembershipRequest> requests) {
        this.requests = requests;
    }
}
