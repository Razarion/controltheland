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
import com.btxtech.game.jsre.client.GameEngineMode;
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
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        VerticalPanel verticalPanel = new VerticalPanel();
        if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE) {
            verticalPanel.add(new HTML("All alliances you have formed with other players.<br />New alliances can be formed in the speech<br />bubble of enemy units/structures."));
        } else {
            verticalPanel.add(new HTML("All alliances you have formed with other players.<br /><b>Only available an this planet.</b>"));
        }
        allianceTable = new FlexTable();
        verticalPanel.add(allianceTable);
        dialogVPanel.add(verticalPanel);
        Connection.getInstance().getAllAlliances(this);
    }

    public void onAlliancesReceived(Collection<String> alliances) {
        if (allianceTable == null || !isVisible()) {
            return;
        }
        for (final String alliance : alliances) {
            int row = allianceTable.getRowCount() + 1;
            allianceTable.setText(row, 1, alliance);
            allianceTable.setWidget(row, 2, new Button("Break", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Connection.getInstance().breakAlliance(alliance);
                    close();
                }
            }));
        }
    }
}
