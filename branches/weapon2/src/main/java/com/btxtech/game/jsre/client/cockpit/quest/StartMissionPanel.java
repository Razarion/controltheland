package com.btxtech.game.jsre.client.cockpit.quest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class StartMissionPanel extends Composite {

    private static StartMissionPanelUiBinder uiBinder = GWT.create(StartMissionPanelUiBinder.class);
    @UiField
    PushButton startButton;

    interface StartMissionPanelUiBinder extends UiBinder<Widget, StartMissionPanel> {
    }

    public StartMissionPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("startButton")
    void onStartButtonClick(ClickEvent event) {
        QuestVisualisationModel.getInstance().startMission();
    }
}
