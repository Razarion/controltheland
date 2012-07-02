package com.btxtech.game.jsre.client.dialogs.quest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class QuestTable extends Composite {

    private static QuestTableUiBinder uiBinder = GWT.create(QuestTableUiBinder.class);
    @UiField
    FlexTable questTable;
    @UiField
    HTML quests;
    @UiField
    HTML missions;

    interface QuestTableUiBinder extends UiBinder<Widget, QuestTable> {
    }

    public QuestTable() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void displayQuestOverview(QuestDialog questDialog, QuestOverview questOverview) {
        quests.setHTML("Quests: " + questOverview.getQuestsDone() + "/" + questOverview.getTotalQuests());
        missions.setHTML("Missions: " + questOverview.getMissionsDone() + "/" + questOverview.getTotalMissions());
        questTable.clear();
        for (QuestInfo questInfo : questOverview.getQuestInfos()) {
            switch (questInfo.getType()) {
                case MISSION:
                    questTable.setWidget(questTable.getRowCount(), 0, new MissionPlate(questInfo, questDialog));
                    break;
                case QUEST:
                    questTable.setWidget(questTable.getRowCount(), 0, new QuestPlate(questInfo, questDialog));
                    break;
                default:
                    throw new IllegalArgumentException("QuestTable: unknown type: " + questInfo.getType());

            }
        }
    }
}
