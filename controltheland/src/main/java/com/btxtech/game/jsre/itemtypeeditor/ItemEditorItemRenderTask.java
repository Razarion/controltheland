package com.btxtech.game.jsre.itemtypeeditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.renderer.AbstractRenderTask;
import com.btxtech.game.jsre.client.renderer.ItemTypeImageLoaderContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 12:11
 */
public class ItemEditorItemRenderTask extends AbstractRenderTask {
    private Context2d context2d;

    public ItemEditorItemRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, Rectangle tileViewRect) {
        for (SyncItem syncItem : ItemContainer.getInstance().getItemsInRectangleFast(viewRect)) {
            ItemTypeSpriteMap itemTypeSpriteMap = syncItem.getItemType().getItemTypeSpriteMap();
            ItemTypeSpriteMap.SyncObjectState syncObjectState = itemTypeSpriteMap.getSyncObjectState(syncItem);
            Index absoluteImagePosition = syncItem.getSyncItemArea().getTopLeftFromImagePosition();
            Index relativeImagePosition = new Index(absoluteImagePosition.getX() - viewRect.getX(), absoluteImagePosition.getY() - viewRect.getY());
            int angelIndex;
            int step;
            int frame;
            switch (syncObjectState) {
                case BUILD_UP:
                    angelIndex = 0;
                    step = itemTypeSpriteMap.getBuildupStep(syncItem);
                    frame = itemTypeSpriteMap.getBuildupAnimationFrame(timeStamp);
                    break;
                case RUN_TIME:
                    angelIndex = syncItem.getSyncItemArea().getAngelIndex();
                    step = 0;
                    frame = itemTypeSpriteMap.getRuntimeAnimationFrame(timeStamp);
                    break;
                case DEMOLITION:
                    angelIndex = syncItem.getSyncItemArea().getAngelIndex();
                    step = itemTypeSpriteMap.getDemolitionStep4ItemImage(syncItem);
                    if (step < 0) {
                        step = 0;
                        frame = itemTypeSpriteMap.getRuntimeAnimationFrame(timeStamp);
                        syncObjectState = ItemTypeSpriteMap.SyncObjectState.RUN_TIME;
                    }  else {
                        frame = itemTypeSpriteMap.getDemolitionAnimationFrame(step, timeStamp);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("ItemTypeSpriteMap.getItemTypeImageOffset() unknown SyncObjectState: " + syncObjectState);
            }

            if (ItemTypeEditorModel.getInstance().isProtagonist(syncItem) && ItemTypeEditorModel.getInstance().isImageOverridden(angelIndex, step, frame, syncObjectState)) {
                ImageElement imageElement = ItemTypeEditorModel.getInstance().getImageElement(angelIndex, step, frame, syncObjectState);
                context2d.drawImage(imageElement, relativeImagePosition.getX(), relativeImagePosition.getY());
            } else {
                ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(syncItem.getItemType());
                if (imageElement == null) {
                    continue;
                }
                Index offset = itemTypeSpriteMap.getItemTypeImageOffset(syncItem, timeStamp);
                context2d.drawImage(imageElement,
                        offset.getX(), // the x coordinate of the upper-left corner of the source rectangle
                        offset.getY(), // the y coordinate of the upper-left corner of the source rectangle
                        itemTypeSpriteMap.getImageWidth(),// the width of the source rectangle
                        itemTypeSpriteMap.getImageHeight(),// sh the width of the source rectangle
                        relativeImagePosition.getX(),// the x coordinate of the upper-left corner of the destination rectangle
                        relativeImagePosition.getY(),// the y coordinate of the upper-left corner of the destination rectangle
                        itemTypeSpriteMap.getImageWidth(),// the width of the destination rectangle
                        itemTypeSpriteMap.getImageHeight()// the height of the destination rectangle
                );
            }
        }

        ItemTypeImageLoaderContainer.getInstance().startLoad();
    }
}
