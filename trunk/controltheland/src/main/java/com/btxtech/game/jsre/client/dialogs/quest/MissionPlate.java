package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualtsationModel;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MissionPlate extends Composite {
    private static MissionPlateUiBinder uiBinder = GWT.create(MissionPlateUiBinder.class);
    private int missionId;
    private QuestDialog questDialog;
    @UiField
    Button button;
    @UiField
    Label titel;
    @UiField
    Label rewards;
    @UiField
    HTML description;

    interface MissionPlateUiBinder extends UiBinder<Widget, MissionPlate> {
    }

    public MissionPlate(QuestInfo info, QuestDialog questDialog) {
        this.questDialog = questDialog;
        initWidget(uiBinder.createAndBindUi(this));
        missionId = info.getId();
        titel.setText(info.getTitle());
        rewards.setText(ClientI18nHelper.CONSTANTS.reward(info.getGold(), info.getXp()));
        description.setHTML(info.getDescription());
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        if (QuestVisualtsationModel.getInstance().hasActiveQuest()) {
            ClickHandler clickHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    questDialog.close();
                    Connection.getInstance().activateQuest(missionId);
                }
            };
            DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.activeQuest(), ClientI18nHelper.CONSTANTS.activeQuestAbort(), ClientI18nHelper.CONSTANTS.activate(), clickHandler, ClientI18nHelper.CONSTANTS.cancel(), null), DialogManager.Type.STACK_ABLE);
        } else {
            questDialog.close();
            Connection.getInstance().activateQuest(missionId);
        }
    }
}
