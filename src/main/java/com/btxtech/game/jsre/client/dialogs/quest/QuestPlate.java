package com.btxtech.game.jsre.client.dialogs.quest;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.unlock.ClientUnlockServiceImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuestPlate extends Composite {
    private static QuestPlateUiBinder uiBinder = GWT.create(QuestPlateUiBinder.class);
    private static QuestImages questImages = GWT.create(QuestImages.class);
    private int questId;
    private QuestInfo info;
    private QuestDialog questDialog;
    @UiField
    Button button;
    @UiField
    Label titel;
    @UiField
    Label rewards;
    @UiField(provided = true)
    Image icon;
    @UiField
    VerticalPanel descriptionPanel;

    interface QuestPlateUiBinder extends UiBinder<Widget, QuestPlate> {
    }

    public QuestPlate(QuestInfo info, QuestDialog questDialog) {
        this.info = info;
        this.questDialog = questDialog;
        if (info.getType() == QuestInfo.Type.MISSION) {
            icon = new Image(questImages.mission());
        } else {
            icon = new Image(questImages.quest());
        }
        initWidget(uiBinder.createAndBindUi(this));
        questId = info.getId();
        titel.setText(info.getTitle());
        rewards.setText(ClientI18nHelper.CONSTANTS.reward(info.getGold(), info.getXp()));
        if (QuestTypeEnum.isVisible(info.getQuestTypeEnum())) {
            descriptionPanel.add(new DescriptionBlock(ClientI18nHelper.CONSTANTS.questType(), info.getQuestTypeEnum().getString()));
        }
        descriptionPanel.add(new DescriptionBlock(ClientI18nHelper.CONSTANTS.description(), info.getDescription()));
        if (ClientUnlockServiceImpl.getInstance().isQuestLocked(info)) {
            button.setText(ClientI18nHelper.CONSTANTS.unlockButton());
        } else {
            button.setText(ClientI18nHelper.CONSTANTS.activate());
        }
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        if (ClientUnlockServiceImpl.getInstance().isQuestLocked(info)) {
            ClientUnlockServiceImpl.getInstance().askUnlockQuest(info, new Runnable() {
                @Override
                public void run() {
                    activateQuest();
                }
            });
        } else {
            activateQuest();
        }
    }

    private void activateQuest() {
        if (QuestVisualisationModel.getInstance().hasActiveQuest()) {
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
