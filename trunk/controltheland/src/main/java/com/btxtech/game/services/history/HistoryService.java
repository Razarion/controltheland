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

package com.btxtech.game.services.history;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;

import java.util.List;

/**
 * User: beat
 * Date: Jul 5, 2009
 * Time: 7:27:16 PM
 */
public interface HistoryService {
    void addBaseStartEntry(SimpleBase simpleBase);

    void addBaseDefeatedEntry(SimpleBase actor, SimpleBase target);

    void addBaseSurrenderedEntry(SimpleBase simpleBase);

    void addItemCreatedEntry(SyncBaseItem syncBaseItem);

    void addItemDestroyedEntry(SimpleBase actor, SyncBaseItem target);

    void addLevelPromotionEntry(UserState user, DbLevel level);

    void addLevelTaskCompletedEntry(UserState userState, DbLevelTask levelTask);

    void addLevelTaskActivated(UserState userState, DbLevelTask dbLevelTask);

    void addLevelTaskDeactivated(UserState userState, DbLevelTask dbLevelTask);

    void addAllianceOffered(User actor, User target);

    void addAllianceOfferAccepted(User actor, User target);

    void addAllianceOfferRejected(User actor, User target);

    void addAllianceBroken(User actor, User target);

    void addBoxExpired(SyncBoxItem boxItem);

    void addBoxDropped(SyncBoxItem boxItem, Index position, SyncBaseItem dropper);

    void addBoxPicked(SyncBoxItem boxItem, SyncBaseItem picker);

    void addRazarionFromBox(UserState userState, int razarion);

    void addRazarionBought(UserState userState, int razarionBought);

    void addInventoryItemFromBox(UserState userState, String inventoryItemName);

    void addInventoryArtifactFromBox(UserState userState, String inventoryArtifactName);

    void addInventoryItemUsed(UserState userState, String inventoryItemName);

    void addInventoryItemBought(UserState userState, String inventoryItemName, int razarion);

    void addInventoryArtifactBought(UserState userState, String inventoryArtifactName, int razarion);

    void addBotEnrageUp(String botName, BotEnragementStateConfig botEnragementState, SimpleBase actor);

    void addBotEnrageNormal(String botName, BotEnragementStateConfig botEnragementState);

    void addItemUnlocked(UserState userState, BaseItemType baseItemType);

    void addQuestUnlocked(UserState userState, DbLevelTask dbLevelTask);

    void addPlanetUnlocked(UserState userState, PlanetLiteInfo planetLiteInfo);

    List<DisplayHistoryElement> getNewestHistoryElements(User user, int count);

    List<DisplayHistoryElement> getHistoryElements(GameHistoryFrame gameHistoryFrame, GameHistoryFilter gameHistoryFilter);

    int getLevelPromotionCount(final String sessionId);

    ReadonlyListContentProvider<DisplayHistoryElement> getNewestHistoryElements();
}
