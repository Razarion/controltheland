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

import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:02:57
 */
public interface UserGuidanceService {
    List<DbLevel> getDbLevels();

    void deleteDbLevel(DbLevel dbLevel);

    void addDbLevel();

    void saveDbLevels(List<DbLevel> dbLevels);

    void saveDbLevel(DbLevel dbLevel);

    void moveUpDbLevel(DbLevel dbLevel);

    void moveDownDbLevel(DbLevel dbLevel);

    Level getLevel4Base();

    String getMissionTarget4NextLevel(Base base);

    void onSyncBaseItemCreated(SyncBaseItem syncBaseItem);

    void setupLevel4NewBase(Base base);

    Level getLevelToRunMissionTarget();

    void onTutorialTerminated();

    void onIncreaseXp(Base base, int xp);

    void onBaseDeleted(Base base);

    void onMoneyIncrease(Base base);    

    void onItemKilled(Base actorBase);

    void restore(Collection<Base> bases);

    boolean isTutorialRequired();

    void onTutorialFinished();
}
