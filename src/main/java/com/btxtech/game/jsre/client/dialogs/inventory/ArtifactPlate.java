package com.btxtech.game.jsre.client.dialogs.inventory;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ArtifactPlate extends Composite implements HasText {

    private static ArtifactPlateUiBinder uiBinder = GWT.create(ArtifactPlateUiBinder.class);
    @UiField
    Label label;
    @UiField
    Image image;
    @UiField VerticalPanel panel;

    interface ArtifactPlateUiBinder extends UiBinder<Widget, ArtifactPlate> {
    }

    public ArtifactPlate(String artifactName, String url, int available, int needed, String bgColor) {
        initWidget(uiBinder.createAndBindUi(this));
        image.setUrl(url);
        setStatus(available, needed);
        panel.getElement().getStyle().setBackgroundColor(bgColor);
        panel.setTitle(ClientI18nHelper.CONSTANTS.tooltipArtifact(artifactName));
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

    public void setStatus(int available, int needed) {
        label.setText(available + "/" + needed);
        Style style = label.getElement().getStyle();
        if (needed > available) {
            style.setColor("#FF0000");
        } else {
            style.setColor("#00FF00");
        }
    }
}
