package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.*;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.ProgressBar;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 06.11.2011
 * Time: 23:13:15
 */
public class SideCockpit {
    private static final SideCockpit INSTANCE = new SideCockpit();
    private static final String TEXT_COLOR = "#C7C4BB";
    // Background Panels
    private static final int MAIN_PANEL_W = 227;
    private static final int MAIN_PANEL_H = 240;
    private static final int MISSION_PANEL_X = 218;
    private static final int MISSION_PANEL_W = 789;
    private static final int MISSION_PANEL_H = 42;
    // Level
    private static final int LEVEL_X = 5;
    private static final int LEVEL_Y = 5;
    // Money
    private static final int MONEY_X = 75;
    private static final int MONEY_Y = 4;
    // Item limit
    private static final int ITEM_LIMIT_X = 157;
    private static final int ITEM_LIMIT_Y = 4;
    // Mission
    private static final int MISSION_X = 20;
    private static final int MISSION_Y = 5;
    // Energy
    private static final int ENERGY_X = 119;
    private static final int ENERGY_Y = 31;
    private static final int ENERGY_W = 95;
    private static final int ENERGY_H = 7;
    private static final int ENERGY_TEXT_X = 75;
    private static final int ENERGY_TEXT_Y = 27;
    // Radar
    private static final int RADAR_X = 13;
    private static final int RADAR_Y = 65;
    private static final int RADAR_W = 150;
    private static final int RADAR_H = 150;
    // Buttons
    private static final int BNT_X = 181;
    private static final int BNT_Y = 73;
    private static final int BNT_Y_SPACE = 35;
    private static final int BNT_SCROLL_X = BNT_X;
    private static final int BNT_SCROLL_Y = BNT_Y;
    private static final int BNT_SELL_X = BNT_X;
    private static final int BNT_SELL_Y = BNT_SCROLL_Y + BNT_Y_SPACE;
    private static final int BNT_MUTE_X = BNT_X;
    private static final int BNT_MUTE_Y = BNT_SELL_Y + BNT_Y_SPACE;
    private static final int BNT_STAT_X = BNT_X;
    private static final int BNT_STAT_Y = BNT_MUTE_Y + BNT_Y_SPACE;
    // Debug
    private static final int DEBUG_X = 10;
    private static final int DEBUG_Y = 213;

    private AbsolutePanel mainPanel;
    private AbsolutePanel missionPanel;
    private LevelPanel levelPanel;
    private Label money;
    private Label itemLimit;
    private HTML mission;
    private ProgressBar energyBar;
    private Label energyText;
    private ExtendedCustomButton sellButton;
    private Label debugPosition;
    private CockpitMode cockpitMode;

    public static SideCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SideCockpit() {
        setupPanels();
        setupMoney();
        setupItemLimit();
        setDebugPanel();
        setupLevelPanel();
        setupEnergy();
        setupMission();
        setupRadar();
        setupButtonPanel();
        cockpitMode = new CockpitMode();
    }

    private void setupPanels() {
        mainPanel = new AbsolutePanel();
        mainPanel.getElement().getStyle().setBackgroundImage("url(/images/cockpit/cockpit.png)");
        preventEvents(mainPanel);
        mainPanel.setPixelSize(MAIN_PANEL_W, MAIN_PANEL_H);

        missionPanel = new AbsolutePanel();
        missionPanel.getElement().getStyle().setBackgroundImage("url(/images/cockpit/cockpit.png)");
        missionPanel.getElement().getStyle().setProperty("backgroundPosition", "-" + Integer.toString(MISSION_PANEL_X) + "px 0");
        preventEvents(missionPanel);
        missionPanel.setPixelSize(MISSION_PANEL_W, MISSION_PANEL_H);
    }

    private void setupMoney() {
        money = new Label();
        money.setTitle(ToolTips.TOOL_TIP_MONEY);
        money.getElement().getStyle().setColor(TEXT_COLOR);
        mainPanel.add(money, MONEY_X, MONEY_Y);
    }

    private void setupItemLimit() {
        itemLimit = new Label();
        itemLimit.setTitle(ToolTips.TOOL_TIP_UNITS);
        itemLimit.getElement().getStyle().setColor(TEXT_COLOR);
        mainPanel.add(itemLimit, ITEM_LIMIT_X, ITEM_LIMIT_Y);
    }

    private void setDebugPanel() {
        if (Game.isDebug()) {
            debugPosition = new Label();
            debugPosition.getElement().getStyle().setBackgroundColor("#FFFFFF");
            mainPanel.add(debugPosition, DEBUG_X, DEBUG_Y);
        }
    }

    private void setupLevelPanel() {
        levelPanel = new LevelPanel();
        mainPanel.add(levelPanel, LEVEL_X, LEVEL_Y);
    }

    private void setupMission() {
        mission = new HTML();
        mission.setTitle(ToolTips.TOOL_TIP_MISSION);
        mission.getElement().getStyle().setColor(TEXT_COLOR);
        missionPanel.add(mission, MISSION_X, MISSION_Y);
    }

