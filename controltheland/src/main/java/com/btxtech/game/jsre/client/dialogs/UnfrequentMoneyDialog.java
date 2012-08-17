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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.HashMap;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class UnfrequentMoneyDialog extends UnfrequentDialog {
    private static HashMap<Type, UnfrequentMoneyDialog> dialogHashMap = new HashMap<Type, UnfrequentMoneyDialog>();
    private long lastShowTimeStamp = 0;  // Can not be converted to local variable

    protected UnfrequentMoneyDialog() {
        super(Type.NO_MONEY);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        HTML messageWidget = new HTML("You do not have enough money. You have to Collect more money.", true);
        messageWidget.getElement().getStyle().setWidth(17, Style.Unit.EM);
        dialogVPanel.add(messageWidget);
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE) {
            HTML newBase = new HTML("<br />You can also start a new base.", true);
            newBase.getElement().getStyle().setWidth(17, Style.Unit.EM);
            dialogVPanel.add(newBase);
            Button button = new Button("New Base", new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                close();
                                DialogManager.showDialog(new YesNoDialog("Start Over", "Start new base? You will lose the current base.", "Yes", new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent event) {
                                        Connection.getInstance().surrenderBase();
                                    }
                                }, "No", null), DialogManager.Type.PROMPTLY);
                            }
                        });
            dialogVPanel.add(button);
            dialogVPanel.setCellHorizontalAlignment(button, HasHorizontalAlignment.ALIGN_CENTER);
        }
    }

}
