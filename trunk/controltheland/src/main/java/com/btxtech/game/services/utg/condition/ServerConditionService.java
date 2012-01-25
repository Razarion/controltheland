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

package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.common.utg.ConditionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.backup.DbAbstractComparisonBackup;

import java.util.Map;

/**
 * User: beat
 * Date: 28.12.2010
 * Time: 18:16:08
 */
public interface ServerConditionService extends ConditionService<UserState, Integer> {
    DbAbstractComparisonBackup createBackup(DbUserState dbUserState, UserState userState);

    void restoreBackup(Map<DbUserState, UserState> userStates, ItemService itemService);

    void onTutorialFinished(UserState userState, int taskId);

    void onIncreaseXp(UserState userState, int xp);
}
