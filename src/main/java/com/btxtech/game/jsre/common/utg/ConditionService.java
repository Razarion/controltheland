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

package com.btxtech.game.jsre.common.utg;

import com.btxtech.game.jsre.client.cockpit.quest.QuestProgressInfo;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryArtifactInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;

/**
 * User: beat Date: 28.12.2010 Time: 18:14:41 A: Actor I: Identifier
 */
public interface ConditionService<A, I> {
    void setConditionServiceListener(ConditionServiceListener<A, I> conditionServiceListener);

    void activateCondition(ConditionConfig conditionConfig, A a, I i);

    void deactivateActorCondition(A a, I i);

    void deactivateAllActorConditions(A a);

    void deactivateAll();

    void sendProgressUpdate(A a, I i);

    QuestProgressInfo getQuestProgressInfo(A a, I i);

    void onSyncItemKilled(SimpleBase actor, SyncBaseItem killedItem);

    void onSyncItemBuilt(SyncBaseItem syncBaseItem);

    void onSyncItemDeactivated(SyncBaseItem syncBaseItem);

    void onMoneyIncrease(SimpleBase base, double accountBalance);

    void onBaseDeleted(SimpleBase actorBase);

    void onSyncItemUnloaded(SyncBaseItem syncItem);

    InventoryArtifactInfo createInventoryArtifactInfo(int id);
}
