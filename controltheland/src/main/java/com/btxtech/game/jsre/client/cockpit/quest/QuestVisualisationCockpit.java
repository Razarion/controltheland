package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.quest.QuestDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.event.logical.shared.ValueChangeEvent;

public class QuestVisualisationCockpit extends Composite {

    private static QuestVisualisationCockpitUiBinder uiBinder = GWT.create(QuestVisualisationCockpitUiBinder.class);
    @UiField
    Label titleLabel;
    @UiField
    Button questDialogButton;
    @UiField
    SimplePanel mainPanel;
    @UiField CheckBox visualiseCheckBox;
    private QuestVisualisationPanel questVisualisationPanel;
    private static Logger log = Logger.getLogger(QuestVisualisationCockpit.class.getName());

    interface QuestVisualisationCockpitUiBinder extends UiBinder<Widget, QuestVisualisationCockpit> {
    }

    public QuestVisualisationCockpit() {
        initWidget(uiBinder.createAndBindUi(this));
        QuestVisualtsationModel.getInstance().setListener(this);
        questDialogButton.setStyleName("singleButton");
    }

    public void addToParent(AbsolutePanel parent) {
        parent.add(this, 0, 0);
        getElement().getStyle().setZIndex(Constants.Z_INDEX_SIDE_COCKPIT);
        getElement().getStyle().clearBottom();
        getElement().getStyle().clearLeft();
        getElement().getStyle().setTop(0, Style.Unit.PX);
        getElement().getStyle().setRight(0, Style.Unit.PX);
    }

    @UiHandler("questDialogButton")
    void onQuestDialogButtonClick(ClickEvent event) {
        DialogManager.showDialog(new QuestDialog(), DialogManager.Type.QUEUE_ABLE);
    }

    public void updateType(QuestInfo questInfo) {
        questVisualisationPanel = null;
        if (QuestVisualtsationModel.getInstance().isNoQuest()) {
            mainPanel.clear();
            titleLabel.setText("No active quest");
        } else if (QuestVisualtsationModel.getInstance().isShowStartMission()) {
            mainPanel.setWidget(new StartMissionPanel());
            titleLabel.setText(questInfo.getTitle());
        } else if (QuestVisualtsationModel.getInstance().isNextPlanet()) {
            mainPanel.setWidget(new NextPlanetPanel());
            titleLabel.setText("");
        } else {
            questVisualisationPanel = new QuestVisualisationPanel(questInfo);
            mainPanel.setWidget(questVisualisationPanel);
            titleLabel.setText(questInfo.getTitle());
        }
        questDialogButton.setVisible(Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE);
    }

    public void updateQuestProgress(QuestProgressInfo questProgressInfo) {
        if (QuestVisualtsationModel.getInstance().isNextPlanet()) {
            return;
        }
        if (questVisualisationPanel == null) {
            throw new NullPointerException("QuestVisualisationCockpit.updateQuestProgress() questVisualisationPanel == null");
        }
        try {
            questVisualisationPanel.update(questProgressInfo);
        } catch (Exception e) {
            log.log(Level.SEVERE, "QuestVisualisationCockpit.updateQuestProgress()", e);
        }
    }

    @UiHandler("visualiseCheckBox")
    void onVisualiseCheckBoxValueChange(ValueChangeEvent event) {
    }
}
