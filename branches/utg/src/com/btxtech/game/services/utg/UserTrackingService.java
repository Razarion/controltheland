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

import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.services.utg.UserAction;
import com.btxtech.game.wicket.pages.basepage.BasePage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 12.01.2010
 * Time: 22:04:31
 */
public interface UserTrackingService {
    void newSession(UserDetails userDetails);

    void pageAccess(BasePage basePage);

    void gameStartup(GameStartupState server, Date timeStamp);

    void saveUserActions(ArrayList<UserAction> userActions);

    List<VisitorInfo> getVisitorInfos();
}
