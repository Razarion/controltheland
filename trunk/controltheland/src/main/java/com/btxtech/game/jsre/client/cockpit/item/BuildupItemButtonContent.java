package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BuildupItemButtonContent extends Composite {
    private static BuildupItemButtonContentUiBinder uiBinder = GWT.create(BuildupItemButtonContentUiBinder.class);
    @UiField
    Label itemLimitLabel;
    @UiField(provided = true)
    Image image;

    interface BuildupItemButtonContentUiBinder extends UiBinder<Widget, BuildupItemButtonContent> {
    }

    public BuildupItemButtonContent(BaseItemType baseItemType) {
        image = ImageHandler.getItemTypeImage(baseItemType, 40, 40);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            image.getElement().getStyle().setOpacity(1.0);
        } else {
            image.getElement().getStyle().setOpacity(0.5);
        }
    }

    public void setText(String text) {
        itemLimitLabel.setText(text);
    }

}
