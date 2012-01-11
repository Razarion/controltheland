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
import com.btxtech.game.jsre.common.CmsUtil;
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
@Deprecated
public class MenuDialog extends Dialog {
    public MenuDialog() {
        setShowCloseButton(true);
        getElement().getStyle().setWidth(300, Style.Unit.PX);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        FlexTable flexTable = new FlexTable();
        flexTable.setCellSpacing(6);

        addRow(flexTable, "UserPage (Market)", "user", getPredefinedUrls(CmsUtil.CmsPredefinedPage.USER_PAGE), CmsUtil.TARGET_USER_PAGE);
        addRow(flexTable, "Statistics", "chart", getPredefinedUrls(CmsUtil.CmsPredefinedPage.HIGH_SCORE), CmsUtil.TARGET_USER_INFO);
        addRow(flexTable, "Game instruction", "lifebuoy", getPredefinedUrls(CmsUtil.CmsPredefinedPage.INFO), CmsUtil.TARGET_USER_INFO);
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
        dialogVPanel.add(flexTable);
    }

    private void closeWindow() {
        Window.open(getPredefinedUrls(CmsUtil.CmsPredefinedPage.USER_PAGE), "_self", "");
    }

    private void addRow(FlexTable flexTable, String name, String icon, final String url, final String wndName) {
        addRow(flexTable, name, icon, new Runnable() {
            @Override
            public void run() {
                Window.open(url, wndName, "");
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

    private String getPredefinedUrls(CmsUtil.CmsPredefinedPage cmsPredefinedPage) {
        return Connection.getInstance().getGameInfo().getPredefinedUrls().get(cmsPredefinedPage);
    }
}
