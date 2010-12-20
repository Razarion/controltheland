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

import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.widgetideas.client.GlassPanel;

/**
 * User: beat
 * Date: 14.12.2010
 * Time: 18:36:49
 */
public class UserStageDialog extends Dialog {
    private String html;
    private GlassPanel glassPanel;

    public UserStageDialog(String html) {
        this.html = html;
        setShowCloseButton(true);
        setupDialog();
        getElement().getStyle().setWidth(300, Style.Unit.PX);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_USER_STAGE_DIALOG);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new HTML(html));
    }

    public static void showDialog(String html) {
        final UserStageDialog userStageDialog = new UserStageDialog(html);
        userStageDialog.glassPanel = new GlassPanel(false);
        userStageDialog.glassPanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_GLASS_PANEL);
        RootPanel.get().add(userStageDialog.glassPanel, 0, 0);
        userStageDialog.setPopupPositionAndShow(new PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = (Window.getClientHeight() - offsetHeight) / 2;
                userStageDialog.setPopupPosition(left, top);
            }
        });
    }

    @Override
    public void close() {
        RootPanel.get().remove(glassPanel);
        super.close();
    }
}