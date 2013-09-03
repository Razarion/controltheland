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
import com.btxtech.game.jsre.client.ClientUserService;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class MessageDialog extends Dialog {
    private String message;
    private boolean showRegisterDialogButton;

    public MessageDialog(String title, String message) {
        this(title, message, false);
    }

    public MessageDialog(String title, String message, boolean showRegisterDialogButton) {
        super(title);
        this.message = message;
        this.showRegisterDialogButton = showRegisterDialogButton;
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        HTML messageWidget = new HTML(message, true);
        messageWidget.getElement().getStyle().setWidth(17, Style.Unit.EM);
        if (showRegisterDialogButton) {
            VerticalPanel verticalPanel = new VerticalPanel();
            verticalPanel.add(messageWidget);
            Button button = new Button(ClientI18nHelper.CONSTANTS.register(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    ClientUserService.getInstance().promptRegister();
                }
            });
            button.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
            verticalPanel.add(button);
            dialogVPanel.add(verticalPanel);
            verticalPanel.setCellHorizontalAlignment(button, HasHorizontalAlignment.ALIGN_CENTER);
        } else {
            dialogVPanel.add(messageWidget);
        }
    }
}
