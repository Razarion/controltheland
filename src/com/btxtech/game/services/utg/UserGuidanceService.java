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

package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import java.util.List;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:02:57
 */
public interface UserGuidanceService {
    void createMissionTarget(Id attacker) throws NoSuchItemTypeException, ItemDoesNotExistException;

    void createMissionMoney(Id harvester) throws NoSuchItemTypeException, ItemDoesNotExistException;

    List<DbLevel> getDbLevels();

    void deleteDbLevel(DbLevel dbLevel);

    void addDbLevel();

    void saveDbLevels(List<DbLevel> dbLevels);

    void saveDbLevel(DbLevel dbLevel);

    void moveUpDbLevel(DbLevel dbLevel);

    void moveDownDbLevel(DbLevel dbLevel);

    String getMissionTarget4NextLevel();

    void onSyncBaseItemCreated(SyncBaseItem syncBaseItem);

    void setupLevel4NewBase(Base base);

    void tutorialTerminated();

    void onIncreaseXp(Base base, int xp);
}
