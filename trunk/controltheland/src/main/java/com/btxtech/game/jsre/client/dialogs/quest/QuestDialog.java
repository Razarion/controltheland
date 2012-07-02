package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.dialogs.Dialog;
import com.btxtech.game.jsre.client.dialogs.inventory.Inventory;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;
import java.util.List;

public class QuestDialog extends Dialog {
    private static QuestDialog staticInstance;
    private QuestTable questTable;

    public QuestDialog() {
        super("Quests");
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
