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

package com.btxtech.game.jsre.client.dialogs;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class StartNewBaseDialog {
    public static void show() {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalArgumentException("StartNewBaseDialog: only allowed if real game");
        }
        YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.newBase(),
                ClientI18nHelper.CONSTANTS.startNewBase(),
                ClientI18nHelper.CONSTANTS.startOver(),
                new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Connection.getInstance().surrenderBase(null);
                    }
                }, ClientI18nHelper.CONSTANTS.close(), null);
        DialogManager.showDialog(yesNoDialog, DialogManager.Type.STACK_ABLE);
    }
}
