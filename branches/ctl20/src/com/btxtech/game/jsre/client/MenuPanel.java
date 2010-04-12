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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 25.03.2010
 * Time: 17:24:31
 */
public class MenuPanel extends TopMapPanel {
    private FlexTable flexTable;

    @Override
    protected Widget createBody() {
        flexTable = new FlexTable();
        flexTable.setCellSpacing(6);

        addRow("Market", "box", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.market.MarketPage");
        if (Connection.getInstance().isRegistered()) {
            addRow("User Page", "user", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.user.UserPage");
        }
        addRow("Statistics", "chart", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.statistics.StatisticsPage");
        addRow("Game instruction", "lifebuoy", "/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.forum.CategoryView&id=2");
        addRow("Quit", "control-power", new Runnable() {
            @Override
            public void run() {
                Connection.getInstance().closeConnection();
                closeWindow();
            }
        });
        if (Connection.getInstance().isRegistered()) {
            addRow("Surrender Base", "cross", new Runnable() {
                @Override
                public void run() {
                    YesNoDialog.show("Surrender", "Do you really want to surrender and lose your base?", new Runnable() {

                        @Override
                        public void run() {
                            Connection.getInstance().surrenderBase();
                            closeWindow();
                        }
                    });
                }
            });
        }
        flexTable.getFlexCellFormatter().setColSpan(6, 0, 2);
        flexTable.getFlexCellFormatter().setAlignment(6, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
        Button button = new Button("Close");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
            }
        });
        flexTable.setWidget(6, 0, button);
        flexTable.getElement().getStyle().setColor("orange");
        return flexTable;
    }

    private void closeWindow() {
        if (Connection.getInstance().isRegistered()) {
            Window.open("/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.user.UserPage", "_self", "");
        } else {
            Window.open("/?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.info.Info", "_self", "");
        }
    }

    private void addRow(String name, String icon, final String url) {
        addRow(name, icon, new Runnable() {
            @Override
            public void run() {
                Window.open(url, "_blank", "");
            }
        });
    }

    private void addRow(String name, String icon, final Runnable runnable) {
        int row = flexTable.getRowCount();
        Image image = ImageHandler.getIcon16(icon);
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                runnable.run();
                close();
            }
        });
        image.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        flexTable.setWidget(row, 0, image);

        Label label = new Label(name);
        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                runnable.run();
                close();
            }
        });
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        flexTable.setWidget(row, 1, label);
    }

}
