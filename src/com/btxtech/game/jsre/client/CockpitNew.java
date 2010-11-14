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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * User: beat
 * Date: 08.11.2010
 * Time: 23:14:36
 */
public class CockpitNew extends AbsolutePanel {
    private static final CockpitNew INSTANCE = new CockpitNew();
    private static final int WIDTH = 1015;
    private static final int HEIGHT = 230;
    private static final int RADAR_WIDTH = 198;
    private static final int RADAR_HEIGHT = 198;
    private static final int RADAR_LEFT = 17;
    private static final int RADAR_TOP = 21;
    private static final int BUTTON_SCROLL_HOME_LEFT = 238;
    private static final int BUTTON_SCROLL_HOME_TOP = 35;
    private static final int BUTTON_SELL_LEFT = 238;
    private static final int BUTTON_SELL_TOP = 70;
    private static final int BUTTON_OPTION_LEFT = 238;
    private static final int BUTTON_OPTION_TOP = 105;
    private static final int MONEY_LEFT = 673;
    private static final int MONEY_TOP = 33;
    private AbsolutePanel radar;
    private Label money;

    public static CockpitNew getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private CockpitNew() {
        getElement().getStyle().setBackgroundImage("url(/images/cockpit/cockpit.png)");
        setPixelSize(WIDTH, HEIGHT);
        preventEvents();
        setupRadar();
        setupButtons();
        /////////////////////////////////
        money = new Label();
        add(money, MONEY_LEFT, MONEY_TOP);
    }

    private void setupButtons() {
        ExtendedCustomButton scrollHome = new ExtendedCustomButton("/images/cockpit/scrollHomeButton-up.png", "/images/cockpit/scrollHomeButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TerrainView.getInstance().moveToHome();
            }
        });
        add(scrollHome, BUTTON_SCROLL_HOME_LEFT, BUTTON_SCROLL_HOME_TOP);
        ExtendedCustomButton sell = new ExtendedCustomButton("/images/cockpit/sellButton-up.png", "/images/cockpit/sellButton-down.png", true, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExtendedCustomButton btn = (ExtendedCustomButton) event.getSource();
                SelectionHandler.getInstance().setSellMode(btn.isDown());
            }
        });
        add(sell, BUTTON_SELL_LEFT, BUTTON_SELL_TOP);
        ExtendedCustomButton option = new ExtendedCustomButton("/images/cockpit/optionButton-up.png", "/images/cockpit/optionButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MenuPanel menuPanel = new MenuPanel();
                menuPanel.addToParent(MapWindow.getAbsolutePanel(), TopMapPanel.Direction.CENTER, 0);
            }
        });
        add(option, BUTTON_OPTION_LEFT, BUTTON_OPTION_TOP);
    }

    private void setupRadar() {
        radar = RadarPanel.getInstance().createWidget(RADAR_WIDTH, RADAR_HEIGHT);
        add(radar, RADAR_LEFT, RADAR_TOP);
        Image radarFrame = ImageHandler.createImageIE6TransparencyProblem("/images/cockpit/radarframe.png", RADAR_WIDTH, RADAR_HEIGHT);
        radarFrame.getElement().getStyle().setZIndex(100);
        radarFrame.addMouseDownHandler(RadarPanel.getInstance().getRadarFrameView());
        radar.add(radarFrame, 0, 0);
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 1);
        getElement().getStyle().setProperty("top", "");
        getElement().getStyle().setProperty("bottom", 0 + "px");
        getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);
    }

    public void setVisibleRadar(boolean visible) {
        radar.setVisible(visible);
    }

    private void preventEvents() {
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.stopPropagation();
            }
        }, MouseUpEvent.getType());

        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        }, MouseDownEvent.getType());
        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.stopPropagation();
            }
        }, MouseDownEvent.getType());
    }

    public void updateMoney() {
        if (money != null) {
            money.setText(Integer.toString((int) Math.round(ClientBase.getInstance().getAccountBalance())));
        }
    }

}
