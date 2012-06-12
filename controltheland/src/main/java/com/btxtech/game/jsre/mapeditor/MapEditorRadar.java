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
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.RadarMode;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 23.12.2010
 * Time: 11:09:10
 */
public class MapEditorRadar extends TopMapPanel {
    private static final int RADAR_WIDTH = 200;
    private static final int RADAR_HEIGHT = 200;

    @Override
    protected Widget createBody() {
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(new Button("Update Radar", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                RadarPanel.getInstance().getMiniTerrain().onTerrainChanged();
            }
        }));
        verticalPanel.add(RadarPanel.getInstance().createWidget(RADAR_WIDTH, RADAR_HEIGHT));
        RadarPanel.getInstance().setLevelRadarMode(RadarMode.MAP);
        RadarPanel.getInstance().getRadarFrameView().getCanvas().addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        });
        RadarPanel.getInstance().getRadarFrameView().getCanvas().addMouseUpHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.stopPropagation();
            }
        });
        return verticalPanel;
    }
}
