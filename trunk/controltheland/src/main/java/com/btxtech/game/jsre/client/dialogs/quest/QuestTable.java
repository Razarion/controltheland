package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class QuestTable extends Composite {

    private static QuestTableUiBinder uiBinder = GWT.create(QuestTableUiBinder.class);
    @UiField
    FlexTable questTable;
    @UiField
    Label questCountLabel;
    @UiField
    Label missionCountLabel;

    interface QuestTableUiBinder extends UiBinder<Widget, QuestTable> {
    }

    public QuestTable() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void displayQuestOverview(QuestDialog questDialog, QuestOverview questOverview) {
        questCountLabel.setText(ClientI18nHelper.CONSTANTS.questOverview(questOverview.getQuestsDone(), questOverview.getTotalQuests()));
        missionCountLabel.setText(ClientI18nHelper.CONSTANTS.missionOverview(questOverview.getMissionsDone(), questOverview.getTotalMissions()));
        questTable.removeAllRows();
        for (QuestInfo questInfo : questOverview.getQuestInfos()) {
            questTable.setWidget(questTable.getRowCount(), 0, new QuestPlate(questInfo, questDialog));
        }
    }
}
