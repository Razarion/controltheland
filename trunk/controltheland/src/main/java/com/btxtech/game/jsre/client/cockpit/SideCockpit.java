package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * User: beat
 * Date: 06.11.2011
 * Time: 23:13:15
 */
public class SideCockpit extends AbsolutePanel {
    private static final SideCockpit INSTANCE = new SideCockpit();
    private static final int WIDTH = 200;
    public static final int HEIGHT = 395;
    // Common
    private static final int CONTROL_PANEL_WIDTH = 185;
    private static final int CONTROL_PANEL_LEFT = 5;
    // Control Panel
    private static final int CONTROL_PANEL_HEIGHT = 160;
    private static final int CONTROL_PANEL_TOP = 25;
    // Radar
    private static final int RADAR_TOP = CONTROL_PANEL_HEIGHT + CONTROL_PANEL_TOP + 10;

    public static SideCockpit getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private SideCockpit() {
        setPixelSize(WIDTH, HEIGHT);
        setupMetal();
        setupControlPanel();
        setupRadar();
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

    private void setupControlPanel() {
        CockpitControlPanel cockpitControlPanel = new CockpitControlPanel(CONTROL_PANEL_WIDTH, CONTROL_PANEL_HEIGHT);
        add(cockpitControlPanel, CONTROL_PANEL_LEFT, CONTROL_PANEL_TOP);
    }

    private void setupRadar() {
        @SuppressWarnings({"SuspiciousNameCombination"})
        RadarControlPanel radarControlPanel = new RadarControlPanel(CONTROL_PANEL_WIDTH, CONTROL_PANEL_WIDTH);
        add(radarControlPanel, CONTROL_PANEL_LEFT, RADAR_TOP);
    }
}
