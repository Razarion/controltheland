package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GuildInvitationsDialog extends Dialog {

    public GuildInvitationsDialog() {
        super(ClientI18nHelper.CONSTANTS.guildInvitations());
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalStateException("GuildInvitationsDialog wrong game engine mode: " + Connection.getInstance().getGameEngineMode());
        }
        if (!ClientUserService.getInstance().isRegisteredAndVerified()) {
            throw new IllegalStateException("GuildInvitationsDialog user is not registered");
        }
        if (ClientBase.getInstance().isGuildMember()) {
            throw new IllegalStateException("GuildInvitationsDialog user is already member of a guild");
        }
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new GuildInvitationsPanel(this));
    }
}
