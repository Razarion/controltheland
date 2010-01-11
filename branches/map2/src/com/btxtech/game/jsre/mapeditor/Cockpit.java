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

package com.btxtech.game.jsre.mapeditor;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 9:30:48 PM
 */
public class Cockpit extends TopMapPanel {
    private TileSelectorItem selection;
    private FlexTable tileSelector;
    private ToggleButton deleteButton;
    private GameEditorAsync gameEditor;

    public Cockpit(GameEditorAsync gameEditor) {
        this.gameEditor = gameEditor;
    }

    @Override
    protected Widget createBody() {
        FlexTable controlPanel = new FlexTable();
        // Dele Button
        deleteButton = new ToggleButton("Delete");
        controlPanel.setWidget(0, 0, deleteButton);
        // Save Button
        final Button saveButton = new Button("Save");
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                saveButton.setEnabled(false);
                gameEditor.saveTerrainImagePositions(TerrainView.getInstance().getTerrainHandler().getTerrainImagePosition(),  new AsyncCallback<List<TerrainImagePosition>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GwtCommon.handleException(throwable);
                        saveButton.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(List<TerrainImagePosition> terrainImagePositions) {
                        saveButton.setEnabled(true);
                    }
                });

            }
        });
        controlPanel.setWidget(1, 0, saveButton);

        // Tile Panel
        tileSelector = new FlexTable();
        tileSelector.setCellSpacing(5);
        tileSelector.setCellPadding(3);
        tileSelector.addStyleName("tile-selector");
        controlPanel.setWidget(2, 0, tileSelector);
        return controlPanel;
    }

    public void setupTiles(Collection<Integer> tileIds) {
        for (Integer tileId : tileIds) {
            int numRows = tileSelector.getRowCount();
            tileSelector.setWidget(numRows, 0, new TileSelectorItem(this, tileId));
        }
    }

    public void onSelectionChanged(TileSelectorItem newSelection) {
        if (selection == newSelection) {
            return;
        }
        if (selection != null) {
            selection.setSelected(false);
        }

        selection = newSelection;
        selection.setSelected(true);
    }

    public int getSelection() {
        if (selection == null) {
            return -1;
        }
        return selection.getImageId();
    }

    public boolean isDeleteModus() {
        return deleteButton.isDown();
    }
}
