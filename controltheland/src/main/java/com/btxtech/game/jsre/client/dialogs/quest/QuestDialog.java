package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QuestDialog extends Dialog {
    private static QuestDialog staticInstance;
    private QuestTable questTable;

    public QuestDialog() {
        super(ClientI18nHelper.CONSTANTS.quests());
    }

    @Override
    protected void setupPanel(VerticalPanel dialogVPanel) {
        questTable = new QuestTable();
        dialogVPanel.add(questTable);
        Connection.getInstance().loadQuestOverview(this);
        staticInstance = this;
    }

    public void displayQuestOverview(QuestOverview questOverview) {
        questTable.displayQuestOverview(this, questOverview);
        center();
    }

    @Override
    public void close() {
        staticInstance = null;
        super.close();
    }

    public static void updateQuestDialog() {
        if (staticInstance != null) {
            Connection.getInstance().loadQuestOverview(staticInstance);
        }
    }
}
