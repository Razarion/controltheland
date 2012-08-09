package com.btxtech.game.jsre.itemtypeeditor._OLD_;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.ImageLoader;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.DecoratorPanel;

import java.util.Map;

/**
 * User: beat
 * Date: 16.08.2011
 * Time: 12:28:30
 */
public class ItemTypeView extends DecoratorPanel {
    public static final Index ITEM_POSITION = new Index(150, 150);
    private Context2d context2d;
    private ImageLoader<ItemType> imageLoader;
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
        imageLoader = new ImageLoader<ItemType>();
        imageLoader.addImageUrl(ImageHandler.getItemTypeSpriteMapUrl(itemType.getId()), itemType);
        imageLoader.startLoading(new ImageLoader.Listener<ItemType>() {
            @Override
            public void onLoaded(Map<ItemType, ImageElement> imageElements) {
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
        // TODO SyncItemArea syncItemArea = boundingBox.createSyntheticSyncItemArea(ITEM_POSITION, boundingBox.imageNumberToAngel(imageNr));

        // TODO Index imageOffset = boundingBox.getTopLeftFromImage(ITEM_POSITION);

        // TODO context2d.drawImage(imageLoader.getImage(itemType),
        // TODO        boundingBox.getImageWidth() * imageNr, // the x coordinate of the upper-left corner of the source rectangle
        // TODO         0, // the y coordinate of the upper-left corner of the source rectangle
        // TODO         boundingBox.getImageWidth(),// the width of the source rectangle
        // TODO         boundingBox.getImageHeight(),// sh the width of the source rectangle
        // TODO         imageOffset.getX(),// the x coordinate of the upper-left corner of the destination rectangle
        // TODO         imageOffset.getY(),// the y coordinate of the upper-left corner of the destination rectangle
        // TODO         boundingBox.getImageWidth(),// the width of the destination rectangle
        // TODO         boundingBox.getImageHeight()// the height of the destination rectangle
        // TODO );
        // Bounding box
        // TODO boundingBoxControl.draw(syncItemArea, context2d);
        muzzleFlashControl.draw(imageNr, context2d);
    }
}
