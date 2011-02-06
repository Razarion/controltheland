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

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class MessageDialog extends Dialog {
    private String message;

    public MessageDialog(String message) {
        this.message = message;
        setupDialog();
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new HTML(message, false));
    }

    public static void show(String message) {
        new MessageDialog(message);
    }

    public static void showOnCursorPosition(String message, final MouseDownEvent event) {
        final MessageDialog messageDialog = new MessageDialog(message);
        messageDialog.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = event.getClientX() - offsetWidth / 2;
                int top = event.getClientY() - offsetHeight / 2;
                messageDialog.setPopupPosition(left, top);
            }
        });

    }

}
