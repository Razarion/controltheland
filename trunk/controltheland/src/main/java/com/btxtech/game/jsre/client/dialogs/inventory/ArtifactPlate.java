package com.btxtech.game.jsre.client.dialogs.inventory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalPanel;

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
        panel.setTitle("Artifact: " + artifactName);
    }

    public void setText(String text) {
    }

    public String getText() {
        return null;
    }

    public void setStatus(int available, int needed) {
        label.setText(available + "/" + needed);
        Style style = label.getElement().getStyle();
        if (available == 0) {
            style.setColor("#FF0000");
            image.getElement().getStyle().setOpacity(0.3);
        } else if (needed > available) {
            style.setColor("#FFFF00");
            image.getElement().getStyle().setOpacity(1);
        } else {
            style.setColor("#00FF00");
            image.getElement().getStyle().setOpacity(1);
        }
    }
}
