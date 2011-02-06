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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 26.03.2010
 * Time: 12:27:53
 */
public class YesNoDialog extends Dialog {
    private String message;
    private Runnable onYesBtn;


    public YesNoDialog(String message, Runnable onYesBtn) {
        this.message = message;
        this.onYesBtn = onYesBtn;
        setShowCloseButton(false);
        setupDialog();
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new HTML(message + "<br>&nbsp;", false));
        Button yes = new Button("Yes");
        yes.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
                onYesBtn.run();
            }
        });
        Button no = new Button("No");
        no.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                close();
            }
        });
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setWidth("100%");
        horizontalPanel.add(no);
        horizontalPanel.setCellHorizontalAlignment(no, HasHorizontalAlignment.ALIGN_CENTER);
        horizontalPanel.add(yes);
        horizontalPanel.setCellHorizontalAlignment(yes, HasHorizontalAlignment.ALIGN_CENTER);
        dialogVPanel.add(horizontalPanel);
    }

    public static void show(String message, Runnable onYesBtn) {
        new YesNoDialog(message, onYesBtn);
    }
}
