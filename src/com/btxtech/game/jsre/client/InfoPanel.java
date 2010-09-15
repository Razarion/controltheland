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
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.MissionTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 11:26:24 AM
 */
public class InfoPanel extends TopMapPanel {
    private static final InfoPanel INSTANCE = new InfoPanel();
    public HorizontalPanel userIdentification;
    private Label level;
    private Label money;
    private Label xp;
    private Label name;
    private SimplePanel marker;
    private Label cursorPos;
    private Label itemLimit;
    private ProgressBar energyBar;
    private int generating;
    private int consuming;
    private Button scrollHome;
    private Button option;
    private ToggleButton sell;

    /**
     * Simgleton
     */
    private InfoPanel() {

    }

    @Override
    protected Widget createBody() {
        FlexTable layout = new FlexTable();
        layout.setStyleName("topMapPanelText");
        layout.setCellSpacing(1);

        // Mission Target
        Anchor missionTargetLink = new Anchor("Mission Target");
        missionTargetLink.getElement().getStyle().setColor("darkorange");
        missionTargetLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                MissionTarget.getInstance().showMissionTargetDialog();
            }
        });
        layout.setWidget(0, 1, missionTargetLink);
        layout.getFlexCellFormatter().setColSpan(0, 1, 2);

        // Level
        layout.setHTML(1, 1, "Level");
        level = new Label();
        level.setText("");
        layout.setWidget(1, 2, level);

        // Name
        layout.setHTML(2, 1, "Name");
        name = new Label("");
        layout.setWidget(2, 2, name);

        // Color
        layout.setHTML(3, 1, "Color");
        marker = new SimplePanel();
        marker.setPixelSize(30, 15);
        layout.setWidget(3, 2, marker);

        // Money
        layout.setHTML(4, 1, "Money");
        money = new Label("");
        layout.setWidget(4, 2, money);

        // Xp
        layout.setHTML(5, 1, "Xp");
        xp = new Label("");
        layout.setWidget(5, 2, xp);

        // Energy
        layout.setHTML(6, 1, "Energy");
        layout.getFlexCellFormatter().setColSpan(6, 1, 2);
        energyBar = new ProgressBar(0, 0) {
            @Override
            protected String generateText(double curProgress) {
                return Integer.toString(consuming) + "/" + Integer.toString(generating);
            }
        };
        energyBar.setStyleName("gwt-EnergyBar-shell");
        energyBar.getElement().getStyle().setHeight(15, Style.Unit.PX);
        energyBar.getElement().getStyle().setColor("#000000");
        layout.setWidget(7, 1, energyBar);
        layout.getFlexCellFormatter().setColSpan(7, 1, 2);

        // Item Limit
        layout.setHTML(8, 1, "Item Limit");
        itemLimit = new Label("");
        layout.setWidget(8, 2, itemLimit);

        // Move home button
        layout.getFlexCellFormatter().setColSpan(9, 1, 2);
        scrollHome = new Button("Scroll Home");
        scrollHome.setWidth("100%");
        layout.setWidget(9, 1, scrollHome);
        scrollHome.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                TerrainView.getInstance().moveToHome();
            }
        });

        // Menu button
        layout.getFlexCellFormatter().setColSpan(10, 1, 2);
        option = new Button("Options");
        option.setWidth("100%");
        layout.setWidget(10, 1, option);
        option.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                MenuPanel menuPanel = new MenuPanel();
                menuPanel.addToParent(MapWindow.getAbsolutePanel(), Direction.CENTER, 0);
            }
        });

        // Sell button
        layout.getFlexCellFormatter().setColSpan(11, 1, 2);
        sell = new ToggleButton("Sell");
        sell.getElement().getStyle().setColor("#000000");
        sell.getElement().getStyle().setProperty("textAlign", "center");

        layout.setWidget(11, 1, sell);
        sell.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                SelectionHandler.getInstance().setSellMode(sell.isDown());
            }
        });

        // Debug
        if (Game.isDebug()) {
            // Cursor
            layout.setHTML(12, 1, "Cursor");
            cursorPos = new Label("");
            layout.setWidget(12, 2, cursorPos);
        }

        return layout;
    }

    public static InfoPanel getInstance() {
        return INSTANCE;
    }

    public void setGameInfo(RealityInfo realityInfo) {
        money.setText("$" + Integer.toString((int) realityInfo.getAccountBalance()));
        xp.setText(Integer.toString(realityInfo.getXp()));
        updateEnergy(realityInfo.getEnergyGenerating(), realityInfo.getEnergyConsuming());
        updateBase();
    }

    public void updateBase() {
        name.setText(ClientBase.getInstance().getOwnBaseName());
        marker.getElement().getStyle().setBackgroundColor(ClientBase.getInstance().getOwnBaseHtmlColor());
    }

    public void updateMoney() {
        if (money != null) {
            money.setText(Integer.toString((int) Math.round(ClientBase.getInstance().getAccountBalance())));
        }
    }

    public void updateXp(int amount) {
        if (xp != null) {
            xp.setText(Integer.toString(amount));
        }
    }


    public void setAbsoluteCureserPos(int x, int y) {
        if (cursorPos != null) {
            cursorPos.setText(x + ":" + y);
        }
    }

    public void updateEnergy(int generating, int consuming) {
        this.generating = generating;
        this.consuming = consuming;
        if (generating == 0) {
            energyBar.setMaxProgress(consuming);
        } else {
            energyBar.setMaxProgress(generating);
        }
        energyBar.setProgress(consuming);
    }

    public Button getScrollHome() {
        return scrollHome;
    }

    public ToggleButton getSell() {
        return sell;
    }

    public Button getOption() {
        return option;
    }

    public Label getMoney() {
        return money;
    }

    public void setLevel(String level) {
        this.level.setText(level);
    }

    public void updateItemLimit() {
        StringBuilder builder = new StringBuilder();
        builder.append(ItemContainer.getInstance().getOwnItemCount());
        builder.append("/");
        builder.append(ClientBase.getInstance().getHouseSpace());
        builder.append("/");
        builder.append(ClientBase.getInstance().getItemLimit());
        itemLimit.setText(builder.toString());
    }
}
