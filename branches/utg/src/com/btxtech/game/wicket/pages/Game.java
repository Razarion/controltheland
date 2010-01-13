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

package com.btxtech.game.wicket.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;

/**
 * User: beat
 * Date: Jun 1, 2009
 * Time: 12:10:57 AM
 */
public class Game extends WebPage {
    @SpringBean
    private UserTrackingService userTrackingService;

    @Override
    protected void onBeforeRender() {
        userTrackingService.gameStartup(GameStartupState.SERVER);
        super.onBeforeRender();
    }

}
