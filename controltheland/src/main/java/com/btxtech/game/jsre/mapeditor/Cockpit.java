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

import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.common.TerrainInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 9:30:48 PM
 */
public class Cockpit extends TopMapPanel implements ChangeHandler {
    public static final String HEIGHT = "600px";
    private static final String LIST_BOX_SELECTION_OPERATION = "Selection Operation";
    private static final String LIST_BOX_SURFACE = "Surface";
    private Map<Integer, FlexTable> imageGroup = new HashMap<Integer, FlexTable>();
    private FlexTable controlPanel;
    private int selectorRow;
    private ListBox placements;
    private ListBox zIndexSelector;
    private MapEditorModel mapEditorModel;
    private TerrainEditorConnection terrainEditorConnection;
    private ScrollPanel surfaceScroll;
    private Map<String, ScrollPanel> imageScrollMap = new HashMap<String, ScrollPanel>();

    public Cockpit() {
    }

    @Override
    protected Widget createBody() {
        controlPanel = new FlexTable();
        // Save Button
        final Button saveButton = new Button("Save");
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                saveButton.setEnabled(false);
                terrainEditorConnection.save(saveButton);
            }
        });
        controlPanel.setWidget(0, 0, saveButton);
        placements = new ListBox();
        placements.addChangeHandler(this);
        controlPanel.setWidget(1, 0, placements);
        placements.addItem(LIST_BOX_SELECTION_OPERATION);
        return controlPanel;
    }

    public void setupSurfaceSelector(TerrainInfo terrainInfo) {
        FlexTable surfaceSelector = new FlexTable();
        surfaceSelector.setCellSpacing(5);
        surfaceSelector.setCellPadding(3);
        surfaceSelector.addStyleName("tile-selector");
        surfaceScroll = new ScrollPanel(surfaceSelector);
        surfaceScroll.setAlwaysShowScrollBars(true);
        surfaceScroll.setHeight(HEIGHT);
        placements.addItem(LIST_BOX_SURFACE);
        // Fill surface images
        for (SurfaceImage surfaceImage : terrainInfo.getSurfaceImages()) {
            surfaceSelector.setWidget(surfaceSelector.getRowCount(), 0, new SurfaceSelectorItem(surfaceImage, mapEditorModel));
        }
    }

    public void setupImageSelectors(TerrainInfo terrainInfo, Map<String, Collection<Integer>> terrainImageGroups) {
        for (Map.Entry<String, Collection<Integer>> entry : terrainImageGroups.entrySet()) {
            setupImageGroupSelector(entry);
        }
        // Z Index
        zIndexSelector = new ListBox();
        zIndexSelector.addItem("Layer 1", TerrainImagePosition.ZIndex.LAYER_1.name());
        zIndexSelector.addItem("Layer 2", TerrainImagePosition.ZIndex.LAYER_2.name());
        zIndexSelector.setSelectedIndex(0);
        zIndexSelector.setEnabled(false);
        zIndexSelector.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                mapEditorModel.setActiveLayer(zIndexSelector.getSelectedIndex() == 0 ? MapEditorModel.ActiveLayer.IMAGE_LAYER_1 : MapEditorModel.ActiveLayer.IMAGE_LAYER_2);
            }
        });
        controlPanel.setWidget(controlPanel.getRowCount(), 0, zIndexSelector);
        selectorRow = controlPanel.getRowCount();
        // Fill Images
        for (TerrainImage terrainImage : terrainInfo.getTerrainImages()) {
            FlexTable terrainImageSelector = imageGroup.get(terrainImage.getId());
            if (terrainImageSelector == null) {
                throw new IllegalArgumentException("No group for terrain image: " + terrainImage.getId());
            }
            terrainImageSelector.setWidget(terrainImageSelector.getRowCount(), 0, new TerrainImageSelectorItem(terrainImage, this, mapEditorModel));
        }
    }

    private void setupImageGroupSelector(Map.Entry<String, Collection<Integer>> imageGroupEntry) {
        FlexTable imageSelector = new FlexTable();
        imageSelector.setCellSpacing(5);
        imageSelector.setCellPadding(3);
        imageSelector.addStyleName("tile-selector");
        ScrollPanel imageScroll = new ScrollPanel(imageSelector);
        imageScroll.setAlwaysShowScrollBars(true);
        imageScroll.setHeight(HEIGHT);
        for (Integer imageId : imageGroupEntry.getValue()) {
            imageGroup.put(imageId, imageSelector);
        }
        imageScrollMap.put(imageGroupEntry.getKey(), imageScroll);
        placements.addItem(imageGroupEntry.getKey());
    }

    private Widget createSelectionOperationPanel() {
        FlexTable flexTable = new FlexTable();
        CheckBox surfaceCheckBox = new CheckBox("Surface");
        surfaceCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                mapEditorModel.setSelectionModeSurface(event.getValue());
            }
        });
        surfaceCheckBox.setValue(mapEditorModel.isSelectionModeSurface());
        flexTable.setWidget(0, 0, surfaceCheckBox);
        CheckBox surfaceLayer1 = new CheckBox("Layer 1");
        surfaceLayer1.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                mapEditorModel.setSelectionModeLayer1(event.getValue());
            }
        });
        surfaceLayer1.setValue(mapEditorModel.isSelectionModeLayer1());
        flexTable.setWidget(1, 0, surfaceLayer1);
        CheckBox surfaceLayer2 = new CheckBox("Layer 2");
        surfaceLayer2.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                mapEditorModel.setSelectionModeLayer2(event.getValue());
            }
        });
        surfaceLayer2.setValue(mapEditorModel.isSelectionModeLayer2());
        flexTable.setWidget(2, 0, surfaceLayer2);
        flexTable.setWidget(3, 0, new Button("Delete", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mapEditorModel.deleteTerrainImageSurfaceGroup();
            }
        }));
        return flexTable;
    }

    public void setMapEditorModel(MapEditorModel mapEditorModel) {
        this.mapEditorModel = mapEditorModel;
    }

    public void setTerrainEditorConnection(TerrainEditorConnection terrainEditorConnection) {
        this.terrainEditorConnection = terrainEditorConnection;
    }

    @Override
    public void onChange(ChangeEvent event) {
        String selectedIndex = placements.getItemText(placements.getSelectedIndex());
        if (LIST_BOX_SELECTION_OPERATION.equals(selectedIndex)) {
            controlPanel.setWidget(selectorRow, 0, createSelectionOperationPanel());
            zIndexSelector.setEnabled(false);
            mapEditorModel.setSelectionMode(true);
        } else if (LIST_BOX_SURFACE.equals(selectedIndex)) {
            controlPanel.setWidget(selectorRow, 0, surfaceScroll);
            zIndexSelector.setEnabled(false);
            mapEditorModel.setActiveLayer(MapEditorModel.ActiveLayer.SURFACE);
            mapEditorModel.setSelectionMode(false);
        } else if (imageScrollMap.containsKey(selectedIndex)) {
            controlPanel.setWidget(selectorRow, 0, imageScrollMap.get(selectedIndex));
            zIndexSelector.setEnabled(true);
            mapEditorModel.setActiveLayer(zIndexSelector.getSelectedIndex() == 0 ? MapEditorModel.ActiveLayer.IMAGE_LAYER_1 : MapEditorModel.ActiveLayer.IMAGE_LAYER_2);
            mapEditorModel.setSelectionMode(false);
        }
    }
}
