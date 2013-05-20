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
public class StartNewBaseDialog extends Dialog {

    public StartNewBaseDialog() {
        super(ClientI18nHelper.CONSTANTS.newBase());
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalArgumentException("StartNewBaseDialog: only allowed if real game");
        }
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        Label label = new Label(ClientI18nHelper.CONSTANTS.startNewBase());
        dialogVPanel.add(label);
        label.getElement().getStyle().setWidth(17, Style.Unit.EM);
        Button button = new Button(ClientI18nHelper.CONSTANTS.startOver());
        dialogVPanel.add(new HTML("&nbsp;"));
        dialogVPanel.add(button);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
                Connection.getInstance().surrenderBase();
            }
        });
        dialogVPanel.setCellHorizontalAlignment(button, HasHorizontalAlignment.ALIGN_CENTER);
    }

}
