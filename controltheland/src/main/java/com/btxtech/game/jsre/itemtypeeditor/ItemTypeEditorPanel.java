package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 15.08.2011
 * Time: 20:27:37
 */
public class ItemTypeEditorPanel extends FlexTable {
    private Logger log = Logger.getLogger(ItemTypeEditorPanel.class.getName());
    private int itemTypeId;

    public ItemTypeEditorPanel(int itemTypeId) {
        this.itemTypeId = itemTypeId;
        ItemTypeAccessAsync itemTypeAccess = GWT.create(ItemTypeAccess.class);
        itemTypeAccess.getItemType(itemTypeId, new AsyncCallback<ItemType>() {
            @Override
            public void onFailure(Throwable caught) {
                log.log(Level.SEVERE, "getBoundingBox call failed", caught);
            }

            @Override
            public void onSuccess(ItemType itemType) {
                setupGui(itemType);
            }
        });
    }

    private void setupGui(ItemType itemType) {
        // Create panels
        BoundingBoxControl boundingBoxControl = new BoundingBoxControl(itemTypeId, itemType.getBoundingBox());
        ItemTypeSimulation itemTypeSimulation = new ItemTypeSimulation(500, 500, itemType);
        ItemTypeView itemTypeView = new ItemTypeView(300, 300, itemType, boundingBoxControl);
        RotationControl rotationControl = new RotationControl(itemType.getBoundingBox(), itemTypeView);
        // Init panels
        boundingBoxControl.setRotationControl(rotationControl);
        // Add panels to main panel
        setWidget(0, 0, itemTypeView);
        getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        getFlexCellFormatter().setRowSpan(0, 0, 2);
        setWidget(0, 1, rotationControl);
        // Col is 0 (only one col in second row)
        setWidget(1, 0, boundingBoxControl);
        getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        setWidget(0, 2, itemTypeSimulation);
        getFlexCellFormatter().setRowSpan(0, 2, 2);
    }
}
