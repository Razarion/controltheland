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

import com.btxtech.game.jsre.client.TopMapPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 22.12.2009
 * Time: 12:26:58
 */
public class RadarPanel extends TopMapPanel {
    public static final String NO_RADAR = "<br/>You do not have a running Radar. Get one from <a href=\"?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.market.MarketPage\" target=\"_blank\">Market</a> and build it with your construction vehicle.";
    public static final String NO_POWER = "<br/>You do not have a enough energy. Get a Power Plant from <a href=\"?wicket:bookmarkablePage=:com.btxtech.game.wicket.pages.market.MarketPage\" target=\"_blank\">Market</a> and build it with your construction vehicle.";
    public static final int WIDTH = 200;
    public static final int HEIGHT = 200;
    public static final String RADAR_1 = "Radar 1";
    private static final RadarPanel INSTANCE = new RadarPanel();
    private MiniTerrain miniTerrain;
    private FrameView frameView;
    private boolean hasRadar = false;
    private boolean hasEnergy = false;
    private HTML noRadaPanel;

    public static RadarPanel getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private RadarPanel() {
    }

    @Override
    protected Widget createBody() {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setPixelSize(WIDTH, HEIGHT);
        boolean state = hasRadar && hasEnergy;

        // No radar Panel
        noRadaPanel = new HTML();
        noRadaPanel.setHTML(NO_RADAR);
        noRadaPanel.setSize("100%", "100%");
        noRadaPanel.getElement().getStyle().setColor("#FFFFFF");
        noRadaPanel.getElement().getStyle().setBackgroundColor("#000000");
        noRadaPanel.setVisible(!state);
        absolutePanel.add(noRadaPanel, 0, 0);

        // Terrain
        miniTerrain = new MiniTerrain(WIDTH, HEIGHT);
        miniTerrain.getElement().getStyle().setZIndex(1);
        miniTerrain.setVisible(state);
        absolutePanel.add(miniTerrain, 0, 0);

        // Frame view
        frameView = new FrameView(WIDTH, HEIGHT);
        frameView.getElement().getStyle().setZIndex(2);
        frameView.setVisible(state);
        absolutePanel.add(frameView, 0, 0);

        return absolutePanel;
    }

    public void setRadarState(boolean state) {
        if (hasRadar == state) {
            return;
        }
        hasRadar = state;
        handleRadarState();
    }

    private void handleRadarState() {
        boolean state = hasRadar && hasEnergy;

        if (miniTerrain != null) {
            miniTerrain.setVisible(state);
        }
        if (frameView != null) {
            frameView.setVisible(state);
        }
        if (noRadaPanel != null) {
            if (state) {
                noRadaPanel.setVisible(false);
            } else if(!hasRadar)  {
                noRadaPanel.setVisible(true);
                noRadaPanel.setHTML(NO_RADAR);
            } else if(!hasEnergy)  {
                noRadaPanel.setVisible(true);
                noRadaPanel.setHTML(NO_POWER);
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
}