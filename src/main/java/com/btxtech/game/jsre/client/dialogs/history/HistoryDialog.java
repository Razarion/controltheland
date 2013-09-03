package com.btxtech.game.jsre.client.dialogs.history;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * User: beat
 * Date: 20.05.13
 * Time: 13:40
 */
public class HistoryDialog extends Dialog {
    public HistoryDialog() {
        super(ClientI18nHelper.CONSTANTS.historyDialogTitle());
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            throw new IllegalArgumentException("HistoryDialog: only allowed if real game");
        }
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        dialogVPanel.add(new HistoryPanel());
    }
}
