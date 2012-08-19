package com.btxtech.game.jsre.client.cockpit.item;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
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
public class OtherInfoPanel extends Composite {
    private static OwnInfoPanelUiBinder uiBinder = GWT.create(OwnInfoPanelUiBinder.class);
    @UiField(provided = true)
    Image image;
    @UiField
    Label itemTypeName;
    @UiField
    Label type;
    @UiField
    Button offerAlliance;
    @UiField
    HTML itemTypeDescr;
    private SimpleBase simpleBase;

    interface OwnInfoPanelUiBinder extends UiBinder<Widget, OtherInfoPanel> {
    }

    public OtherInfoPanel(SyncItem syncItem) {
        image = ImageHandler.getItemTypeImage(syncItem.getItemType(), 50, 50);
        initWidget(uiBinder.createAndBindUi(this));
        GwtCommon.preventDragImage(image);
        itemTypeName.setText(syncItem.getItemType().getName());
        itemTypeDescr.setHTML(syncItem.getItemType().getDescription());
        offerAlliance.setVisible(false);
        if (syncItem instanceof SyncBaseItem) {
            SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
            if (ClientBase.getInstance().isBot(syncBaseItem.getBase())) {
                type.setText("Bot enemy: " + ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
            } else if (ClientBase.getInstance().isEnemy(syncBaseItem)) {
                simpleBase = syncBaseItem.getBase();
                type.setText("Player enemy: " + ClientBase.getInstance().getBaseName(simpleBase));
                offerAlliance.setVisible(true);
            } else {
                type.setText("Alliance member: " + ClientBase.getInstance().getBaseName(syncBaseItem.getBase()));
            }
        } else if (syncItem instanceof SyncResourceItem) {
            type.setVisible(false);
        } else if (syncItem instanceof SyncBoxItem) {
            type.setVisible(false);
        }
    }

    @UiHandler("offerAlliance")
    void onButtonClick(ClickEvent event) {
        Connection.getInstance().proposeAlliance(simpleBase);
    }

}
