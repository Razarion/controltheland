package com.btxtech.game.jsre.client.dialogs.guild;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MyGuildDialog extends Dialog {
    private static MyGuildDialog showingInstance;
    private MyGuildPanel myGuildPanel;

    public MyGuildDialog() {
        super(ClientI18nHelper.CONSTANTS.myGuildDialogTitle());
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalStateException("MyGuildDialog wrong game engine mode: " + Connection.getInstance().getGameEngineMode());
        }
        if (!ClientUserService.getInstance().isRegisteredAndVerified()) {
            throw new IllegalStateException("MyGuildDialog user is not registered");
        }
        if (!ClientBase.getInstance().isGuildMember()) {
            throw new IllegalStateException("MyGuildDialog user is not member of a guild");
        }
        showingInstance = this;
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        myGuildPanel = new MyGuildPanel(this);
        dialogVPanel.add(myGuildPanel);
    }

    @Override
    public void close() {
        super.close();
        showingInstance = null;
    }

    public static void updateIfShowing(FullGuildInfo fullGuildInfo) {
        if (showingInstance != null && showingInstance.myGuildPanel != null) {
            showingInstance.myGuildPanel.onFullGuildInfo(fullGuildInfo);
        }
    }
}
