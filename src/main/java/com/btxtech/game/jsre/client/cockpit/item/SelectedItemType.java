package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SelectedItemType extends Composite {

    private static SelectedItemTypeUiBinder uiBinder = GWT.create(SelectedItemTypeUiBinder.class);
    @UiField(provided = true)
    Image image;
    @UiField
    Label countLabel;
    private final BaseItemType baseItemType;

    interface SelectedItemTypeUiBinder extends UiBinder<Widget, SelectedItemType> {
    }

    public SelectedItemType(BaseItemType baseItemType, int count) {
        this.baseItemType = baseItemType;
        image = ImageHandler.getItemTypeImage(baseItemType, 40, 40);
        initWidget(uiBinder.createAndBindUi(this));
        countLabel.setText(Integer.toString(count));
        setTitle(ClientI18nHelper.CONSTANTS.tooltipSelect(ClientI18nHelper.getLocalizedString(baseItemType.getI18Name())));
    }

    @UiHandler("image")
    void onImageMouseDown(MouseDownEvent event) {
        SelectionHandler.getInstance().keepOnlyOwnOfType(baseItemType);
    }

    @UiHandler("countLabel")
    void onCountLabelMouseDown(MouseDownEvent event) {
        SelectionHandler.getInstance().keepOnlyOwnOfType(baseItemType);
    }
}
