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

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:26:58
 */
public class RadarPanel {
    public static final String RADAR_1 = "Radar 1";
    private static final String NO_POWER = "<br/>You do not have a enough energy.";
    private static final RadarPanel INSTANCE = new RadarPanel();
    private MiniTerrain miniTerrain;
    private RadarFrameView radarFrameView;
    private RadarHintView radarHintView;
    private RadarItemView radarItemView;
    private boolean hasRadar1 = false;
    private boolean hasEnergy = false;
    private HTML noRadarPanel;

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
        boolean state = !hasRadar1 || hasEnergy;

        // No radar Panel
        noRadarPanel = new HTML();
        noRadarPanel.setSize("100%", "100%");
        noRadarPanel.getElement().getStyle().setColor("#FFFFFF");
        noRadarPanel.getElement().getStyle().setBackgroundColor("#000000");
        noRadarPanel.setHTML(NO_POWER);
        noRadarPanel.setVisible(!state);
        absolutePanel.add(noRadarPanel, 0, 0);

        // Terrain
        miniTerrain = new MiniTerrain(width, height);
        miniTerrain.getCanvas().getElement().getStyle().setZIndex(1);
        miniTerrain.getCanvas().setVisible(state);
        absolutePanel.add(miniTerrain.getCanvas(), 0, 0);

        // Item view
        radarItemView = new RadarItemView(width, height);
        radarItemView.getCanvas().getElement().getStyle().setZIndex(2);
        radarItemView.getCanvas().setVisible(hasRadar1 && hasEnergy);
        absolutePanel.add(radarItemView.getCanvas(), 0, 0);

        // Hint view
        radarHintView = new RadarHintView(width, height);
        radarHintView.getCanvas().getElement().getStyle().setZIndex(3);
        absolutePanel.add(radarHintView.getCanvas(), 0, 0);

        // Frame view
        radarFrameView = new RadarFrameView(width, height);
        radarFrameView.getCanvas().getElement().getStyle().setZIndex(4);
        radarFrameView.getCanvas().setVisible(state);
        absolutePanel.add(radarFrameView.getCanvas(), 0, 0);

        return absolutePanel;
    }

    public void setRadarState1(boolean state) {
        if (hasRadar1 == state) {
            return;
        }
        hasRadar1 = state;
        handleRadarState();
    }

    private void handleRadarState() {
        boolean state = !hasRadar1 || hasEnergy;

        if (miniTerrain != null) {
            miniTerrain.getCanvas().setVisible(state);
        }
        if (radarFrameView != null) {
            radarFrameView.getCanvas().setVisible(state);
        }
        if (radarItemView != null) {
            radarItemView.getCanvas().setVisible(hasRadar1 && hasEnergy);
        }
        if (noRadarPanel != null) {
            if (hasRadar1 && !hasEnergy) {
                noRadarPanel.setVisible(true);
                noRadarPanel.setHTML(NO_POWER);
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

    public void setRadarItemsVisible() {
        hasEnergy = true;
        hasRadar1 = true;
        handleRadarState();
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
