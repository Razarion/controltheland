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
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 9:30:48 PM
 */
public class Cockpit extends TopMapPanel {
    public static final String HEIGHT = "600px";
    private TerrainImageSelectorItem selection;
    private FlexTable terrainImageSelector;
    private FlexTable surfaceSelector;
    private ToggleButton deleteButton;
    private TerrainEditorAsync terrainEditor;
    private TerrainImageModifier terrainImageModifier;
    private SurfaceModifier surfaceModifier;
    private ScrollPanel terrainImageScroll;
    private ScrollPanel surfaceScroll;

    public Cockpit(TerrainEditorAsync terrainEditor) {
        this.terrainEditor = terrainEditor;
    }

    @Override
    protected Widget createBody() {
        terrainImageModifier = new TerrainImageModifier(this);
        surfaceModifier = new SurfaceModifier(this);

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
                terrainEditor.saveTerrainImagePositions(TerrainView.getInstance().getTerrainHandler().getTerrainImagePositions(),
                        TerrainView.getInstance().getTerrainHandler().getSurfaceRects(),
                        new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                GwtCommon.handleException(throwable);
                                saveButton.setEnabled(true);
                            }

                            @Override
                            public void onSuccess(Void aVoid) {
                                saveButton.setEnabled(true);
                            }
                        });

            }
        });
        controlPanel.setWidget(1, 0, saveButton);

        // Tab panel
        TabPanel tp = new TabPanel();
        tp.add(setupSurfaces(), "Surface");
        tp.add(setupTerrainImages(), "Images");
        tp.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> integerSelectionEvent) {
                editModeChanged(integerSelectionEvent.getSelectedItem());
            }
        });
        tp.selectTab(0);
        controlPanel.setWidget(2, 0, tp);
        return controlPanel;
    }

    private void editModeChanged(int mode) {

        switch (mode) {
            case 0:
                // Surface
                MapWindow.getInstance().setTerrainMouseMoveListener(surfaceModifier);
                break;
            case 1:
                // Terrain Images
                MapWindow.getInstance().setTerrainMouseMoveListener(terrainImageModifier);
                break;
            case 2:
                // Territory
                break;

        }
    }

    private Widget setupSurfaces() {
        surfaceSelector = new FlexTable();
        surfaceSelector.setCellSpacing(5);
        surfaceSelector.setCellPadding(3);
        surfaceSelector.addStyleName("tile-selector");
        surfaceScroll = new ScrollPanel(surfaceSelector);
        surfaceScroll.setAlwaysShowScrollBars(true);
        return surfaceScroll;
    }

    private Widget setupTerrainImages() {
        terrainImageSelector = new FlexTable();
        terrainImageSelector.setCellSpacing(5);
        terrainImageSelector.setCellPadding(3);
        terrainImageSelector.addStyleName("tile-selector");
        terrainImageScroll = new ScrollPanel(terrainImageSelector);
        terrainImageScroll.setAlwaysShowScrollBars(true);
        return terrainImageScroll;
    }

    public void fillTerrainImages(Collection<TerrainImage> terrainImages) {
        for (TerrainImage terrainImage : terrainImages) {
            terrainImageSelector.setWidget(terrainImageSelector.getRowCount(), 0, new TerrainImageSelectorItem(terrainImage));
        }
        terrainImageScroll.setHeight(HEIGHT);
    }

    public void fillSurfaces(Collection<SurfaceImage> surfaceImages) {
        for (SurfaceImage surfaceImage : surfaceImages) {
            surfaceSelector.setWidget(surfaceSelector.getRowCount(), 0, new SurfaceSelectorItem(surfaceImage));
        }
        surfaceScroll.setHeight(HEIGHT);
    }

    public void onSelectionChanged(TerrainImageSelectorItem newSelection) {
        if (selection == newSelection) {
            return;
        }
        if (selection != null) {
            selection.setSelected(false);
        }

        selection = newSelection;
        selection.setSelected(true);
    }

    public boolean isDeleteModus() {
        return deleteButton.isDown();
    }
}
