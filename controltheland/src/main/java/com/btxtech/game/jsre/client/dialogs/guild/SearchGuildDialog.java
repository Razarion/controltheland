package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchGuildDialog extends Dialog {

    public SearchGuildDialog() {
        super(ClientI18nHelper.CONSTANTS.searchGuildDialogTitle());
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalStateException("SearchGuildDialog wrong game engine mode: " + Connection.getInstance().getGameEngineMode());
        }
        if (!ClientUserService.getInstance().isRegisteredAndVerified()) {
            throw new IllegalStateException("SearchGuildDialog user is not registered");
        }
        if (ClientBase.getInstance().isGuildMember()) {
            throw new IllegalStateException("SearchGuildDialog user is already member of a guild");
        }
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new SearchGuildPanel());
    }
}
