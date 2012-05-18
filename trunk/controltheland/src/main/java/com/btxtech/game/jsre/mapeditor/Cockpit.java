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
import com.btxtech.game.jsre.client.control.task.SimpleDeferredStartup;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: Sep 2, 2009
 * Time: 9:30:48 PM
 */
public class Cockpit extends TopMapPanel {
    public static final String HEIGHT = "600px";
    private static final String RADIO_BUTTON_GROUP = "RadioButtonGroup";
    private TerrainImageSelectorItem selection;
    private FlexTable surfaceSelector;
    private ToggleButton deleteButton;
    private TerrainEditorAsync terrainEditor;
    private int terrainId;
    private TerrainImageModifier terrainImageModifier;
    private SurfaceModifier surfaceModifier;
    private Map<Integer, FlexTable> imageGroup = new HashMap<Integer, FlexTable>();
    private FlexTable controlPanel;
    private int selectorRow;
    private ListBox zIndexSelector;

    public Cockpit(TerrainEditorAsync terrainEditor, int terrainId) {
        this.terrainEditor = terrainEditor;
        this.terrainId = terrainId;
        setupTerrainImageGroups();
    }

    @Override
    protected Widget createBody() {
        terrainImageModifier = new TerrainImageModifier(this);
        surfaceModifier = new SurfaceModifier(this);

        controlPanel = new FlexTable();
        // Delete Button
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
                        terrainId,
                        new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                GwtCommon.handleException(throwable, true);
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
        return controlPanel;
    }

    private void setupTerrainImageGroups() {
        terrainEditor.getTerrainImageGroups(new AsyncCallback<Map<String, Collection<Integer>>>() {
            @Override
            public void onFailure(Throwable caught) {
                GwtCommon.handleException(caught, true);
            }

            @Override
            public void onSuccess(Map<String, Collection<Integer>> result) {
                setupSurfaceSelector();

                for (Map.Entry<String, Collection<Integer>> entry : result.entrySet()) {
                    setupImageSelector(entry);
                }
                // Z Index
                zIndexSelector = new ListBox();
                zIndexSelector.addItem("Layer 1", TerrainImagePosition.ZIndex.LAYER_1.name());
                zIndexSelector.addItem("Layer 2", TerrainImagePosition.ZIndex.LAYER_2.name());
                zIndexSelector.setSelectedIndex(0);
                zIndexSelector.setEnabled(false);
                controlPanel.setWidget(controlPanel.getRowCount(), 0, zIndexSelector);

                selectorRow = controlPanel.getRowCount();

                terrainEditor.getTerrainInfo(terrainId, new AsyncCallback<TerrainInfo>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        GwtCommon.handleException(throwable);
                    }

                    @Override
                    public void onSuccess(TerrainInfo terrainInfo) {
                        TerrainView.getInstance().setupTerrain(terrainInfo.getTerrainSettings(),
                                terrainInfo.getTerrainImagePositions(),
                                terrainInfo.getSurfaceRects(),
                                terrainInfo.getSurfaceImages(),
                                terrainInfo.getTerrainImages(),
                                terrainInfo.getTerrainImageBackground());
                        TerrainView.getInstance().getTerrainHandler().loadImagesAndDrawMap(new SimpleDeferredStartup());
                        fillTerrainImages(terrainInfo.getTerrainImages());
                        fillSurfaces(terrainInfo.getSurfaceImages());
                    }
                });

            }
        });
    }

    private void setupImageSelector(Map.Entry<String, Collection<Integer>> entry) {
        FlexTable imageSelector = new FlexTable();
        imageSelector.setCellSpacing(5);
        imageSelector.setCellPadding(3);
        imageSelector.addStyleName("tile-selector");
        final ScrollPanel imageScroll = new ScrollPanel(imageSelector);
        imageScroll.setAlwaysShowScrollBars(true);
        imageScroll.setHeight(HEIGHT);
        for (Integer imageId : entry.getValue()) {
            imageGroup.put(imageId, imageSelector);
        }

        RadioButton imageButton = new RadioButton(RADIO_BUTTON_GROUP, entry.getKey());
        imageButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                MapWindow.getInstance().setTerrainMouseMoveListener(terrainImageModifier);
                controlPanel.setWidget(selectorRow, 0, imageScroll);
                zIndexSelector.setEnabled(true);
            }
        });

        controlPanel.setWidget(controlPanel.getRowCount(), 0, imageButton);
    }

    private void setupSurfaceSelector() {
        surfaceSelector = new FlexTable();
        surfaceSelector.setCellSpacing(5);
        surfaceSelector.setCellPadding(3);
        surfaceSelector.addStyleName("tile-selector");
        final ScrollPanel surfaceScroll = new ScrollPanel(surfaceSelector);
        surfaceScroll.setAlwaysShowScrollBars(true);
        surfaceScroll.setHeight(HEIGHT);

        RadioButton surfaceButton = new RadioButton(RADIO_BUTTON_GROUP, "Surface");
        surfaceButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                MapWindow.getInstance().setTerrainMouseMoveListener(surfaceModifier);
                controlPanel.setWidget(selectorRow, 0, surfaceScroll);
                zIndexSelector.setEnabled(false);
            }
        });
        controlPanel.setWidget(controlPanel.getRowCount(), 0, surfaceButton);
    }

    private void fillTerrainImages(Collection<TerrainImage> terrainImages) {
        for (TerrainImage terrainImage : terrainImages) {
            FlexTable terrainImageSelector = imageGroup.get(terrainImage.getId());
            if (terrainImageSelector == null) {
                throw new IllegalArgumentException("No group for terrain image: " + terrainImage.getId());
            }
            terrainImageSelector.setWidget(terrainImageSelector.getRowCount(), 0, new TerrainImageSelectorItem(terrainImage, this));
        }
    }

    private void fillSurfaces(Collection<SurfaceImage> surfaceImages) {
        for (SurfaceImage surfaceImage : surfaceImages) {
            surfaceSelector.setWidget(surfaceSelector.getRowCount(), 0, new SurfaceSelectorItem(surfaceImage));
        }
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

    public TerrainImagePosition.ZIndex getSelectedZIndex() {
        return TerrainImagePosition.ZIndex.valueOf(zIndexSelector.getValue(zIndexSelector.getSelectedIndex()));
    }
}
