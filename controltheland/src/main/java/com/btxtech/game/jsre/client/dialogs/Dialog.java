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
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.List;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:19:00 PM
 */
public abstract class Dialog extends DialogBox {
    private static final String TOOL_TIP_CLOSE = "Close dialog";
    private boolean showCloseButton = true;

    protected Dialog(String title) {
        super(false, false);
        setText(title);
        setStyleName("dialogBox");
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
            Button button = new Button("Close", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    close();
                }
            });
            button.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
            button.setTitle(TOOL_TIP_CLOSE);
            dialogVPanel.add(button);
            dialogVPanel.setCellHorizontalAlignment(button, HasHorizontalAlignment.ALIGN_CENTER);
            button.setFocus(true);
        }
        center();
        getElement().getStyle().setZIndex(getZIndex());
    }

    protected int getZIndex() {
        return Constants.Z_INDEX_DIALOG;
    }


    public void close() {
        hide(true);
    }

    abstract protected void setupPanel(VerticalPanel dialogVPanel);


}
