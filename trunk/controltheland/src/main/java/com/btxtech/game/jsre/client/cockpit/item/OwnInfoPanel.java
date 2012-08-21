package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 18.08.12
 * Time: 13:04
 */
public class OwnInfoPanel extends Composite {
    private static OwnInfoPanelUiBinder uiBinder = GWT.create(OwnInfoPanelUiBinder.class);
    @UiField(provided = true)
    Image image;
    @UiField
    Label itemTypeName;
    @UiField
    HTML itemTypeDescr;
    @UiField Label countLabel;

    interface OwnInfoPanelUiBinder extends UiBinder<Widget, OwnInfoPanel> {
    }

    public OwnInfoPanel(BaseItemType baseItemType, int count) {
        image = ImageHandler.getItemTypeImage(baseItemType, 50, 50);
        initWidget(uiBinder.createAndBindUi(this));
        itemTypeName.setText(baseItemType.getName());
        itemTypeDescr.setHTML(baseItemType.getDescription());
        if(count > 1) {
            countLabel.setText(Integer.toString(count));
        }else {
            countLabel.setVisible(false); 
        }
        GwtCommon.preventDragImage(image);
    }

}
