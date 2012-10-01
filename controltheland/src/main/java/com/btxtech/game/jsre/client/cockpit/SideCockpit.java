package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.ExtendedCustomButton;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.WebBrowserCustomButton;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimulationInfo;
import com.btxtech.game.jsre.client.dialogs.AllianceDialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.dialogs.highscore.HighscoreDialog;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.FacebookUtils;
import com.btxtech.game.jsre.common.ProgressBar;
import com.btxtech.game.jsre.common.packets.BoxPickedPacket;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 06.11.2011
 * Time: 23:13:15
 */
public class SideCockpit {
    public static final String TEXT_COLOR = "#C7C4BB";
    private static final SideCockpit INSTANCE = new SideCockpit();
    // RealGame or Mission panel
    private static final int REAL_GAME_MISSION_X = 2;
    private static final int REAL_GAME_MISSION_Y = 4;
    // Background Panels
    private static final int MAIN_PANEL_W = 227;
    private static final int MAIN_PANEL_H = 240;
    private static final int LEVEL_PANEL_X = 215;
    private static final int LEVEL_PANEL_W = 116;
    private static final int LEVEL_PANEL_H = 77;
    // Information panel
    public static final int INFORMATION_COCKPIT_X = LEVEL_PANEL_X + LEVEL_PANEL_W + 10;
    public static final int INFORMATION_COCKPIT_Y = 10;
    // Money
    private static final int MONEY_X = 25;
    private static final int MONEY_Y = 4;
    // Item limit
    private static final int ITEM_LIMIT_X = 126;
    private static final int ITEM_LIMIT_Y = 4;
    // Energy
    private static final int ENERGY_X = 102;
    private static final int ENERGY_Y = 31;
    private static final int ENERGY_W = 102;
    private static final int ENERGY_H = 7;
    private static final int ENERGY_TEXT_X = 25;
    private static final int ENERGY_TEXT_Y = 26;
    // Radar
    private static final int RADAR_X = 4;
    private static final int RADAR_Y = 50;
    private static final int RADAR_W = 169;
    private static final int RADAR_H = 178;
    // Buttons
    private static final int BNT_X = 179;
    private static final int BNT_Y = 82;
    private static final int BNT_Y_SPACE = 26;
    private static final int BNT_INVENTORY_X = BNT_X;
    private static final int BNT_INVENTORY_Y = BNT_Y;
    private static final int BNT_ALLIANCE_X = BNT_X;
    private static final int BNT_ALLIANCE_Y = BNT_INVENTORY_Y + BNT_Y_SPACE;
    private static final int BNT_SELL_X = BNT_X;
    private static final int BNT_SELL_Y = BNT_ALLIANCE_Y + BNT_Y_SPACE;
    private static final int BNT_MUTE_X = BNT_X;
    private static final int BNT_MUTE_Y = BNT_SELL_Y + BNT_Y_SPACE;
    private static final int BNT_STAT_X = BNT_X;
    private static final int BNT_STAT_Y = BNT_MUTE_Y + BNT_Y_SPACE;
    // Social net
    private static final int BNT_FB_COMMUNITY_X = 185;
    private static final int BNT_FB_COMMUNITY_Y = 213;
    private static final int BNT_FB_INVITE_X = 177;
    private static final int BNT_FB_INVITE_Y = 49;
    // Debug
    private static final int DEBUG_X = 10;
    private static final int DEBUG_Y = 200;

    private AbsolutePanel mainPanel;
    private AbsolutePanel levelPanel;
    private Label money;
    private Label itemLimit;
    private ProgressBar energyBar;
    private Label energyText;
    private ExtendedCustomButton sellButton;
    private Label debugPosition;
    private Label debugFrameRate;
    private QuestProgressCockpit questProgressCockpit;
    private InformationCockpit informationCockpit;
    private SideCockpitRealGame sideCockpitRealGame;
    private SideCockpitMission sideCockpitMission;

