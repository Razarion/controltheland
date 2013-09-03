package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.cockpit.item.InvitingUnregisteredBaseException;
import com.btxtech.game.jsre.client.common.info.RazarionCostInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.UserIsAlreadyGuildMemberException;
import com.btxtech.game.jsre.common.gameengine.services.user.NoSuchUserException;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
import com.btxtech.game.services.common.NoSuchPropertyException;
import com.btxtech.game.services.common.WrongPropertyTypeException;

import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: 24.04.12
 * Time: 10:12
 */
public interface GuildService {
    SimpleGuild createGuild(String guildName) throws NoSuchPropertyException, WrongPropertyTypeException;

    FullGuildInfo getFullGuildInfo(int guildId);

    void inviteUserToGuild(String userName) throws NoSuchUserException, UserIsAlreadyGuildMemberException;

    void inviteUserToGuild(SimpleBase simpleBase) throws NoSuchUserException, InvitingUnregisteredBaseException, UserIsAlreadyGuildMemberException;

    SimpleGuild joinGuild(int guildId);

    List<GuildDetailedInfo> dismissGuildInvitation(int guildId);

    List<GuildDetailedInfo> getGuildInvitations();

    RazarionCostInfo getCreateGuildRazarionCost() throws NoSuchPropertyException, WrongPropertyTypeException;

    void guildMembershipRequest(int guildId, String text);

    FullGuildInfo dismissGuildMemberRequest(int userId);

    FullGuildInfo kickGuildMember(int userId);

    FullGuildInfo changeGuildMemberRank(int userId, GuildMemberInfo.Rank rank);

    FullGuildInfo saveGuildText(String text);

    VerificationRequestCallback.ErrorResult isGuildNameValid(String guildName);

    SearchGuildsResult searchGuilds(int start, int length, String guildNameQuery);

    void leaveGuild();

    void closeGuild();

    void fillUserAttentionPacket(User user, UserAttentionPacket userAttentionPacket);

    SimpleGuild getSimpleGuild();

    SimpleGuild getGuildId(UserState userState);

    void onMakeBaseAbandoned(User user, SimpleBase simpleBase);

    void onMakeBaseAbandonedHandleEnemies(User user, SimpleBase simpleBase);

    Set<SimpleBase> getGuildBases(UserState userState, int planetId);
}