    private void setupEnergy() {
        // Setup base
        energyBar = new ProgressBar();
        energyBar.setTitle(ToolTips.TOOL_TIP_ENERGY);
        energyBar.setPixelSize(ENERGY_W, ENERGY_H);
        energyBar.setColors("red", "green");
        mainPanel.add(energyBar, ENERGY_X, ENERGY_Y);
        // Setup text
        energyText = new Label();
        energyText.setTitle(ToolTips.TOOL_TIP_ENERGY);
        energyText.getElement().getStyle().setColor(TEXT_COLOR);
        mainPanel.add(energyText, ENERGY_TEXT_X, ENERGY_TEXT_Y);

        updateEnergy(33, 100);
    }

    private void setupRadar() {
        AbsolutePanel absolutePanel = RadarPanel.getInstance().createWidget(RADAR_W, RADAR_H);
        absolutePanel.setTitle(ToolTips.TOOL_TIP_RADAR);
        mainPanel.add(absolutePanel, RADAR_X, RADAR_Y);
    }

    private void setupButtonPanel() {
        //Scroll home
        ExtendedCustomButton scrollHome = new ExtendedCustomButton("scrollHomeButton", false, ToolTips.TOOL_TIP_SCROLL_HOME, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TerrainView.getInstance().moveToHome();
            }
        });
        mainPanel.add(scrollHome, BNT_SCROLL_X, BNT_SCROLL_Y);
        // Sell button
        sellButton = new ExtendedCustomButton("sellButton", true, ToolTips.TOOL_TIP_SELL, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExtendedCustomButton btn = (ExtendedCustomButton) event.getSource();
                SelectionHandler.getInstance().setSellMode(btn.isDown());
            }
        });
        mainPanel.add(sellButton, BNT_SELL_X, BNT_SELL_Y);
        // Sell button
        ExtendedCustomButton mute = new ExtendedCustomButton("speakerButton", true, ToolTips.TOOL_TIP_MUTE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExtendedCustomButton btn = (ExtendedCustomButton) event.getSource();
                SoundHandler.getInstance().mute(btn.isDown());
            }
        });
        mainPanel.add(mute, BNT_MUTE_X, BNT_MUTE_Y);
        mainPanel.add(new WebBrowserCustomButton("highscoreButton", ToolTips.TOOL_TIP_HIGH_SCORE, CmsUtil.CmsPredefinedPage.HIGH_SCORE), BNT_STAT_X, BNT_STAT_Y);
    }

    public void debugAbsoluteCursorPos(int x, int y) {
        debugPosition.setText(x + ":" + y);
    }

    public void updateItemLimit() {
        if (itemLimit != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(ItemContainer.getInstance().getOwnItemCount());
            builder.append("/");
            builder.append(ClientBase.getInstance().getHouseSpace() + ClientLevelHandler.getInstance().getLevelScope().getHouseSpace());
            itemLimit.setText(builder.toString());
        }
    }


    public void updateMoney() {
        if (money != null) {
            double accountBalance = ClientBase.getInstance().getAccountBalance();
            if (accountBalance < 0) {
                money.setText("0");
            } else {
                money.setText(Integer.toString((int) accountBalance));
            }
            if (ItemCockpit.getInstance().isActive()) {
                ItemCockpit.getInstance().onMoneyChanged(accountBalance);
            }
        }
    }

    public void setLevel(LevelScope levelScope) {
        if (levelPanel != null) {
            levelPanel.onLevelUp(levelScope);
        }
        onStateChanged();
    }

    public void onLevelTaskDone() {
        if (levelPanel != null) {
            levelPanel.onLevelTaskDone();
        }
    }

    public void onStateChanged() {
        if (ItemCockpit.getInstance().isActive()) {
            ItemCockpit.getInstance().onStateChanged();
        }
    }

    public void setMissionHtml(String missionHtml) {
        if (mission != null) {
            mission.setHTML(missionHtml);
        }
    }

    public void updateEnergy(int generating, int consuming) {
        if (energyBar != null) {
            energyBar.setProgress(generating, consuming);
            energyText.setText(Integer.toString(consuming) + "/" + Integer.toString(generating));
        }
    }

    public void clearSellMode() {
        if (sellButton != null) {
            sellButton.setDownState(false);
        }
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(mainPanel, 0, 0);
        mainPanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);
        parent.add(missionPanel, MISSION_PANEL_X, 0);
        missionPanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);
    }

    private void preventEvents(Widget widget) {
        widget.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        widget.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                GwtCommon.preventDefault(event);
            }
        }, MouseUpEvent.getType());

        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                ItemCockpit.getInstance().deActivate();
                GwtCommon.preventDefault(event);
            }
        }, MouseDownEvent.getType());
        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                GwtCommon.preventDefault(event);
            }
        }, MouseDownEvent.getType());
    }

    public Rectangle getArea() {
        return new Rectangle(mainPanel.getAbsoluteLeft(), mainPanel.getAbsoluteTop(), mainPanel.getOffsetWidth(), mainPanel.getOffsetHeight());
    }

    public CockpitMode getCockpitMode() {
        return cockpitMode;
    }
}
