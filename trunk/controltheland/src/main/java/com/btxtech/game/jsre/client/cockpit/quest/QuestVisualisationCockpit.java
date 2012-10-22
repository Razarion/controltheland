package com.btxtech.game.jsre.client.cockpit.quest;

import java.util.logging.Logger;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.SimplePanel;

public class QuestVisualisationCockpit extends Composite {

    private static QuestVisualisationCockpitUiBinder uiBinder = GWT.create(QuestVisualisationCockpitUiBinder.class);
    @UiField
    Label titleLabel;
    @UiField
    Button questDialogButton;
    @UiField
    SimplePanel mainPanel;
    private QuestVisualtisationPanel questVisualtisationPanel;

    interface QuestVisualisationCockpitUiBinder extends UiBinder<Widget, QuestVisualisationCockpit> {
    }

    public QuestVisualisationCockpit() {
        initWidget(uiBinder.createAndBindUi(this));
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
    }

    public void updateType(QuestInfo questInfo) {
        questVisualtisationPanel = null;
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
            mainPanel.setWidget(questVisualtisationPanel);
            titleLabel.setText(questInfo.getTitle());
            questVisualtisationPanel = new QuestVisualtisationPanel();
        }
    }

    public void updateQuestProgress(QuestProgressInfo questProgressInfo) {
        if (questVisualtisationPanel == null) {
            throw new NullPointerException("QuestVisualisationCockpit.updateQuestProgress() questVisualtisationPanel == null");
        }
        questVisualtisationPanel.update(questProgressInfo);
    }

}
