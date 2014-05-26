package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 12:11
 */
public class ItemRenderTask extends AbstractRenderTask {
    private Context2d context2d;

    public ItemRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, Rectangle tileViewRect) {
        context2d.setFillStyle("#999999");
        Collection<SyncItem> noneResourceItems = new ArrayList<>(itemsInView.size());
        for (SyncItem syncItem : itemsInView) {
            ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(syncItem.getItemType());
            if (imageElement == null) {
                continue;
            }
            SyncItemArea syncItemArea = syncItem.getSyncItemArea();
            if (Game.isDebug()) {
                Index relativeMiddle = syncItemArea.getPosition().sub(viewRect.getStart());
                context2d.setGlobalAlpha(0.5);
                context2d.setFillStyle("rgb(200, 0, 0)");
                context2d.beginPath();
                context2d.arc(relativeMiddle.getX(), relativeMiddle.getY(), syncItemArea.getBoundingBox().getRadius(), 0, 2 * Math.PI, false);
                context2d.fill();
                context2d.setGlobalAlpha(1.0);
            }
            // Render the resources below the items
            if(syncItem instanceof SyncResourceItem) {
                renderSyncItem(timeStamp, viewRect, syncItem, imageElement, syncItemArea);
            }else{
                noneResourceItems.add(syncItem);
            }
        }
        for (SyncItem syncItem : noneResourceItems) {
            SyncItemArea syncItemArea = syncItem.getSyncItemArea();
            ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(syncItem.getItemType());
            renderSyncItem(timeStamp, viewRect, syncItem, imageElement, syncItemArea);
        }

        ItemTypeImageLoaderContainer.getInstance().startLoad();
    }

    private void renderSyncItem(long timeStamp, Rectangle viewRect, SyncItem syncItem, ImageElement imageElement, SyncItemArea syncItemArea) {
        ItemTypeSpriteMap itemTypeSpriteMap = syncItem.getItemType().getItemTypeSpriteMap();
        Index absoluteImagePosition = syncItemArea.getTopLeftFromImagePosition();
        Index relativeImagePosition = new Index(absoluteImagePosition.getX() - viewRect.getX(), absoluteImagePosition.getY() - viewRect.getY());

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
