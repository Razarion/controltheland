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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class MessageDialog extends Dialog {
    private String message;
    private boolean showRegisterDialogButton;

    public MessageDialog(String message) {
        this.message = message;
    }

    public MessageDialog(String message, boolean showRegisterDialogButton) {
        this.message = message;
        this.showRegisterDialogButton = showRegisterDialogButton;
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        if (showRegisterDialogButton) {
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.add(new HTML(message, false));
            verticalPanel.add(new Button("Register", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    DialogManager.showDialog(new RegisterDialog(), DialogManager.Type.PROMPTLY);
                }
            }));
            dialogVPanel.add(verticalPanel);
        } else {
            dialogVPanel.add(new HTML(message, false));
        }
    }
}
