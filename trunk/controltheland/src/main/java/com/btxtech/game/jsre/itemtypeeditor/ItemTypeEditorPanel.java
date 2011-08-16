package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
    private final int imageCount = 24;

    public ItemTypeEditorPanel(int itemTypeId) {
        //setPixelSize(700, 900);
        // Canvas
        setSpacing(5);
        BoundingBoxControl boundingBoxControl = new BoundingBoxControl(new Index(40, 40), new Index(10, 10), 80, 80);
        ItemTypeEditorView itemTypeEditorView = new ItemTypeEditorView(CANVAS_WIDTH, CANVAS_HEIGHT, itemTypeId, imageCount, boundingBoxControl);
        add(itemTypeEditorView);
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setSpacing(5);
        add(verticalPanel);

        RotationControl rotationControl = new RotationControl(imageCount, itemTypeEditorView);
        boundingBoxControl.setRotationControl(rotationControl);
        verticalPanel.add(rotationControl);
        verticalPanel.add(boundingBoxControl);
    }
}
