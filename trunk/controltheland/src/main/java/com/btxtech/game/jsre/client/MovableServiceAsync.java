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
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.CrystalCostInfo;
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
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The async counterpart of <code>MovableService</code>.
 */
public interface MovableServiceAsync {
    void getRealGameInfo(String startUuid, Integer planetId, AsyncCallback<RealGameInfo> asyncCallback);

    void getSimulationGameInfo(int levelTaskId, AsyncCallback<SimulationInfo> asyncCallback);

    void surrenderBase(AsyncCallback<Void> async);

    void sendDebug(Date date, String category, String message, AsyncCallback async);

    void sendCommands(String startUuid, List<BaseCommand> baseCommands, AsyncCallback async);

    void getSyncInfo(String startUuid, boolean resendLast, AsyncCallback<List<Packet>> async);

    void getAllSyncInfo(String startUuid, AsyncCallback<Collection<SyncItemInfo>> async);

    void register(String userName, String password, String confirmPassword, String email, AsyncCallback<SimpleUser> asyncCallback);

    void createAndLoginFacebookUser(String signedRequestParameter, String nickname, String email, AsyncCallback<SimpleUser> asyncCallback);

    void login(String name, String password, AsyncCallback<SimpleUser> async);

    void loginFacebookUser(String signedRequestParameter, AsyncCallback<Void> async);

    void isFacebookUserRegistered(String signedRequestParameter, AsyncCallback<Boolean> async);

    void isNickNameValid(String nickname, AsyncCallback<VerificationRequestCallback.ErrorResult> async);

    void sendChatMessage(ChatMessage chatMessage, ChatMessageFilter chatMessageFilter, AsyncCallback<Void> asyncCallback);

    void setChatMessageFilter(ChatMessageFilter chatMessageFilter, AsyncCallback<List<MessageIdPacket>> asyncCallback);

    void pollMessageIdPackets(Integer lastMessageId, ChatMessageFilter chatMessageFilter, GameEngineMode gameEngineMode, AsyncCallback<List<MessageIdPacket>> asyncCallback);

    void sendTutorialProgress(TutorialConfig.TYPE type, String startUuid, int levelTaskId, int dbId, String name, long duration, long clientTimeStamp, AsyncCallback<GameFlow> asyncCallback);

    void sendEventTrackingStart(EventTrackingStart eventTrackingStart, AsyncCallback<Void> asyncCallback);

    void sendEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems,
                               Collection<SyncItemInfo> syncItemInfos,
                               Collection<SelectionTrackingItem> selectionTrackingItems,
                               Collection<TerrainScrollTracking> terrainScrollTrackings,
                               Collection<BrowserWindowTracking> browserWindowTrackings,
                               Collection<DialogTracking> dialogTrackings,
                               AsyncCallback<Void> asyncCallback);

    void sellItem(String startUuid, Id id, AsyncCallback<Void> asyncCallback);

    void sendStartupTask(StartupTaskInfo startupTaskInfo, String startUuid, Integer levelTaskId, AsyncCallback<Void> asyncCallback);

    void sendStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId, AsyncCallback<Void> asyncCallback);

    void getInventory(AsyncCallback<InventoryInfo> asyncCallback);

    void assembleInventoryItem(int inventoryItemId, AsyncCallback<InventoryInfo> asyncCallback);

    void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced, AsyncCallback<Void> asyncCallback);

    void buyInventoryItem(int inventoryItemId, AsyncCallback<InventoryInfo> asyncCallback);

    void buyInventoryArtifact(int inventoryArtifactId, AsyncCallback<InventoryInfo> asyncCallback);

    void loadQuestOverview(AsyncCallback<QuestOverview> async);

    void activateQuest(int questId, AsyncCallback<Void> async);

    void loadCurrentStatisticEntryInfos(AsyncCallback<Collection<CurrentStatisticEntryInfo>> async);

    void sendPerfmonData(Map<PerfmonEnum, Integer> workTimes, Map<PerfmonEnum, Map<String, Integer>> workChildTimes, int totalTime, AsyncCallback<Void> async);

    void getCrystals(AsyncCallback<Integer> async);

    void unlockItemType(int itemTypeId, AsyncCallback<UnlockContainer> async);

    void unlockQuest(int questId, AsyncCallback<UnlockContainer> async);

    void unlockPlanet(int planetId, AsyncCallback<UnlockContainer> async);

    void createBase(String startUuid, Index position, AsyncCallback<RealGameInfo> async);

    void logout(AsyncCallback<Void> async);

    void getHistoryElements(HistoryFilter historyFilter, AsyncCallback<HistoryElementInfo> callback);

    void getNewsEntry(int index, AsyncCallback<NewsEntryInfo> callback);

    void saveGuildText(String text, AsyncCallback<FullGuildInfo> asyncCallback);

    void kickGuildMember(int userId, AsyncCallback<FullGuildInfo> asyncCallback);

    void changeGuildMemberRank(int userId, GuildMemberInfo.Rank rank, AsyncCallback<FullGuildInfo> asyncCallback);

    void getSuggestedUserName(String query, int limit, AsyncCallback<SuggestOracle.Response> asyncCallback);

    void inviteUserToGuild(String userName, AsyncCallback<FullGuildInfo> asyncCallback);

    void inviteUserToGuild(SimpleBase simpleBase, AsyncCallback<Void> asyncCallback);

    void dismissGuildMemberRequest(int userId, AsyncCallback<FullGuildInfo> asyncCallback);

    void getFullGuildInfo(int guildId, AsyncCallback<FullGuildInfo> asyncCallback);

    void getCreateGuildCrystalCost(AsyncCallback<CrystalCostInfo> asyncCallback);

    void createGuild(String guildName, AsyncCallback<SimpleGuild> asyncCallback);

    void isGuildNameValid(String guildName, AsyncCallback<VerificationRequestCallback.ErrorResult> asyncCallback);

    void guildMembershipRequest(int guildId, String text, AsyncCallback<Void> asyncCallback);

    void searchGuilds(int start, int length, String guildNameQuery, AsyncCallback<SearchGuildsResult> asyncCallback);

    void joinGuild(int guildId, AsyncCallback<SimpleGuild> asyncCallback);

    void dismissGuildInvitation(int guildId, AsyncCallback<List<GuildDetailedInfo>> asyncCallback);

    void getGuildInvitations(AsyncCallback<List<GuildDetailedInfo>> getGuildInitations);

    void leaveGuild(AsyncCallback<Void> asyncCallback);

    void closeGuild(AsyncCallback<Void> asyncCallback);

    void sendMailInvite(String emailAddress, AsyncCallback<Void> asyncCallback);

    void onFacebookInvite(String fbRequestId, Collection<String> fbUserIds, AsyncCallback<Void> asyncCallback);

    void getFriendInvitationBonuses(AsyncCallback<List<FriendInvitationBonus>> asyncCallback);

    void getStarMapInfo(AsyncCallback<StarMapInfo> asyncCallback);
}
