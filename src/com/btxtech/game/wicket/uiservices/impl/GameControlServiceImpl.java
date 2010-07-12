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

package com.btxtech.game.wicket.uiservices.impl;

import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.base.GameFullException;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.entergame.StartGamePage;
import com.btxtech.game.wicket.pages.user.UserPage;
import com.btxtech.game.wicket.uiservices.GameControlService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 26.03.2010
 * Time: 23:57:19
 */
@Component("gameControlService")
public class GameControlServiceImpl implements GameControlService {
    private Log log = LogFactory.getLog(GameControlServiceImpl.class);
    @Autowired
    private BaseService baseService;
    @Autowired
    private UserService userService;

    @Override
    public Class<? extends org.apache.wicket.Page> getEnterGamePage(boolean isInUserPage) throws GameFullException {
        try {
            if (userService.isLoggedin()) {
                if (isInUserPage) {
                    if (baseService.getBaseForLoggedInUser() != null) {
                        baseService.continueBase();
                        return Game.class;
                    } else {
                        return StartGamePage.class;
                    }
                } else {
                    return UserPage.class;
                }
            } else {
                baseService.createNewBase();
                return Game.class;
            }
        } catch (GameFullException e) {
            log.error("", e);
            throw e;
        } catch (Throwable t) {
            log.error("", t);
            throw new RuntimeException(t);
        }
    }

}
