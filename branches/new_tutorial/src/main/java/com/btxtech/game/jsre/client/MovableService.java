/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.cockpit.item.InvitingUnregisteredBaseException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RazarionCostInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.guild.FullGuildInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildDetailedInfo;
import com.btxtech.game.jsre.client.dialogs.guild.GuildMemberInfo;
import com.btxtech.game.jsre.client.dialogs.guild.SearchGuildsResult;
import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryElementInfo;
import com.btxtech.game.jsre.client.dialogs.history.HistoryFilter;
import com.btxtech.game.jsre.client.dialogs.incentive.FriendInvitationBonus;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.news.NewsEntryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapInfo;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.UserIsAlreadyGuildMemberException;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.user.EmailAlreadyExitsException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedException;
import com.btxtech.game.jsre.common.gameengine.services.user.LoginFailedNotVerifiedException;
import com.btxtech.game.jsre.common.gameengine.services.user.NoSuchUserException;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.common.packets.MessageIdPacket;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.ui.SuggestOracle;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("movableService")
public interface MovableService extends RemoteService {
    RealGameInfo getRealGameInfo(String startUuid, Integer planetId) throws InvalidLevelStateException;

    SimulationInfo getSimulationGameInfo(int levelTaskId) throws InvalidLevelStateException;

    void surrenderBase();

    void sendDebug(Date date, String category, String message);

    void sendCommands(String startUuid, List<BaseCommand> baseCommands) throws NoConnectionException;

    List<Packet> getSyncInfo(String startUuid, boolean resendLast) throws NoConnectionException;

    Collection<SyncItemInfo> getAllSyncInfo(String startUuid) throws NoConnectionException;

    SimpleUser register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException, EmailAlreadyExitsException;

    SimpleUser createAndLoginFacebookUser(String signedRequestParameter, String nickname, String email) throws UserAlreadyExistsException, PasswordNotMatchException;

    void loginFacebookUser(String signedRequestParameter) throws UserAlreadyExistsException;

    SimpleUser login(String name, String password) throws LoginFailedException, LoginFailedNotVerifiedException;

    void logout();

    boolean isFacebookUserRegistered(String signedRequestParameter);

    VerificationRequestCallback.ErrorResult isNickNameValid(String nickname) throws UserAlreadyExistsException, PasswordNotMatchException;

    void sendChatMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter);

    List<MessageIdPacket> setChatMessageFilter(ChatMessageFilter chatMessageFilter) throws NotAGuildMemberException;

    List<MessageIdPacket> pollMessageIdPackets(Integer lastMessageId, ChatMessageFilter chatMessageFilter, GameEngineMode gameEngineMode);

    void sendStartupTask(StartupTaskInfo startupTaskInfo, String uuid, Integer levelTaskId);

    void sendStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId);

    GameFlow sendTutorialProgress(TutorialConfig.TYPE type, String startUuid, int levelTaskId, String name, long duration, long clientTimeStamp);

    void sendEventTrackingStart(EventTrackingStart eventTrackingStart);

    void sendEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems,
                               Collection<SyncItemInfo> syncItemInfos,
                               Collection<SelectionTrackingItem> selectionTrackingItems,
                               Collection<TerrainScrollTracking> terrainScrollTrackings,
                               Collection<BrowserWindowTracking> browserWindowTrackings,
                               Collection<DialogTracking> dialogTrackings);

    void sellItem(String startUuid, Id id);

    InventoryInfo getInventory(Integer filterPlanetId, boolean filterLevel);

    InventoryInfo assembleInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel);

    void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced);

    InventoryInfo buyInventoryItem(int inventoryItemId, Integer filterPlanetId, boolean filterLevel);

    InventoryInfo buyInventoryArtifact(int inventoryArtifactId, Integer filterPlanetId, boolean filterLevel);

    QuestOverview loadQuestOverview();

    void activateQuest(int questId);

    Collection<CurrentStatisticEntryInfo> loadCurrentStatisticEntryInfos();

    void sendPerfmonData(Map<PerfmonEnum, Integer> workTimes, int totalTime);

    int getRazarion();

    UnlockContainer unlockItemType(int itemTypeId);

    UnlockContainer unlockQuest(int questId);

    UnlockContainer unlockPlanet(int planetId);

    RealGameInfo createBase(String startUuid, Index position) throws PositionInBotException, NoConnectionException;

    HistoryElementInfo getHistoryElements(HistoryFilter historyFilter);

    NewsEntryInfo getNewsEntry(int index);

    FullGuildInfo saveGuildText(String text);

    FullGuildInfo kickGuildMember(int userId);

    FullGuildInfo changeGuildMemberRank(int userId, GuildMemberInfo.Rank rank);

    SuggestOracle.Response getSuggestedUserName(String query, int limit);

    FullGuildInfo inviteUserToGuild(String userName) throws NoSuchUserException, UserIsAlreadyGuildMemberException;

    void inviteUserToGuild(SimpleBase simpleBase) throws InvitingUnregisteredBaseException;

    FullGuildInfo dismissGuildMemberRequest(int userId);

    FullGuildInfo getFullGuildInfo(int guildId);

    RazarionCostInfo getCreateGuildRazarionCost();

    SimpleGuild createGuild(String guildName);

    VerificationRequestCallback.ErrorResult isGuildNameValid(String guildName);

    void guildMembershipRequest(int guildId, String text);

    SearchGuildsResult searchGuilds(int start, int length, String guildNameQuery);

    SimpleGuild joinGuild(int guildId);

    List<GuildDetailedInfo> dismissGuildInvitation(int guildId);

    List<GuildDetailedInfo> getGuildInvitations();

    void leaveGuild();

    void closeGuild();

    void sendMailInvite(String emailAddress);

    void onFacebookInvite(String fbRequestId, Collection<String> fbUserIds);

    List<FriendInvitationBonus> getFriendInvitationBonuses();

    StarMapInfo getStarMapInfo();
}
