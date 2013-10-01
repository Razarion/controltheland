package com.btxtech.game.jsre.client.cockpit.menu;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.client.common.info.SimpleUser;
import com.btxtech.game.jsre.client.dialogs.AskOpenDialog;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.guild.GuildInvitationsDialog;
import com.btxtech.game.jsre.client.dialogs.guild.MyGuildDialog;
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

    public void initRealGame(RealGameInfo gameInfo) {
        menuBarPanel.initRealGame(gameInfo);
        onUserAttentionPacket(gameInfo.getUserAttentionPacket());
    }

    public void initSimulated(GameInfo gameInfo) {
        menuBarPanel.initSimulated();
        onUserAttentionPacket(gameInfo.getUserAttentionPacket());
    }

    public void updateUser() {
        menuBarPanel.updateUser();
    }

    public void blinkNewBase(boolean blink) {
        menuBarPanel.blinkNewBase(blink);
    }

    public void onUserAttentionPacket(UserAttentionPacket userAttentionPacket) {
        if (userAttentionPacket.isNews()) {
            menuBarPanel.blinkNews(userAttentionPacket.getNews() == UserAttentionPacket.Type.RAISE);
        }
        if (userAttentionPacket.getGuildInvitation() == UserAttentionPacket.Type.RAISE) {
            DialogManager.showDialog(new AskOpenDialog(ClientI18nHelper.CONSTANTS.guildInvitations(),
                    ClientI18nHelper.CONSTANTS.guildInvitationNotification(),
                    ClientI18nHelper.CONSTANTS.openGuildInvitation(),
                    new Runnable() {
                        @Override
                        public void run() {
                            DialogManager.showDialog(new GuildInvitationsDialog(), DialogManager.Type.QUEUE_ABLE);
                        }
                    }
            ), DialogManager.Type.QUEUE_ABLE);
        }
        if (userAttentionPacket.getGuildMembershipRequest() == UserAttentionPacket.Type.RAISE) {
            DialogManager.showDialog(new AskOpenDialog(ClientI18nHelper.CONSTANTS.guildMembershipRequestTitle(),
                    ClientI18nHelper.CONSTANTS.guildMembershipRequestNotification(),
                    ClientI18nHelper.CONSTANTS.openGuildMembershipRequest(),
                    new Runnable() {
                        @Override
                        public void run() {
                            DialogManager.showDialog(new MyGuildDialog(), DialogManager.Type.QUEUE_ABLE);
                        }
                    }
            ), DialogManager.Type.QUEUE_ABLE);
        }
    }

    public void updateGuild(SimpleGuild mySimpleGuild) {
        menuBarPanel.updateGuild(mySimpleGuild);
    }
}
