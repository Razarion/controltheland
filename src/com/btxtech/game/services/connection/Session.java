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

package com.btxtech.game.services.connection;

import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.utg.DbUserStage;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:45:30 PM
 */
public interface Session {
    Connection getConnection();

    void setConnection(Connection connection);

    String getSessionId();

    String getCookieId();

    String getUserAgent();

    User getUser();

    void setUser(User user);

    UserItemTypeAccess getUserItemTypeAccess();

    void setUserItemTypeAccess(UserItemTypeAccess userItemTypeAccess);

    void clearGame();

    void onJavaScriptDetected();

    boolean isJavaScriptDetected();

    DbUserStage getUserStage();

    void setDbUserStage(DbUserStage dbUserStage);
}
