package com.btxtech.game.jsre.client.cockpit.quest;

import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.client.unlock.ClientUnlockServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NextPlanetLocketPanel extends Composite {
    private static NextPlanetLocketPanelUiBinder uiBinder = GWT.create(NextPlanetLocketPanelUiBinder.class);
    @UiField
    Button button;

    interface NextPlanetLocketPanelUiBinder extends UiBinder<Widget, NextPlanetLocketPanel> {
    }

    public NextPlanetLocketPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        ClientUnlockServiceImpl.getInstance().askUnlockPlanet(QuestVisualisationModel.getInstance().getNextPlanet(), null);
    }
}
