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
import com.btxtech.game.services.user.User;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 29.01.2010
 * Time: 22:02:57
 */
public interface UserGuidanceService {
    GameStartupSeq getColdStartupSeq();

    void promote(User user);

    DbScope getDbScope();

    DbLevel getDbLevel();

    DbLevel getDbLevel(SimpleBase simpleBase);

    DbLevel getDbLevel(String levelName);

    String getDbLevelHtml();

    void restore(Collection<Base> bases);

    void setLevelForNewUser(User user);

    List<DbLevel> getDbLevels();

    void saveDbLevels(List<DbLevel> dbLevels);

    void activateLevels();
}
