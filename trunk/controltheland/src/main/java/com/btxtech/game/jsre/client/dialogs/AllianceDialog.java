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
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;

/**
 * User: beat
 * Date: Jul 2, 2009
 * Time: 3:23:47 PM
 */
public class AllianceDialog extends Dialog {
    private FlexTable allianceTable;

    public AllianceDialog() {
        super(ClientI18nHelper.CONSTANTS.alliances());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        VerticalPanel verticalPanel = new VerticalPanel();
        HTML html;
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE) {
            html = new HTML(ClientI18nHelper.CONSTANTS.alliancesAll());
        } else {
            html = new HTML(ClientI18nHelper.CONSTANTS.alliancesAllNotAvailable());
        }
        html.getElement().getStyle().setWidth(17, Style.Unit.EM);
        verticalPanel.add(html);

        allianceTable = new FlexTable();
        verticalPanel.add(allianceTable);
        dialogVPanel.add(verticalPanel);
        Connection.getInstance().getAllAlliances(this);
    }

    public void onAlliancesReceived(Collection<String> alliances) {
        if (allianceTable == null || !isVisible()) {
            return;
        }
        if (alliances == null || alliances.isEmpty()) {
            allianceTable.setText(1, 1, ClientI18nHelper.CONSTANTS.alliancesNo());
        } else {
            for (final String alliance : alliances) {
                int row = allianceTable.getRowCount() + 1;
                allianceTable.setText(row, 1, alliance);
                Button breakButton = new Button(ClientI18nHelper.CONSTANTS.breakAlliance(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Connection.getInstance().breakAlliance(alliance);
                        close();
                    }
                });
                breakButton.getElement().getStyle().setPaddingLeft(3, Style.Unit.PX);
                breakButton.getElement().getStyle().setPaddingRight(3, Style.Unit.PX);
                breakButton.setWidth("auto");
                allianceTable.setWidget(row, 2, breakButton);
            }
        }
    }
}
