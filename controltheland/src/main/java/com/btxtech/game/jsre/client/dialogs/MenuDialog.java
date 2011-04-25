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
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 25.03.2010
 * Time: 17:24:31
 */
public class MenuDialog extends Dialog {
    public MenuDialog() {
        setShowCloseButton(true);
        getElement().getStyle().setWidth(300, Style.Unit.PX);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        FlexTable flexTable = new FlexTable();
        flexTable.setCellSpacing(6);

        addRow(flexTable, "Market", "box", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.market.MarketPage");
        if (Connection.getInstance().isRegistered()) {
            addRow(flexTable, "User Page", "user", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.user.UserPage");
        }
        addRow(flexTable, "Statistics", "chart", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.statistics.StatisticsPage");
        addRow(flexTable, "Game instruction", "lifebuoy", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.forum.CategoryView&id=2");
        addRow(flexTable, "Quit", "control-power", new Runnable() {
            @Override
            public void run() {
                Connection.getInstance().closeConnection();
                closeWindow();
            }
        });
        if (Connection.getInstance().isRegistered()) {
            addRow(flexTable, "Surrender Base", "cross", new Runnable() {
                @Override
                public void run() {
                    YesNoDialog yesNoDialog = new YesNoDialog("Do you really want to surrender and lose your base?", new Runnable() {

                        @Override
                        public void run() {
                            Connection.getInstance().surrenderBase();
                            closeWindow();
                        }
                    });
                    DialogManager.showDialog(yesNoDialog, DialogManager.Type.PROMPTLY);
                }
            });
        }
        addRow(flexTable, "Startup screen", "clock-select", new Runnable() {

            @Override
            public void run() {
                close();
                StartupScreen.getInstance().showStartScreen();
            }
        });

        dialogVPanel.add(flexTable);
    }

    private void closeWindow() {
        if (Connection.getInstance().isRegistered()) {
            Window.open("/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.user.UserPage", "_self", "");
        } else {
            Window.open("/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.info.Info", "_self", "");
        }
    }

    private void addRow(FlexTable flexTable, String name, String icon, final String url) {
        addRow(flexTable, name, icon, new Runnable() {
            @Override
            public void run() {
                Window.open(url, "_blank", "");
            }
        });
    }

    private void addRow(FlexTable flexTable, String name, String icon, final Runnable runnable) {
        int row = flexTable.getRowCount();
        Image image = ImageHandler.getIcon16(icon);
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                runnable.run();
            }
        });
        image.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        flexTable.setWidget(row, 0, image);

        Label label = new Label(name);
        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                runnable.run();
            }
        });
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        flexTable.setWidget(row, 1, label);
    }
}
