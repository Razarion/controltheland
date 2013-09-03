package com.btxtech.game.jsre.client.dialogs.highscore;

import com.btxtech.game.jsre.client.ClientI18nConstants;
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;

public class HighscoreDialog extends Dialog {
    private HighscoreTable highscoreTable;

    public HighscoreDialog() {
        super(ClientI18nHelper.CONSTANTS.highScore());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        highscoreTable = new HighscoreTable(this);
        dialogVPanel.add(highscoreTable);
        Connection.getInstance().loadCurrentStatisticEntryInfos(this);
    }

    public void onHighscoreRecived(Collection<CurrentStatisticEntryInfo> highscore) {
        if (isShowing()) {
            highscoreTable.setHighscore(highscore);
            center();
        }
    }
}
