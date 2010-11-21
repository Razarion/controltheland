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

package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.MenuPanel;
import com.btxtech.game.jsre.client.TopMapPanel;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.UserMessage;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.client.dialogs.SendMessageDialog;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.MissionTarget;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.ProgressBar;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 08.11.2010
 * Time: 23:14:36
 */
public class CockpitNew extends AbsolutePanel {
    private static final CockpitNew INSTANCE = new CockpitNew();
    private static final int WIDTH = 1015;
    private static final int HEIGHT = 230;
    // Radar
    private static final int RADAR_WIDTH = 198;
    private static final int RADAR_HEIGHT = 198;
    private static final int RADAR_LEFT = 17;
    private static final int RADAR_TOP = 21;
    // Buttons
    private static final int BUTTON_SCROLL_HOME_LEFT = 654;
    private static final int BUTTON_SCROLL_HOME_TOP = 39;
    private static final int BUTTON_SELL_LEFT = 684;
    private static final int BUTTON_SELL_TOP = 39;
    private static final int BUTTON_OPTION_LEFT = 623;
    private static final int BUTTON_OPTION_TOP = 39;
    private static final int MISSION_LEFT = 716;
    private static final int MISSION_TOP = 39;
    // Label
    private static final int MONEY_LEFT = 644;
    private static final int MONEY_TOP = 114;
    private static final int XP_LEFT = 644;
    private static final int XP_TOP = 134;
    private static final int LEVEL_LEFT = 644;
    private static final int LEVEL_TOP = 156;
    private static final int ITEM_LIMIT_LEFT = 644;
    private static final int ITEM_LIMIT_TOP = 176;
    private static final int ENERGY_LEFT = 644;
    private static final int ENERGY_TOP = 197;
    private static final int ENERGY_BAR_LEFT = 775;
    private static final int ENERGY_BAR_TOP = 202;
    // Chat
    private static final int RECEIVED_TEXT_LEFT = 785;
    private static final int RECEIVED_TEXT_TOP = 55;
    private static final int SEND_LEFT = 961;
    private static final int SEND_TOP = 27;
    private static final int NAME_LEFT = 812;
    private static final int NAME_TOP = 33;
    private static final int NAME_BG_LEFT = 808;
    private static final int NAME_BG_TOP = 32;
    private static final int MAX_CHARS_RECEIVED_BOX = 1000;
    private static final int FLASHING_COUNT = 3;
    private static final int FLASHING_DELAY = 500;
    // Selection
    private static final int SELECTION_LEFT = 240;
    private static final int SELECTION_TOP = 26;

    private CockpitMode cockpitMode;
    private AbsolutePanel radar;
    private Label money;
    private Label xp;
    private Label level;
    private Label itemLimit;
    private Label energy;
    private ProgressBar energyBar;
    private TextArea receivedText;
    private ExtendedCustomButton send;
    private AbsolutePanel userColor;
    private Label userName;
    private int currentFlashingCount = 0;
    private Timer timer;
    private Map<CockpitGuiElements, Widget> widgets = new HashMap<CockpitGuiElements, Widget>();
    private SelectedItemPanel selectedItemPanel;

    public static CockpitNew getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private CockpitNew() {
        cockpitMode = new CockpitMode();
        getElement().getStyle().setBackgroundImage("url(/images/cockpit/cockpit.png)");
        setPixelSize(WIDTH, HEIGHT);
        preventEvents();
        setupRadar();
        setupButtons();
        setupInfo();
        setupOnline();
        selectedItemPanel = new SelectedItemPanel();
        add(selectedItemPanel, SELECTION_LEFT, SELECTION_TOP);
    }

