package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.utg.ClientUserGuidanceService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;

public class NextPlanetPanel extends Composite {

    private static NextPlanetPanelUiBinder uiBinder = GWT.create(NextPlanetPanelUiBinder.class);
    @UiField PushButton startButton;

    interface NextPlanetPanelUiBinder extends UiBinder<Widget, NextPlanetPanel> {
    }

    public NextPlanetPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("startButton")
    void onStartButtonClick(ClickEvent event) {
        ClientUserGuidanceService.moveToNextPlanet();
    }
}
