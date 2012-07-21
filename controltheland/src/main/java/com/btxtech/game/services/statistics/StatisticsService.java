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

import com.btxtech.game.jsre.client.dialogs.highscore.CurrentStatisticEntryInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserState;

import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: Sep 13, 2009
 * Time: 1:25:35 PM
 */
public interface StatisticsService {
    void onItemKilled(SyncBaseItem targetItem, SimpleBase actorBase);

    void onItemCreated(SyncBaseItem syncBaseItem);

    void onBaseKilled(SimpleBase target, SimpleBase actor);

    ReadonlyListContentProvider<CurrentStatisticEntry> getCmsCurrentStatistics();

    List<CurrentStatisticEntryInfo> getInGameCurrentStatistics();

    void restoreBackup(Map<DbUserState, UserState> userStates);

    void createAndAddBackup(DbUserState dbUserState, UserState userState);

    void onRemoveUserState(UserState userState);

    StatisticsEntry getStatisticsEntryAccess(UserState userState);
}
