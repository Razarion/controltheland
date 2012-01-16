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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;

/**
 * User: beat
 * Date: 18.12.2009
 * Time: 21:07:42
 */
public interface XpService {
    void onItemKilled(Base actorBase, SyncBaseItem killedItem);

    void onReward(SimpleBase simpleBase, int deltaXp);

    void onItemBuilt(SyncBaseItem builtItem);

    DbXpSettings getXpPointSettings();

    void saveXpPointSettings(DbXpSettings dbXpSettings);
}
