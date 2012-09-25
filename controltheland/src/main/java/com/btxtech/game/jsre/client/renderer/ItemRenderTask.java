package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

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
    public void render(long timeStamp, Rectangle viewRect, Rectangle tileViewRect) {
        context2d.setFillStyle("#999999");
        Group ownSelection = SelectionHandler.getInstance().getOwnSelection();
        SyncItem targetSelection = SelectionHandler.getInstance().getSelectedTargetSyncItem();
        for (SyncItem syncItem : ItemContainer.getInstance().getItemsInRectangleFastIncludingDead(viewRect)) { // TODO clips off items if the middle is no longer in the view rect
            ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(syncItem.getItemType());
            if (imageElement == null) {
                continue;
            }
            SyncItemArea syncItemArea = syncItem.getSyncItemArea();
            ItemTypeSpriteMap itemTypeSpriteMap = syncItem.getItemType().getItemTypeSpriteMap();
            Index absoluteImagePosition = syncItemArea.getTopLeftFromImagePosition();
            Index relativeImagePosition = new Index(absoluteImagePosition.getX() - viewRect.getX(), absoluteImagePosition.getY() - viewRect.getY());

            if (syncItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                if (ownSelection != null && ownSelection.contains(syncBaseItem) || syncItem.equals(targetSelection)) {
                    showHealthBar(itemTypeSpriteMap, relativeImagePosition, syncBaseItem);
                }
                showMarker(itemTypeSpriteMap, relativeImagePosition, syncBaseItem);
                showProgressBar(itemTypeSpriteMap, relativeImagePosition, syncBaseItem);
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

        ItemTypeImageLoaderContainer.getInstance().startLoad();
    }

    private void showProgressBar(ItemTypeSpriteMap itemTypeSpriteMap, Index relativeImagePosition, SyncBaseItem syncBaseItem) {
        double progress = 0.0;
        if (syncBaseItem.hasSyncFactory()) {
            progress = syncBaseItem.getSyncFactory().getBuildupProgress();
        } else if (syncBaseItem.hasSyncBuilder()) {
            if (syncBaseItem.getSyncBuilder().getCurrentBuildup() != null) {
                progress = syncBaseItem.getSyncBuilder().getCurrentBuildup().getBuildup();
            }
        } else if (syncBaseItem.isUpgrading()) {
            progress = (double) syncBaseItem.getFullUpgradeProgress() / syncBaseItem.getUpgradeProgress();
        } else if (syncBaseItem.hasSyncLauncher()) {
            progress = syncBaseItem.getSyncLauncher().getBuildup();
        }
        if (progress > 0.0) {
            context2d.setLineWidth(3);
            context2d.setStrokeStyle("#5555FF");
            context2d.beginPath();
            context2d.moveTo(relativeImagePosition.getX(), relativeImagePosition.getY() + 2);
            context2d.lineTo(relativeImagePosition.getX() + itemTypeSpriteMap.getImageWidth() * progress, relativeImagePosition.getY() + 2);
            context2d.stroke();
        }
    }

    private void showMarker(ItemTypeSpriteMap itemTypeSpriteMap, Index relativeImagePosition, SyncBaseItem syncBaseItem) {
        context2d.setFillStyle(ClientBase.getInstance().getBaseHtmlColor(syncBaseItem.getBase()));
        context2d.fillRect(relativeImagePosition.getX(), relativeImagePosition.getY() + itemTypeSpriteMap.getImageHeight() - 7, 4, 4);
    }

    private void showHealthBar(ItemTypeSpriteMap itemTypeSpriteMap, Index relativeImagePosition, SyncBaseItem syncBaseItem) {
        context2d.setLineWidth(2);
        context2d.setStrokeStyle("#FF0000");
        context2d.beginPath();
        context2d.moveTo(relativeImagePosition.getX(), relativeImagePosition.getY() + itemTypeSpriteMap.getImageHeight() - 3);
        context2d.lineTo(relativeImagePosition.getX() + itemTypeSpriteMap.getImageWidth(), relativeImagePosition.getY() + itemTypeSpriteMap.getImageHeight() - 3);
        context2d.stroke();
        context2d.setStrokeStyle("#00FF00");
        context2d.beginPath();
        context2d.moveTo(relativeImagePosition.getX(), relativeImagePosition.getY() + itemTypeSpriteMap.getImageHeight() - 3);
        context2d.lineTo(relativeImagePosition.getX() + itemTypeSpriteMap.getImageWidth() * syncBaseItem.getNormalizedHealth(), relativeImagePosition.getY() + itemTypeSpriteMap.getImageHeight() - 3);
        context2d.stroke();
    }
}
