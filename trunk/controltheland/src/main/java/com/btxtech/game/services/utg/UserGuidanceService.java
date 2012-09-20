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
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.dialogs.quest.QuestOverview;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserState;

import java.util.Map;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:02:57
 */
public interface UserGuidanceService extends CommonUserGuidanceService {
    void promote(UserState userState, int newDbLevelId);

    DbLevel getDbLevel();

    DbLevel getDbLevel(UserState userState);

    DbLevel getDbLevel(int levelId);

    DbLevel getDbLevelCms();

    void setLevelForNewUser(UserState userState);

    void activateLevels() throws LevelActivationException;

    void sendResurrectionMessage(SimpleBase simpleBase);

    LevelScope getLevelScope(UserState userState);

    GameFlow onTutorialFinished(int levelTaskId);

    boolean isStartRealGame();

    int getDefaultLevelTaskId();

    CrudRootServiceHelper<DbLevel> getDbLevelCrud();

    void onRemoveUserState(UserState userState);

    void restoreBackup(Map<DbUserState, UserState> userStates);

    void createAndAddBackup(DbUserState dbUserState, UserState userState);

    InvalidLevelStateException createInvalidLevelState();

    void fillRealGameInfo(RealGameInfo realGameInfo);

    void activateQuest(int dbLevelTaskId);

    QuestOverview getQuestOverview();

    int getXp2LevelUp(UserState userState);
}
