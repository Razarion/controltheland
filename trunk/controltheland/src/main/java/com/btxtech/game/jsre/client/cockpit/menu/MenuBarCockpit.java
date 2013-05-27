package com.btxtech.game.jsre.client.cockpit.menu;

import com.btxtech.game.jsre.client.SimpleUser;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.common.packets.UserAttentionPacket;
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

    public void initRealGame(GameInfo gameInfo) {
        setSimpleUser(gameInfo.getSimpleUser());
        menuBarPanel.initRealGame();
        onUserAttentionPacket(gameInfo.getUserAttentionPacket());
    }

    public void initSimulated(GameInfo gameInfo) {
        setSimpleUser(gameInfo.getSimpleUser());
        menuBarPanel.initSimulated();
        onUserAttentionPacket(gameInfo.getUserAttentionPacket());
    }

    public void setSimpleUser(SimpleUser simpleUser) {
        menuBarPanel.setSimpleUser(simpleUser);
    }

    public void blinkNewBase(boolean blink) {
        menuBarPanel.blinkNewBase(blink);
    }

    public void onUserAttentionPacket(UserAttentionPacket userAttentionPacket) {
        if (userAttentionPacket.isNews()) {
            menuBarPanel.blinkNews(userAttentionPacket.getNews() == UserAttentionPacket.Type.RAISE);
        }
    }
}
