package com.btxtech.game.jsre.client.cockpit.menu;

import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * User: beat
 * Date: 16.04.13
 * Time: 17:15
 */
public class MenuBarCockpit {
    private static final MenuBarCockpit INSTANCE = new MenuBarCockpit();
    private MenuBarPanel menuBarPanel;

    /**
     * Singleton
     */
    private MenuBarCockpit() {
        menuBarPanel = new MenuBarPanel();
    }

    public static MenuBarCockpit getInstance() {
        return INSTANCE;
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(menuBarPanel, SideCockpit.SIDE_COCKPIT_ENDS, 0);
        menuBarPanel.getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT);
    }

    public void initRealGame(SimpleUser simpleUser) {
        setSimpleUser(simpleUser);
        menuBarPanel.initRealGame();
    }

    public void initSimulated(SimpleUser simpleUser) {
        setSimpleUser(simpleUser);
        menuBarPanel.initSimulated();
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        menuBarPanel.setSimpleUser(simpleUser);
    }

    public void blinkNewBase(boolean blink) {
        menuBarPanel.blinkNewBase(blink);
    }

}
