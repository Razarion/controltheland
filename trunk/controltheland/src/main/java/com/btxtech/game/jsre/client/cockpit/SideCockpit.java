package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Level;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 06.11.2011
 * Time: 23:13:15
 */
public class SideCockpit extends AbsolutePanel {
    private static final SideCockpit INSTANCE = new SideCockpit();
    private static final int WIDTH = 200;
    public static final int HEIGHT = 540;
    // Common
    private static final int CONTROL_PANEL_WIDTH = 185;
    private static final int CONTROL_PANEL_LEFT = 5;
    private static final int CONTROL_PANEL_TOP = 25;

    private CockpitDisplayPanel cockpitDisplayPanel;
    private CockpitButtonPanel cockpitButtonPanel;
    private Label debugPosition;
    private CockpitMode cockpitMode;

    public static SideCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SideCockpit() {
        setPixelSize(WIDTH, HEIGHT);
        setupMetal();
        VerticalPanel verticalPanel = new VerticalPanel();
        add(verticalPanel, CONTROL_PANEL_LEFT, CONTROL_PANEL_TOP);
        setupControlPanel(verticalPanel);
        setupRadar(verticalPanel);
        setupButtonPanel(verticalPanel);
        if (Game.isDebug()) {
            debugPosition = new Label();
            debugPosition.getElement().getStyle().setBackgroundColor("#FFFFFF");
            add(debugPosition, CONTROL_PANEL_LEFT, CONTROL_PANEL_TOP);
        }
        preventEvents();
        cockpitMode = new CockpitMode();
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 0);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_TOP_MAP_PANEL);
    }

    private void setupMetal() {
        getElement().getStyle().setBackgroundImage("url(/images/cockpit/grid.jpg)");
        // Top
        AbsolutePanel topBorder = new AbsolutePanel();
        topBorder.getElement().getStyle().setBackgroundImage("url(/images/cockpit/metallBorderH.jpg)");
        topBorder.setPixelSize(WIDTH, 17);
        topBorder.getElement().getStyle().setZIndex(0);
        add(topBorder, 0, 0);
        // Bottom
        AbsolutePanel bottomBorder = new AbsolutePanel();
        bottomBorder.getElement().getStyle().setBackgroundImage("url(/images/cockpit/metallBorderH.jpg)");
        bottomBorder.setPixelSize(WIDTH, 17);
        bottomBorder.getElement().getStyle().setZIndex(0);
        add(bottomBorder, 0, HEIGHT - 17);
        // Right
        AbsolutePanel rightBorder = new AbsolutePanel();
        rightBorder.getElement().getStyle().setBackgroundImage("url(/images/cockpit/metallBorderV.jpg)");
        rightBorder.setPixelSize(17, HEIGHT - 2);
        rightBorder.getElement().getStyle().setZIndex(1);
        add(rightBorder, WIDTH - 17, 0);
    }

    private void setupControlPanel(VerticalPanel verticalPanel) {
        cockpitDisplayPanel = new CockpitDisplayPanel(CONTROL_PANEL_WIDTH);
        verticalPanel.add(cockpitDisplayPanel);
    }

    private void setupRadar(VerticalPanel verticalPanel) {
        RadarControlPanel radarControlPanel = new RadarControlPanel(CONTROL_PANEL_WIDTH, CONTROL_PANEL_WIDTH);
        verticalPanel.add(radarControlPanel);
    }

    private void setupButtonPanel(VerticalPanel verticalPanel) {
        cockpitButtonPanel = new CockpitButtonPanel(CONTROL_PANEL_WIDTH);
        verticalPanel.add(cockpitButtonPanel);
    }

    public void debugAbsoluteCursorPos(int x, int y) {
        debugPosition.setText(x + ":" + y);
    }

    public void updateMoney() {
        double accountBalance = ClientBase.getInstance().getAccountBalance();
        if (cockpitDisplayPanel != null) {
            cockpitDisplayPanel.updateMoney(accountBalance);
        }
        if (ItemCockpit.getInstance().isActive()) {
            ItemCockpit.getInstance().onMoneyChanged(accountBalance);
        }
    }

    public void setGameInfo(RealityInfo realityInfo) {
        cockpitDisplayPanel.updateMoney(realityInfo.getAccountBalance());
    }

    public void setLevel(Level level) {
        if (cockpitDisplayPanel != null) {
            cockpitDisplayPanel.setLevel(level);
        }
        onStateChanged();
    }

    public void onStateChanged() {
        if (ItemCockpit.getInstance().isActive()) {
            ItemCockpit.getInstance().onStateChanged();
        }
    }

    public void updateItemLimit() {
        if (cockpitDisplayPanel != null) {
            cockpitDisplayPanel.updateItemLimit();
        }
    }

    public void updateEnergy(int generating, int consuming) {
        if (cockpitDisplayPanel != null) {
            cockpitDisplayPanel.updateEnergy(generating, consuming);
        }
    }

    public void clearSellMode() {
        if (cockpitButtonPanel != null) {
            cockpitButtonPanel.clearSellMode();
        }
    }

    private void preventEvents() {
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        addDomHandler(new MouseUpHandler() {
            @Override
            public void onMouseUp(MouseUpEvent event) {
                GwtCommon.preventDefault(event);
            }
        }, MouseUpEvent.getType());

        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                ItemCockpit.getInstance().deActivate();
                GwtCommon.preventDefault(event);
            }
        }, MouseDownEvent.getType());
        addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                GwtCommon.preventDefault(event);
            }
        }, MouseDownEvent.getType());
    }

    public Rectangle getArea() {
        return new Rectangle(getAbsoluteLeft(), getAbsoluteTop(), WIDTH, HEIGHT);
    }


    public void setRadarItems() {
        RadarPanel.getInstance().setRadarItemsVisible();
    }

    public CockpitMode getCockpitMode() {
        return cockpitMode;
    }    
}
