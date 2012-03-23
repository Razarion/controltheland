package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:28:30
 */
public class ItemTypeView extends DecoratorPanel {
    public static final Index ITEM_POSITION = new Index(150, 150);
    private Context2d context2d;
    private ItemTypeImageLoader imageLoader;
    private CssColor redrawColor = CssColor.make(255, 255, 255);
    private int canvasWidth;
    private int canvasHeight;
    private ItemType itemType;
    private BoundingBoxControl boundingBoxControl;
    private MuzzleFlashControl muzzleFlashControl;

    public ItemTypeView(int canvasWidth, int canvasHeight, ItemType itemType, BoundingBoxControl boundingBoxControl, MuzzleFlashControl muzzleFlashControl) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.itemType = itemType;
        this.boundingBoxControl = boundingBoxControl;
        this.muzzleFlashControl = muzzleFlashControl;
        Canvas canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new IllegalStateException("ItemTypeEditorPanel: Canvas is not supported.");
        }
        canvas.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                ItemTypeView.this.muzzleFlashControl.onClick(event);
            }
        });
        setWidget(canvas);
        canvas.setCoordinateSpaceWidth(canvasWidth);
        canvas.setCoordinateSpaceHeight(canvasHeight);
        context2d = canvas.getContext2d();
        imageLoader = new ItemTypeImageLoader(itemType.getId(), new ImageLoader.Listener() {
            @Override
            public void onLoaded() {
                draw(0);
            }
        });
    }

    /**
     * @param imageNr from 0 to imageCount - 1
     */
    public void draw(int imageNr) {
        if (!imageLoader.isLoaded()) {
            return;
        }
        context2d.setFillStyle(redrawColor);
        context2d.fillRect(0, 0, canvasWidth, canvasHeight);
        context2d.setLineWidth(2);

        BoundingBox boundingBox = itemType.getBoundingBox();
        SyncItemArea syncItemArea = boundingBox.createSyntheticSyncItemArea(ITEM_POSITION, boundingBox.imageNumberToAngel(imageNr));

        Index imageOffset = boundingBox.getTopLeftFromImage(ITEM_POSITION);

        context2d.drawImage(imageLoader.getImage(0),
                boundingBox.getImageWidth() * imageNr, // the x coordinate of the upper-left corner of the source rectangle
                0, // the y coordinate of the upper-left corner of the source rectangle
                boundingBox.getImageWidth(),// the width of the source rectangle
                boundingBox.getImageHeight(),// sh the width of the source rectangle
                imageOffset.getX(),// the x coordinate of the upper-left corner of the destination rectangle
                imageOffset.getY(),// the y coordinate of the upper-left corner of the destination rectangle
                boundingBox.getImageWidth(),// the width of the destination rectangle
                boundingBox.getImageHeight()// the height of the destination rectangle
        );
        // Bounding box
        boundingBoxControl.draw(syncItemArea, context2d);
        muzzleFlashControl.draw(imageNr, context2d);
    }
}