    private void setupOnline() {
        receivedText = new TextArea();
        receivedText.getElement().getStyle().setColor("black");
        receivedText.setReadOnly(true);
        receivedText.getElement().getStyle().setHeight(106, Style.Unit.PX);
        receivedText.getElement().getStyle().setWidth(202, Style.Unit.PX);
        add(receivedText, RECEIVED_TEXT_LEFT, RECEIVED_TEXT_TOP);
        send = new ExtendedCustomButton("/images/cockpit/sendButton-up.png", "/images/cockpit/sendButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SendMessageDialog.showDialog();
            }
        });
        send.getUpDisabledFace().setImage(new Image("/images/cockpit/sendButton-disabled-up.png"));
        add(send, SEND_LEFT, SEND_TOP);
        userColor = new AbsolutePanel();
        userColor.setPixelSize(147, 16);
        add(userColor, NAME_BG_LEFT, NAME_BG_TOP);
        userName = new Label();
        add(userName, NAME_LEFT, NAME_TOP);
    }

    private void setupInfo() {
        money = new Label();
        add(money, MONEY_LEFT, MONEY_TOP);
        xp = new Label();
        add(xp, XP_LEFT, XP_TOP);
        level = new Label();
        add(level, LEVEL_LEFT, LEVEL_TOP);
        itemLimit = new Label();
        add(itemLimit, ITEM_LIMIT_LEFT, ITEM_LIMIT_TOP);
        energy = new Label("0/0");
        add(energy, ENERGY_LEFT, ENERGY_TOP);
        energyBar = new ProgressBar(0, 0);
        energyBar.setTextVisible(false);
        energyBar.setStyleName("gwt-EnergyBar-shell");
        energyBar.getElement().getStyle().setHeight(10, Style.Unit.PX);
        energyBar.getElement().getStyle().setWidth(210, Style.Unit.PX);
        energyBar.getElement().getStyle().setColor("#000000");
        add(energyBar, ENERGY_BAR_LEFT, ENERGY_BAR_TOP);
    }

    private void setupButtons() {
        ExtendedCustomButton scrollHome = new ExtendedCustomButton("/images/cockpit/scrollHomeButton-up.png", "/images/cockpit/scrollHomeButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TerrainView.getInstance().moveToHome();
            }
        });
        add(scrollHome, BUTTON_SCROLL_HOME_LEFT, BUTTON_SCROLL_HOME_TOP);
        widgets.put(CockpitGuiElements.SCROLL_HOME_BUTTON, scrollHome);
        ExtendedCustomButton sell = new ExtendedCustomButton("/images/cockpit/sellButton-up.png", "/images/cockpit/sellButton-down.png", true, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExtendedCustomButton btn = (ExtendedCustomButton) event.getSource();
                SelectionHandler.getInstance().setSellMode(btn.isDown());
            }
        });
        add(sell, BUTTON_SELL_LEFT, BUTTON_SELL_TOP);
        widgets.put(CockpitGuiElements.SELL_BUTTON, sell);
        ExtendedCustomButton option = new ExtendedCustomButton("/images/cockpit/optionButton-up.png", "/images/cockpit/optionButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MenuPanel menuPanel = new MenuPanel();
                menuPanel.addToParent(MapWindow.getAbsolutePanel(), TopMapPanel.Direction.CENTER, 0);
            }
        });
        add(option, BUTTON_OPTION_LEFT, BUTTON_OPTION_TOP);
        widgets.put(CockpitGuiElements.OPTION_BUTTON, option);
        ExtendedCustomButton mission = new ExtendedCustomButton("/images/cockpit/missionButton-up.png", "/images/cockpit/missionButton-down.png", false, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MissionTarget.getInstance().showMissionTargetDialog();
            }
        });
        add(mission, MISSION_LEFT, MISSION_TOP);
        widgets.put(CockpitGuiElements.MISSION_BUTTON, mission);
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

    public void updateXp(int amount) {
        if (xp != null) {
            xp.setText(Integer.toString(amount));
        }
    }

    public void setLevel(String level) {
        this.level.setText(level);
    }

    public void updateItemLimit() {
        StringBuilder builder = new StringBuilder();
        builder.append(ItemContainer.getInstance().getOwnItemCount());
        builder.append("/");
        builder.append(ClientBase.getInstance().getHouseSpace());
        itemLimit.setText(builder.toString());
    }

    public void updateEnergy(int generating, int consuming) {
        energy.setText(Integer.toString(consuming) + "/" + Integer.toString(generating));
        if (generating == 0) {
            energyBar.setMaxProgress(consuming);
        } else {
            energyBar.setMaxProgress(generating);
        }
        energyBar.setProgress(consuming);
    }

    public void updateBase() {
        userName.setText(ClientBase.getInstance().getOwnBaseName());
        userColor.getElement().getStyle().setBackgroundColor(ClientBase.getInstance().getOwnBaseHtmlColor());
    }

    public void setGameInfo(RealityInfo realityInfo) {
        money.setText(Integer.toString((int) Math.round(realityInfo.getAccountBalance())));
        xp.setText(Integer.toString(realityInfo.getXp()));
        updateEnergy(realityInfo.getEnergyGenerating(), realityInfo.getEnergyConsuming());
        updateBase();
    }

    public void onMessageReceived(UserMessage userMessage) {
        StringBuffer buffer = new StringBuffer();
        if (receivedText.getText().length() > MAX_CHARS_RECEIVED_BOX) {
            buffer.append(receivedText.getText().substring(receivedText.getText().length() - MAX_CHARS_RECEIVED_BOX));
            buffer.append("\n");
        } else if (!receivedText.getText().isEmpty()) {
            buffer.append(receivedText.getText());
            buffer.append("\n");
        }
        buffer.append(userMessage.getBaseName());
        buffer.append("\n");
        buffer.append(userMessage.getMessage());
        buffer.append("\n");
        receivedText.setText(buffer.toString());
        receivedText.getElement().setScrollTop(receivedText.getElement().getScrollHeight());
        startFlashing();
    }

    private void startFlashing() {
        if (currentFlashingCount > 0) {
            currentFlashingCount = FLASHING_COUNT;
            return;
        }
        timer = new Timer() {
            @Override
            public void run() {
                if (currentFlashingCount <= 0) {
                    receivedText.getElement().getStyle().setBackgroundColor("white");
                    timer.cancel();
                } else {
                    currentFlashingCount--;
                    if (currentFlashingCount % 2 == 1) {
                        receivedText.getElement().getStyle().setBackgroundColor("white");
                    } else {
                        receivedText.getElement().getStyle().setBackgroundColor("red");
                    }
                }
            }
        };
        currentFlashingCount = FLASHING_COUNT;
        timer.scheduleRepeating(FLASHING_DELAY);
    }

    public boolean contains(Index point) {
        return Rectangle.contains(getAbsoluteLeft(), getAbsoluteTop(), WIDTH, HEIGHT, point);
    }

    public void enableOnlinePanel(boolean enabled) {
        receivedText.setEnabled(enabled);
        send.setEnabled(enabled);
    }

    public void debugAbsoluteCursorPos(int x, int y) {
        //TODO
    }

    public void enableFocusWidget(CockpitGuiElements cockpitGuiElements, boolean enabled) {
        ((FocusWidget) widgets.get(cockpitGuiElements)).setEnabled(enabled);
    }

    public SelectedItemPanel getSelectedItemPanel() {
        return selectedItemPanel;
    }

    public CockpitMode getCockpitMode() {
        return cockpitMode;
    }
}
