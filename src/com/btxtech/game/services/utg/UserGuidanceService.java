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

import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.common.CrudServiceHelper;
import com.btxtech.game.services.user.UserState;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:02:57
 */
public interface UserGuidanceService {
    GameStartupSeq getColdStartupSeq();

    void promote(UserState userState);

    DbRealGameLevel getDbLevel();

    DbRealGameLevel getDbLevel(SimpleBase simpleBase);

    DbAbstractLevel getDbAbstractLevel();

    DbAbstractLevel getDbLevel(String levelName);

    String getDbLevelHtml();

    void restore(Collection<Base> bases);

    void setLevelForNewUser(UserState userState);

    List<DbAbstractLevel> getDbLevels();

    void saveDbLevels(List<DbAbstractLevel> dbAbstractLevels);

    void saveDbLevel(DbAbstractLevel dbAbstractLevel);

    void activateLevels();

    void init2();

    void createDbLevel();

    void deleteDbLevel(DbAbstractLevel dbAbstractLevel);

    CrudServiceHelper<DbAbstractLevel> getDbLevelCrudServiceHelper();
}
