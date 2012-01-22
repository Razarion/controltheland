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

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.user.UserState;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:02:57
 */
public interface UserGuidanceService extends CommonUserGuidanceService {
    void promote(UserState userState, int newDbLevelId);

    DbLevel getDbLevel();

    DbLevel getDbLevel(UserState userState);

    DbLevel getDbLevelCms();

    void setLevelForNewUser(UserState userState);

    void activateLevels() throws LevelActivationException;

    void init2();

    void createBaseInQuestHub(UserState userState);

    void sendResurrectionMessage(SimpleBase simpleBase);

    LevelScope getLevelScope(SimpleBase simpleBase);

    GameFlow onTutorialFinished(Integer taskId);

    boolean isStartRealGame();

    CrudRootServiceHelper<DbQuestHub> getCrudQuestHub();
}
