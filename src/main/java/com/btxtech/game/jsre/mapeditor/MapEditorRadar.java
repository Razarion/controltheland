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
import com.btxtech.game.jsre.client.cockpit.radar.MiniMapMouseDownListener;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Index;
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
    private MapEditorModel mapEditorModel;

    @Override
    protected Widget createBody() {
        Widget radarPane = RadarPanel.getInstance().createWidget(RADAR_WIDTH, RADAR_HEIGHT);
        RadarPanel.getInstance().setLevelRadarMode(RadarMode.MAP);
        RadarPanel.getInstance().getRadarFrameView().addMouseDownListener(new MiniMapMouseDownListener() {
            @Override
            public void onMouseDown(int absX, int absY, MouseDownEvent mouseDownEvent) {
                Index delta = new Index(absX,absY).sub(mapEditorModel.getViewRectangle().getCenter());
                mapEditorModel.moveDelta(delta.getX(), delta.getY());
            }
        });
        return radarPane;
    }

    public void setMapEditorModel(MapEditorModel mapEditorModel) {
        this.mapEditorModel = mapEditorModel;
    }
}
