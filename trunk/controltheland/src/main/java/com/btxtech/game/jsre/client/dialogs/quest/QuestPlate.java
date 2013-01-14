package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualtsationModel;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

public class QuestPlate extends Composite {
    private static QuestPlateUiBinder uiBinder = GWT.create(QuestPlateUiBinder.class);
    private static QuestImages questImages = GWT.create(QuestImages.class);
    private int questId;
    private QuestDialog questDialog;
    @UiField
    Button button;
    @UiField
    Label titel;
    @UiField
    Label rewards;
    @UiField
    HTML description;
    @UiField Label typeLabel;
    @UiField Label typeTitleLabel;
    @UiField(provided = true) Image icon;

    interface QuestPlateUiBinder extends UiBinder<Widget, QuestPlate> {
    }

    public QuestPlate(QuestInfo info, QuestDialog questDialog) {
        this.questDialog = questDialog;
        if(info.getType() == QuestInfo.Type.MISSION) {
            icon = new Image(questImages.mission()); 
        } else {
            icon = new Image(questImages.quest()); 
        }
        initWidget(uiBinder.createAndBindUi(this));
        questId = info.getId();
        titel.setText(info.getTitle());
        rewards.setText(ClientI18nHelper.CONSTANTS.reward(info.getGold(), info.getXp()));
        if(QuestTypeEnum.isVisible(info.getQuestTypeEnum())) {
            typeTitleLabel.setVisible(true); 
            typeLabel.setVisible(true);
            typeLabel.setText(info.getQuestTypeEnum().getString());
        } else {
            typeTitleLabel.setVisible(false); 
            typeLabel.setVisible(false); 
        }
        description.setHTML(info.getDescription());
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        if (QuestVisualtsationModel.getInstance().hasActiveQuest()) {
            ClickHandler clickHandler = new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    questDialog.close();
                    Connection.getInstance().activateQuest(questId);
                }
            };
            DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.activeQuest(), ClientI18nHelper.CONSTANTS.activeQuestAbort(), ClientI18nHelper.CONSTANTS.activate(), clickHandler, ClientI18nHelper.CONSTANTS.cancel(), null), DialogManager.Type.STACK_ABLE);
        } else {
            questDialog.close();
            Connection.getInstance().activateQuest(questId);
        }
    }
}
