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

import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:19:00 PM
 */
public abstract class Dialog extends DialogBox {
    private boolean showCloseButton = true;

    protected Dialog() {
        setStyleName("ctl-DialogBox");        
    }

    protected void setShowCloseButton(boolean showCloseButton) {
        this.showCloseButton = showCloseButton;
    }

    protected void setupDialog() {
        setAnimationEnabled(true);
        VerticalPanel dialogVPanel = new VerticalPanel();
        setupPanel(dialogVPanel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        setWidget(dialogVPanel);
        if (showCloseButton) {
            final ExtendedCustomButton closeButton = new ExtendedCustomButton("/images/gwtdialog/closeButton-up.png", "/images/gwtdialog/closeButton-down.png", false, new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    close();
                }
            });
            dialogVPanel.add(closeButton);
            dialogVPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_CENTER);
            closeButton.setFocus(true);
        }
        center();
        getElement().getStyle().setZIndex(Constants.Z_INDEX_DIALOG);
    }

    public void close() {
        hide(true);
    }

    abstract protected void setupPanel(VerticalPanel dialogVPanel);
}
