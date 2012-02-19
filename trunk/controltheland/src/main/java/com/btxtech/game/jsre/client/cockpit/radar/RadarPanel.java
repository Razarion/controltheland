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

package com.btxtech.game.jsre.client.cockpit.radar;

import com.btxtech.game.jsre.client.ClientSyncItem;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:26:58
 */
public class RadarPanel {
    private static final String NO_POWER = "<br/>You do not have enough energy.";
    private static final String NO_RADAR = "<br/>You do not have a radar building.";
    private static final RadarPanel INSTANCE = new RadarPanel();
    private MiniTerrain miniTerrain;
    private RadarFrameView radarFrameView;
    private RadarHintView radarHintView;
    private RadarItemView radarItemView;
    private HTML noRadarPanel;
    private boolean hasEnergy = false;
    private RadarMode levelRadarMode = RadarMode.NONE;
    private RadarMode itemRadarMode = RadarMode.NONE;
    private Set<SyncBaseItem> radarModeItems = new HashSet<SyncBaseItem>();

    public static RadarPanel getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private RadarPanel() {
    }

    public AbsolutePanel createWidget(int width, int height) {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setPixelSize(width, height);

        // No radar Panel
        noRadarPanel = new HTML();
        noRadarPanel.setSize("100%", "100%");
        noRadarPanel.getElement().getStyle().setColor("#FFFFFF");
        noRadarPanel.getElement().getStyle().setBackgroundColor("#000000");
        absolutePanel.add(noRadarPanel, 0, 0);

        // Terrain
        miniTerrain = new MiniTerrain(width, height);
        miniTerrain.getCanvas().getElement().getStyle().setZIndex(1);
        miniTerrain.getCanvas().setVisible(false);
        absolutePanel.add(miniTerrain.getCanvas(), 0, 0);

        // Item view
        radarItemView = new RadarItemView(width, height);
        radarItemView.getCanvas().getElement().getStyle().setZIndex(2);
        radarItemView.getCanvas().setVisible(false);
        absolutePanel.add(radarItemView.getCanvas(), 0, 0);

        // Hint view
        radarHintView = new RadarHintView(width, height);
        radarHintView.getCanvas().getElement().getStyle().setZIndex(3);
        radarHintView.getCanvas().setVisible(false);
        absolutePanel.add(radarHintView.getCanvas(), 0, 0);

        // Frame view
        radarFrameView = new RadarFrameView(width, height);
        radarFrameView.getCanvas().getElement().getStyle().setZIndex(4);
        radarFrameView.getCanvas().setVisible(false);
        absolutePanel.add(radarFrameView.getCanvas(), 0, 0);

        return absolutePanel;
    }

    private void handleRadarState() {
        RadarMode mode;
        if (hasEnergy) {
            mode = RadarMode.getHigher(itemRadarMode, levelRadarMode);
        } else {
            mode = levelRadarMode;
        }

        boolean showMap = RadarMode.MAP.sameOrHigher(mode);

        if (miniTerrain != null) {
            miniTerrain.getCanvas().setVisible(showMap);
        }
        if (radarFrameView != null) {
            radarFrameView.getCanvas().setVisible(showMap);
        }
        if (radarHintView != null) {
            radarHintView.getCanvas().setVisible(showMap);
        }

        boolean showUnits = RadarMode.MPA_AND_UNITS.sameOrHigher(mode);
        if (radarItemView != null) {
            radarItemView.getCanvas().setVisible(showUnits);
        }

        if (noRadarPanel != null) {
            if (showMap) {
                noRadarPanel.setVisible(false);
            } else {
                if (!hasEnergy) {
                    noRadarPanel.setVisible(true);
                    noRadarPanel.setHTML(NO_POWER);
                } else {
                    noRadarPanel.setVisible(true);
                    noRadarPanel.setHTML(NO_RADAR);
                }
            }
        }
    }

    public void updateEnergy(int generating, int consuming) {
        boolean state = generating >= consuming;
        if (hasEnergy == state) {
            return;
        }
        hasEnergy = state;
        handleRadarState();
    }

    public void onTerrainSettings(TerrainSettings terrainSettings) {
        miniTerrain.onTerrainSettings(terrainSettings);
        radarFrameView.onTerrainSettings(terrainSettings);
        radarHintView.onTerrainSettings(terrainSettings);
        radarItemView.onTerrainSettings(terrainSettings);
    }

    public RadarFrameView getRadarFrameView() {
        return radarFrameView;
    }

    public void setLevelRadarMode(RadarMode levelRadarMode) {
        this.levelRadarMode = levelRadarMode;
        handleRadarState();
    }

    public void onRadarModeItemChanged(SyncBaseItem syncBaseItem) {
        radarModeItems.add(syncBaseItem);
        handleItemRadarState();
    }

    public void onRadarModeItemRemoved(SyncBaseItem syncBaseItem) {
        radarModeItems.remove(syncBaseItem);
        handleItemRadarState();
    }

    public void onItemTypeChanged(ClientSyncItem clientSyncItem) {
        if (clientSyncItem.getSyncItem() instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = clientSyncItem.getSyncBaseItem();
            if (radarModeItems.contains(syncBaseItem)) {
                handleItemRadarState();
            }
        }
    }

    private void handleItemRadarState() {
        itemRadarMode = findHighestRadarMode();
        handleRadarState();
    }

    public void clearRadarMode() {
        radarModeItems.clear();
        itemRadarMode = RadarMode.NONE;
        levelRadarMode = RadarMode.NONE;
    }

    private RadarMode findHighestRadarMode() {
        RadarMode radarMode = RadarMode.NONE;
        for (SyncBaseItem radarModeItem : radarModeItems) {
            if (!radarModeItem.isReady()) {
                continue;
            }
            if (radarMode.sameOrHigher(radarModeItem.getBaseItemType().getSpecialType().getRadarMode())) {
                radarMode = radarModeItem.getBaseItemType().getSpecialType().getRadarMode();
            }
        }
        return radarMode;
    }

    public void showHint(SyncBaseItem enemyBaseItem) {
        radarHintView.showHint(enemyBaseItem);
    }

    public void hideHint() {
        radarHintView.hideHint();
    }

    public void blinkHint() {
        radarHintView.blinkHint();
    }
}
