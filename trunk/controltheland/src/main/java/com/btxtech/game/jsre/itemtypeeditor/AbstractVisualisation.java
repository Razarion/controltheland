package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Html5NotSupportedException;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 06.08.12
 * Time: 22:16
 */
public abstract class AbstractVisualisation implements ItemTypeEditorModel.UpdateListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    protected static final Index MIDDLE = new Index(WIDTH / 2, HEIGHT / 2);

    private Canvas canvas;
    private Context2d context2d;

    protected AbstractVisualisation() {
        canvas = Canvas.createIfSupported();
        if (canvas == null) {
            throw new Html5NotSupportedException("AbstractVisualisation");
        }
        canvas.setCoordinateSpaceWidth(WIDTH);
        canvas.setCoordinateSpaceHeight(HEIGHT);
        context2d = canvas.getContext2d();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void onModelUpdate() {
        final ItemType itemType = ItemTypeEditorModel.getInstance().getItemType();
        double angel = itemType.getBoundingBox().getAngels()[ItemTypeEditorModel.getInstance().getCurrentAngelIndex()];
        SyncItemArea syncItemArea = itemType.getBoundingBox().createSyntheticSyncItemArea(new Index(WIDTH / 2, HEIGHT / 2), angel);

        context2d.clearRect(0, 0, WIDTH, HEIGHT);
        if (ItemTypeEditorModel.getInstance().isImageOverridden(ItemTypeEditorModel.getInstance().getCurrentAngelIndex(), 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME)) {
            ImageElement imageElement = ItemTypeEditorModel.getInstance().getImageElement(ItemTypeEditorModel.getInstance().getCurrentAngelIndex(), 0, 0, ItemTypeSpriteMap.SyncObjectState.RUN_TIME);
            Index destination = MIDDLE.sub(imageElement.getWidth() / 2, imageElement.getHeight() / 2);
            context2d.drawImage(imageElement, destination.getX(), destination.getY());
        } else {
            if (ItemTypeEditorModel.getInstance().getSpriteMapImageElement() != null) {
                ItemTypeSpriteMap itemTypeSpriteMap = ItemTypeEditorModel.getInstance().getItemTypeSpriteMap();
                Index offset = ItemTypeEditorModel.getInstance().getItemTypeSpriteMap().getRuntimeImageOffset(ItemTypeEditorModel.getInstance().getCurrentAngelIndex(), 0);
                Index destination = MIDDLE.sub(itemTypeSpriteMap.getImageWidth() / 2, itemTypeSpriteMap.getImageHeight() / 2);
                context2d.drawImage(ItemTypeEditorModel.getInstance().getSpriteMapImageElement(),
                        offset.getX(), // the x coordinate of the upper-left corner of the source rectangle
                        offset.getY(), // the y coordinate of the upper-left corner of the source rectangle
                        itemTypeSpriteMap.getImageWidth(),// the width of the source rectangle
                        itemTypeSpriteMap.getImageHeight(),// sh the width of the source rectangle
                        destination.getX(),// the x coordinate of the upper-left corner of the destination rectangle
                        destination.getY(),// the y coordinate of the upper-left corner of the destination rectangle
                        itemTypeSpriteMap.getImageWidth(),// the width of the destination rectangle
                        itemTypeSpriteMap.getImageHeight()// the height of the destination rectangle
                );
            }
        }
        drawVisualisation(context2d, syncItemArea);
    }

    protected abstract void drawVisualisation(Context2d context2d, SyncItemArea syncItemArea);

}