    public static SideCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SideCockpit() {
        questProgressCockpit = new QuestProgressCockpit();
        setupPanels();
        setupMoney();
        setupItemLimit();
        setDebugPanel();
        setupEnergy();
        setupRadar();
        setupButtonPanel();
        setupSocialNetPanel();
        informationCockpit = new InformationCockpit();
    }

    private void setupPanels() {
        mainPanel = new AbsolutePanel();
        mainPanel.getElement().getStyle().setBackgroundImage("url(" + ImageHandler.getCockpitImageUrl("cockpit.png") + ")");
        mainPanel.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        preventEvents(mainPanel);
        mainPanel.setPixelSize(MAIN_PANEL_W, MAIN_PANEL_H);

        levelPanel = new AbsolutePanel();
        levelPanel.getElement().getStyle().setBackgroundImage("url(" + ImageHandler.getCockpitImageUrl("cockpit.png") + ")");
        levelPanel.getElement().getStyle().setProperty("backgroundPosition", "-" + Integer.toString(LEVEL_PANEL_X) + "px 0");
        preventEvents(levelPanel);
        levelPanel.setPixelSize(LEVEL_PANEL_W, LEVEL_PANEL_H);
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
            VerticalPanel verticalPanel = new VerticalPanel();
            mainPanel.add(verticalPanel, DEBUG_X, DEBUG_Y);
            verticalPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
            verticalPanel.getElement().getStyle().setZIndex(10);
            debugPosition = new Label();
            verticalPanel.add(debugPosition);
            debugFrameRate = new Label();
            verticalPanel.add(debugFrameRate);
        }
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

