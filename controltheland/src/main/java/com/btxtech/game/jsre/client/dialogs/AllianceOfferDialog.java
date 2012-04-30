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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class AllianceOfferDialog extends Dialog {
    private String userName;

    public AllianceOfferDialog(String userName) {
        super("Alliance offer");
        this.userName = userName;
        setShowCloseButton(false);
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        VerticalPanel verticalPanel = new VerticalPanel();
        StringBuilder builder = new StringBuilder();
        builder.append("The user <b>");
        builder.append(userName);
        builder.append("</b> has offered you an alliance.");
        HTML html = new HTML(builder.toString());
        html.getElement().getStyle().setWidth(17, Style.Unit.EM);
        verticalPanel.add(html);
        HorizontalPanel buttonPanel = new HorizontalPanel();
        verticalPanel.add(buttonPanel);
        buttonPanel.getElement().getStyle().setMarginTop(20, Style.Unit.PX);
        buttonPanel.add(new Button("Accept", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Connection.getInstance().acceptAllianceOffer(userName);
                close();
            }
        }));
        buttonPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));  // Make gab between buttons
        buttonPanel.add(new Button("Reject", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Connection.getInstance().rejectAllianceOffer(userName);
                close();
            }
        }));
        verticalPanel.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_CENTER);
        dialogVPanel.add(verticalPanel);
    }
}
