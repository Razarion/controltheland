package com.btxtech.game.services.user;

import com.btxtech.game.jsre.client.VerificationRequestCallback;
import com.btxtech.game.jsre.client.common.info.RazarionCostInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.user.NoSuchUserException;
import com.btxtech.game.jsre.common.packets.AllianceOfferPacket;
import com.btxtech.game.services.common.NoSuchPropertyException;
import com.btxtech.game.services.common.WrongPropertyTypeException;

import java.util.Collection;
import java.util.HashMap;
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

    void inviteUserToGuild(String userName) throws NoSuchUserException;

    SimpleGuild joinGuild(int guildId);

    List<GuildDetailedInfo> dismissGuild(int guildId);

    List<GuildDetailedInfo> getGuildInvitations();

    RazarionCostInfo getCreateGuildRazarionCost() throws NoSuchPropertyException, WrongPropertyTypeException;

    void guildMembershipRequest(int guildId, String text);

    FullGuildInfo dismissGuildMemberRequest(int userId);

    FullGuildInfo kickGuildMember(int userId);

    FullGuildInfo changeGuildMemberRank(int userId, GuildMemberInfo.Rank rank);

    FullGuildInfo saveGuildText(String text);

    VerificationRequestCallback.ErrorResult isGuildNameValid(String guildName);

    SearchGuildsResult searchGuilds(int start, int length, String guildNameQuery);

    SimpleGuild getSimpleGuild();

    void leaveGuild();

    void closeGuild();

    /*------------------------------------------------------*/
    void proposeAlliance(SimpleBase partner);

    void acceptAllianceOffer(String partnerUserName);

    void rejectAllianceOffer(String partnerUserName);

    void breakAlliance(String partnerUserName);

    void restoreAlliances();

    Collection<AllianceOfferPacket> getPendingAllianceOffers();

    Collection<String> getAllAlliances();

    void onBaseCreatedOrDeleted(int userId);

    void onMakeBaseAbandoned(SimpleBase simpleBase);

    void fillAlliancesForFakeBases(BaseAttributes fakeBaseAttributes, HashMap<SimpleBase, BaseAttributes> allFakeBaseAttributes, UserState userState, int planetId);

    Set<SimpleBase> getAllianceBases(UserState userState, PlanetInfo planetInfo);
}
