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

package com.btxtech.game.jsre.mapview.territory;

import com.btxtech.game.jsre.client.TopMapPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 26.05.2010
 * Time: 10:52:18
 */
public class TerritoryCockpit extends TopMapPanel {
    private TerritoryEditModel territoryEditModel;
    private Label territoryName;
    private Button saveButton;

    public TerritoryCockpit(TerritoryEditModel territoryEditModel) {
        this.territoryEditModel = territoryEditModel;
    }

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        territoryName = new Label();
        verticalPanel.add(territoryName);

        saveButton = new Button("Save");
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                territoryEditModel.save();
            }
        });
        verticalPanel.add(saveButton);
        return verticalPanel;
    }

    public void enableSaveButton() {
        saveButton.setEnabled(true);
    }

    public void disableSaveButton() {
        saveButton.setEnabled(false);
    }

    public void setTerritoryName(String name) {
        territoryName.setText(name);
    }
}
