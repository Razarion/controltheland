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

import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.packets.ChatMessage;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;
import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.EventTrackingStart;
import com.btxtech.game.jsre.common.utg.tracking.SelectionTrackingItem;
import com.btxtech.game.jsre.common.utg.tracking.TerrainScrollTracking;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * The async counterpart of <code>MovableService</code>.
 */
public interface MovableServiceAsync {
    void getRealGameInfo(AsyncCallback<RealGameInfo> asyncCallback);

    void getSimulationGameInfo(int levelTaskId, AsyncCallback<SimulationInfo> asyncCallback);

    void log(String message, Date date, AsyncCallback async);

    void sendCommands(List<BaseCommand> baseCommands, AsyncCallback async);

    void getSyncInfo(AsyncCallback<List<Packet>> async);

    void getAllSyncInfo(AsyncCallback<Collection<SyncItemInfo>> async);

    void register(String userName, String password, String confirmPassword, String email, AsyncCallback<Void> asyncCallback);

    void sendChatMessage(ChatMessage chatMessage, AsyncCallback<Void> asyncCallback);

    void pollChatMessages(Integer lastMessageId, AsyncCallback<List<ChatMessage>> asyncCallback);

    void surrenderBase(AsyncCallback<Void> asyncCallback);

    void closeConnection(AsyncCallback<Void> async);

    void sendTutorialProgress(TutorialConfig.TYPE type, String startUuid, int levelTaskId, String name, long duration, long clientTimeStamp, AsyncCallback<GameFlow> asyncCallback);

    void sendEventTrackingStart(EventTrackingStart eventTrackingStart, AsyncCallback<Void> asyncCallback);

    void sendEventTrackerItems(Collection<EventTrackingItem> eventTrackingItems,
                               Collection<SyncItemInfo> syncItemInfos,
                               Collection<SelectionTrackingItem> selectionTrackingItems,
                               Collection<TerrainScrollTracking> terrainScrollTrackings,
                               Collection<BrowserWindowTracking> browserWindowTrackings,
                               Collection<DialogTracking> dialogTrackings,
                               AsyncCallback<Void> asyncCallback);

    void sellItem(Id id, AsyncCallback<Void> asyncCallback);

    void sendStartupTask(StartupTaskInfo startupTaskInfo, String startUuid, Integer levelTaskId, AsyncCallback<Void> asyncCallback);

    void sendStartupTerminated(boolean successful, long totalTime, String startUuid, Integer levelTaskId, AsyncCallback<Void> asyncCallback);

    void acceptAllianceOffer(String partnerUserName, AsyncCallback<Void> asyncCallback);

    void breakAlliance(String partnerUserName, AsyncCallback<Void> asyncCallback);

    void proposeAlliance(SimpleBase partner, AsyncCallback<Void> asyncCallback);

    void rejectAllianceOffer(String partnerUserName, AsyncCallback<Void> asyncCallback);

    void getAllAlliances(AsyncCallback<Collection<String>> asyncCallback);

    void getInventory(AsyncCallback<InventoryInfo> asyncCallback);

    void assembleInventoryItem(int inventoryItemId, AsyncCallback<InventoryInfo> asyncCallback);

    void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced, AsyncCallback<Void> asyncCallback);

    void buyInventoryItem(int inventoryItemId, AsyncCallback<Integer> asyncCallback);

    void buyInventoryArtifact(int inventoryArtifactId, AsyncCallback<Integer> asyncCallback);

    void loadRazarion(AsyncCallback<Integer> asyncCallback);

    void loadQuestOverview(AsyncCallback<QuestOverview> async);

    void activateQuest(int questId, AsyncCallback<Void> async);
}