        updateEnergy(0, 0);
    }

    private void setupRadar() {
        AbsolutePanel absolutePanel = RadarPanel.getInstance().createWidget(RADAR_W, RADAR_H);
        absolutePanel.setTitle(ToolTips.TOOL_TIP_RADAR);
        mainPanel.add(absolutePanel, RADAR_X, RADAR_Y);
    }

    private void setupButtonPanel() {
        // Inventory
        ExtendedCustomButton inventory = new ExtendedCustomButton("inventoryButton", false, ToolTips.TOOL_TIP_INVENTORY, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE) {
                    DialogManager.showDialog(new InventoryDialog(), DialogManager.Type.QUEUE_ABLE);
                } else {
                    DialogManager.showDialog(new MessageDialog("Inventory", "Inventory is not available on this planet."), DialogManager.Type.QUEUE_ABLE);
                }
            }
        });
        mainPanel.add(inventory, BNT_INVENTORY_X, BNT_INVENTORY_Y);

        //Alliance
        ExtendedCustomButton alliance = new ExtendedCustomButton("allianceButton", false, ToolTips.TOOL_TIP_ALLIANCE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogManager.showDialog(new AllianceDialog(), DialogManager.Type.QUEUE_ABLE);
            }
        });
        mainPanel.add(alliance, BNT_ALLIANCE_X, BNT_ALLIANCE_Y);

        // Sell button
        sellButton = new ExtendedCustomButton("sellButton", true, ToolTips.TOOL_TIP_SELL, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ExtendedCustomButton btn = (ExtendedCustomButton) event.getSource();
                if (btn.isDown()) {
                    CockpitMode.getInstance().setMode(CockpitMode.Mode.SELL);
                } else {
                    CockpitMode.getInstance().setMode(null);
                }
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
        // High score button
        ExtendedCustomButton highScoreButton = new ExtendedCustomButton("highscoreButton", false, ToolTips.TOOL_TIP_HIGH_SCORE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                DialogManager.showDialog(new HighscoreDialog(), DialogManager.Type.QUEUE_ABLE);
            }
        });
        mainPanel.add(highScoreButton, BNT_STAT_X, BNT_STAT_Y);
    }

    private void setupSocialNetPanel() {
        WebBrowserCustomButton facebookCommunity = new WebBrowserCustomButton("facebookcommunity", ToolTips.TOOL_TIP_FACEBOOK_COMMUNITY, "http://www.facebook.com/RazarionCommunity");
        mainPanel.add(facebookCommunity, BNT_FB_COMMUNITY_X, BNT_FB_COMMUNITY_Y);
        mainPanel.add(new ExtendedCustomButton("facebookinvite", false, ToolTips.TOOL_TIP_FACEBOOK_INVITE, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FacebookUtils.invite();
            }
        }), BNT_FB_INVITE_X, BNT_FB_INVITE_Y);
    }

    public void debugAbsoluteCursorPos(int x, int y) {
        debugPosition.setText(x + ":" + y);
    }

    public void debugFrameRate(int frameRate, int renderTime) {
        debugFrameRate.setText("Frame Rate: " + frameRate + " (" + renderTime + "ms)");
    }

    public void updateItemLimit() {
        if (itemLimit != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(ClientBase.getInstance().getOwnItemCount());
            builder.append("/");
            builder.append(ClientBase.getInstance().getHouseSpace() + ClientPlanetServices.getInstance().getPlanetInfo().getHouseSpace());
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
        if (sideCockpitRealGame != null) {
            sideCockpitRealGame.setLevel(levelScope);
        }
        onStateChanged();
    }


    public void setActiveQuest(QuestInfo questInfo, String activeQuestProgress) {
        questProgressCockpit.setActiveQuest(questInfo, activeQuestProgress);
    }

    public void setNoActiveQuest() {
        questProgressCockpit.setNoActiveQuest();
    }

    public void setWrongPlanet(boolean move) {
        questProgressCockpit.setWrongPlanet(move);
    }

    public void setXp(int xp, int xp2LevelUp) {
        if (sideCockpitRealGame != null) {
            sideCockpitRealGame.setXp(xp, xp2LevelUp);
        }
    }

    public void onStateChanged() {
        if (ItemCockpit.getInstance().isActive()) {
            ItemCockpit.getInstance().onStateChanged();
        }
    }

    public void updateEnergy(int generating, int consuming) {
        if (energyBar != null) {
            energyBar.setProgress(consuming, generating);
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
        mainPanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT);
        parent.add(levelPanel, LEVEL_PANEL_X, 0);
        levelPanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT);
        questProgressCockpit.addToParent(parent);
        informationCockpit.setPatent(parent);
    }

    private void preventEvents(Widget widget) {
        widget.addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                GwtCommon.preventDefault(event);
                ChatCockpit.getInstance().blurFocus();
            }
        }, MouseUpEvent.getType());

        widget.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                GwtCommon.preventDefault(event);
                ChatCockpit.getInstance().blurFocus();
            }
        }, MouseDownEvent.getType());
    }

    public void onBoxPicked(BoxPickedPacket boxPickedPacket) {
        informationCockpit.showBoxPicked(boxPickedPacket.getHtml());
    }

    public void initMission(SimulationInfo simulationInfo) {
        if (sideCockpitRealGame != null) {
            levelPanel.remove(sideCockpitRealGame);
            sideCockpitRealGame = null;
        }
        if (sideCockpitMission == null) {
            sideCockpitMission = new SideCockpitMission();
            levelPanel.add(sideCockpitMission, REAL_GAME_MISSION_X, REAL_GAME_MISSION_Y);
        }
        sideCockpitMission.setAbortable(simulationInfo.isAbortable());
        questProgressCockpit.enableQuestControl(false);
    }

    public void initRealGame(RealGameInfo realGameInfo) {
        if (sideCockpitMission != null) {
            levelPanel.remove(sideCockpitMission);
            sideCockpitMission = null;
        }
        if (sideCockpitRealGame == null) {
            sideCockpitRealGame = new SideCockpitRealGame();
            levelPanel.add(sideCockpitRealGame, REAL_GAME_MISSION_X, REAL_GAME_MISSION_Y);
        }
        sideCockpitRealGame.setXp(realGameInfo.getXpPacket().getXp(), realGameInfo.getXpPacket().getXp2LevelUp());
        sideCockpitRealGame.setLevel(realGameInfo.getLevelScope());
        questProgressCockpit.enableQuestControl(true);
    }
}
