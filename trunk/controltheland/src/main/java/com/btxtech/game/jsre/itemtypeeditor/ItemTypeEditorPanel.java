package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 15.08.2011
 * Time: 20:27:37
 */
public class ItemTypeEditorPanel extends HorizontalPanel {
    private static final int CANVAS_WIDTH = 500;
    private static final int CANVAS_HEIGHT = 500;
    private Logger log = Logger.getLogger(ItemTypeEditorPanel.class.getName());
    private int itemTypeId;

    public ItemTypeEditorPanel(int itemTypeId) {
        this.itemTypeId = itemTypeId;
        ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
        itemTypeAccess.getBoundingBox(itemTypeId, new AsyncCallback<BoundingBox>() {
            @Override
            public void onFailure(Throwable caught) {
                log.log(Level.SEVERE, "getBoundingBox call failed", caught);
            }

            @Override
            public void onSuccess(BoundingBox boundingBox) {
                setupGui(boundingBox);
            }
        });
    }

    private void setupGui(BoundingBox boundingBox) {
        setSpacing(5);
        BoundingBoxControl boundingBoxControl = new BoundingBoxControl(itemTypeId, boundingBox);
        ItemTypeView itemTypeView = new ItemTypeView(CANVAS_WIDTH, CANVAS_HEIGHT, itemTypeId, boundingBox, boundingBoxControl);
        add(itemTypeView);
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setSpacing(5);
        add(verticalPanel);

        RotationControl rotationControl = new RotationControl(boundingBox, itemTypeView);
        boundingBoxControl.setRotationControl(rotationControl);
        verticalPanel.add(rotationControl);
        verticalPanel.add(boundingBoxControl);
    }
}
