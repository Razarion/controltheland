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

package com.btxtech.game.services.statistics;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.user.UserState;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 1:25:35 PM
 */
public interface StatisticsService {

    void onMoneyEarned(SimpleBase simpleBase, double amount);

    void onMoneySpent(SimpleBase simpleBase, double amount);

    void onItemKilled(SyncBaseItem targetItem, SimpleBase actorBase);

    void onItemCreated(SyncBaseItem syncBaseItem);

    void onBaseKilled(SimpleBase target, SimpleBase actor);

    void onLevelPromotion(UserState userState);

    CrudRootServiceHelper<DbStatisticsEntry> getDayStatistics();

    CrudRootServiceHelper<DbStatisticsEntry> getWeekStatistics();

    CrudRootServiceHelper<DbStatisticsEntry> getAllTimeStatistics();
}
