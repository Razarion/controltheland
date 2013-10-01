package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class SideCockpitMission extends Composite {

    private static SideCockpitMissionUiBinder uiBinder = GWT.create(SideCockpitMissionUiBinder.class);
    @UiField
    Image abortImage;
    @UiField
    InlineLabel abortLabel;

    interface SideCockpitMissionUiBinder extends UiBinder<Widget, SideCockpitMission> {
    }

    public SideCockpitMission() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setAbortable(boolean abortable) {
        abortImage.setVisible(abortable);
        abortLabel.setVisible(abortable);
    }

    @UiHandler("abortImage")
    void onAbortImageClick(ClickEvent event) {
        abortMission();
    }

    @UiHandler("abortLabel")
    void onAbortLabelClick(ClickEvent event) {
        abortMission();
    }

    private void abortMission() {
        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                QuestVisualisationModel.getInstance().abortMission();
            }
        };
        DialogManager.showDialog(new YesNoDialog(ClientI18nHelper.CONSTANTS.abortMission(),
                ClientI18nHelper.CONSTANTS.reallyAbortMission(),
                ClientI18nHelper.CONSTANTS.yes(), clickHandler, ClientI18nHelper.CONSTANTS.no(),
                null), DialogManager.Type.QUEUE_ABLE);
    }
}
