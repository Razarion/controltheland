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

package com.btxtech.game.services.bot.impl;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.common.ai.BotLevel;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 20.03.2010
 * Time: 11:44:44
 */
public class BotLevelFactory {
    private List<BotLevel> botLevels = new ArrayList<BotLevel>();

    public BotLevelFactory() {
        // Level 0
        BotLevel botLevel = new BotLevel();
        botLevel.setAttackHold(0.3);
        botLevel.setAttackPause(30000);
        botLevel.setAttackPauseDistance(400, 800);
        botLevel.addItemTypeBalance(Constants.FACTORY, 1); // First prio
        botLevel.addItemTypeBalance(Constants.HARVESTER, 1);// Second prio
        botLevel.addItemTypeBalance(Constants.JEEP, 1);// Third prio
        botLevels.add(botLevel);
        // Level 1
        botLevel = new BotLevel();
        botLevel.setAttackHold(0.3);
        botLevel.setAttackPause(10000);
        botLevel.setAttackPauseDistance(200, 400);
        botLevel.addItemTypeBalance(Constants.FACTORY, 1); // First prio
        botLevel.addItemTypeBalance(Constants.HARVESTER, 1);// Second prio
        botLevel.addItemTypeBalance(Constants.JEEP, 1);// Third prio
        botLevels.add(botLevel);
        // Level 2
        botLevel = new BotLevel();
        botLevel.addItemTypeBalance(Constants.FACTORY, 1); // First prio
        botLevel.addItemTypeBalance(Constants.HARVESTER, 1);// Second prio
        botLevel.addItemTypeBalance(Constants.JEEP, 2);// Third prio
        botLevels.add(botLevel);
        // Level 3
        botLevel = new BotLevel();
        botLevel.addItemTypeBalance(Constants.FACTORY, 1); // First prio
        botLevel.addItemTypeBalance(Constants.HARVESTER, 1);// Second prio
        botLevel.addItemTypeBalance(Constants.JEEP, 4);// Third prio
        botLevels.add(botLevel);
        // Level 4
        botLevel = new BotLevel();
        botLevel.addItemTypeBalance(Constants.FACTORY, 1); // First prio
        botLevel.addItemTypeBalance(Constants.HARVESTER, 2);// Second prio
        botLevel.addItemTypeBalance(Constants.JEEP, 6);// Third prio
        botLevels.add(botLevel);
        // Level 5
        botLevel = new BotLevel();
        botLevel.addItemTypeBalance(Constants.FACTORY, 2); // First prio
        botLevel.addItemTypeBalance(Constants.HARVESTER, 4);// Second prio
        botLevel.addItemTypeBalance(Constants.JEEP, 10);// Third prio
        botLevels.add(botLevel);
        // Level 6
        botLevel = new BotLevel();
        botLevel.addItemTypeBalance(Constants.FACTORY, 3); // First prio
        botLevel.addItemTypeBalance(Constants.HARVESTER, 4);// Second prio
        botLevel.addItemTypeBalance(Constants.JEEP, 20);// Third prio
        botLevels.add(botLevel);
    }

    public BotLevel getBotLevel(int level) {
        return botLevels.get(level);
    }

    public int getLevelForHumanBase(Map<BaseItemType, List<SyncBaseItem>> items) {
        int jeepCount = getCountForItemName(Constants.JEEP, items);
        if (jeepCount == 0) {
            return 0;
        } else if (jeepCount > 0 && jeepCount <= 2) {
            return 1;
        } else if (jeepCount > 2 && jeepCount <= 6) {
            return 2;
        } else if (jeepCount > 6 && jeepCount <= 11) {
            return 3;
        } else if (jeepCount > 11 && jeepCount <= 18) {
            return 4;
        } else if (jeepCount > 11 && jeepCount <= 25) {
            return 5;
        } else {
            return 6;
        }
    }

    private int getCountForItemName(String name, Map<BaseItemType, List<SyncBaseItem>> items) {
        for (Map.Entry<BaseItemType, List<SyncBaseItem>> baseItemTypeListEntry : items.entrySet()) {
            if (baseItemTypeListEntry.getKey().getName().equals(name)) {
                return baseItemTypeListEntry.getValue().size();
            }
        }
        return 0;
    }
}
