package com.btxtech.game.jsre.client.cockpit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.InlineLabel;

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
        System.out.println("onImageClick");
    }

    @UiHandler("abortLabel")
    void onAbortLabelClick(ClickEvent event) {
        System.out.println("onInlineLabelClick");
    }
}
