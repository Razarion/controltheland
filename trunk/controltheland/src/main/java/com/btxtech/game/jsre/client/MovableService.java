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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.InvalidLevelState;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.services.user.PasswordNotMatchException;
import com.btxtech.game.jsre.common.gameengine.services.user.UserAlreadyExistsException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.ChatMessage;
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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("movableService")
public interface MovableService extends RemoteService {
    RealGameInfo getRealGameInfo(String startUuid) throws InvalidLevelState;

    SimulationInfo getSimulationGameInfo(int levelTaskId) throws InvalidLevelState;

    void surrenderBase();

    void log(String message, Date date);

    void sendCommands(List<BaseCommand> baseCommands);

    List<Packet> getSyncInfo(String startUuid) throws NoConnectionException;

    Collection<SyncItemInfo> getAllSyncInfo();

    void register(String userName, String password, String confirmPassword, String email) throws UserAlreadyExistsException, PasswordNotMatchException;

    void sendChatMessage(ChatMessage chatMessage);

    List<ChatMessage> pollChatMessages(Integer lastMessageId);

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

    void sellItem(Id id);

    void proposeAlliance(SimpleBase partner);

    void acceptAllianceOffer(String partnerUserName);

    void rejectAllianceOffer(String partnerUserName);

    void breakAlliance(String partnerUserName);

    Collection<String> getAllAlliances();

    InventoryInfo getInventory();

    InventoryInfo assembleInventoryItem(int inventoryItemId);

    void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced);

    int buyInventoryItem(int inventoryItemId);

    int buyInventoryArtifact(int inventoryArtifactId);

    int loadRazarion();

    QuestOverview loadQuestOverview();

    void activateQuest(int questId);

    Collection<CurrentStatisticEntryInfo> loadCurrentStatisticEntryInfos();

    void sendPerfmonData(Map<PerfmonEnum, Integer> workTimes, int totalTime);
}
