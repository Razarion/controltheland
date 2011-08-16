package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.DecoratorPanel;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:28:30
 */
public class ItemTypeEditorView extends DecoratorPanel {
    private static final int ITEM_TYPE_LEFT = 100;
    private static final int ITEM_TYPE_TOP = 100;
    private final Index offset = new Index(ITEM_TYPE_LEFT, ITEM_TYPE_TOP);
    private Context2d context2d;
    private ImageLoader imageLoader;
    private CssColor redrawColor = CssColor.make(255, 255, 255);
    private int width;
    private int height;
    private int imageCount;
    private BoundingBoxControl boundingBoxControl;
    private boolean showBoundingBox = true;

    public ItemTypeEditorView(int width, int height, int itemTypeId, int imageCount, BoundingBoxControl boundingBoxControl) {
        this.width = width;
        this.height = height;
        this.imageCount = imageCount;
        this.boundingBoxControl = boundingBoxControl;
        Canvas canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new IllegalStateException("Canvas is not supported.");
        }
        canvas.setPixelSize(width, height);
        setWidget(canvas);
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        context2d = canvas.getContext2d();
        imageLoader = new ImageLoader(itemTypeId, imageCount, new ImageLoader.Listener() {
            @Override
            public void onLoaded() {
                draw(0);
            }
        });
    }

    public void showBoundingBox(boolean value) {
        showBoundingBox = value;
    }

    /**
     * @param imageNr from 0 to imageCount - 1
     */
    public void draw(int imageNr) {
        if (!imageLoader.isLoaded()) {
            return;
        }
        //context2d.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        context2d.setFillStyle(redrawColor);
        context2d.fillRect(0, 0, width, height);
        context2d.setLineWidth(2);

        context2d.drawImage(imageLoader.getImage(imageNr), ITEM_TYPE_LEFT, ITEM_TYPE_TOP);
        // Bounding box
        if (showBoundingBox) {
            boundingBoxControl.boundingBox(imageNr, imageCount, offset, context2d);
        }
    }

}
