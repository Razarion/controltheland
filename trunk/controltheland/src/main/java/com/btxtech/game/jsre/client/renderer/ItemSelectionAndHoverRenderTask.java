package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.ItemMouseOverHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 12:11
 */
public class ItemSelectionAndHoverRenderTask extends AbstractRenderTask {
    private Context2d context2d;

    public ItemSelectionAndHoverRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, Rectangle tileViewRect) {
        context2d.setFillStyle("#999999");
        Group ownSelection = SelectionHandler.getInstance().getOwnSelection();
        SyncItem targetSelection = SelectionHandler.getInstance().getSelectedTargetSyncItem();
        for (SyncItem syncItem : itemsInView) {
            if (syncItem instanceof SyncBaseItem) {
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(syncItem.getItemType());
                if (imageElement == null) {
                    continue;
                }
                SyncItemArea syncItemArea = syncItem.getSyncItemArea();
                ItemTypeSpriteMap itemTypeSpriteMap = syncItem.getItemType().getItemTypeSpriteMap();
                Index absoluteImagePosition = syncItemArea.getTopLeftFromImagePosition();
                Index relativeImagePosition = new Index(absoluteImagePosition.getX() - viewRect.getX(), absoluteImagePosition.getY() - viewRect.getY());

                if (ownSelection != null && ownSelection.contains(syncBaseItem) || syncItem.equals(targetSelection)) {
                    showHealthBar(itemTypeSpriteMap, relativeImagePosition, syncBaseItem, 1.0);
                }
                showMarker(itemTypeSpriteMap, relativeImagePosition, syncBaseItem);
                showProgressBar(itemTypeSpriteMap, relativeImagePosition, syncBaseItem);
                SyncBaseItem mouseOverItem = ItemMouseOverHandler.getInstance().getMouseOver();
                if (syncBaseItem.equals(mouseOverItem)) {
                    if (!ClientBase.getInstance().isMyOwnProperty(mouseOverItem)) {
                        showNameAndGuild(mouseOverItem, viewRect, syncItemArea);
                    }
                    if ((ownSelection == null || !ownSelection.contains(mouseOverItem))) {
                        showHealthBar(itemTypeSpriteMap, relativeImagePosition, mouseOverItem, 0.5);
                    }
                }
            }
        }

        ItemTypeImageLoaderContainer.getInstance().startLoad();
    }

    private void showNameAndGuild(SyncBaseItem mouseOverItem, Rectangle viewRect, SyncItemArea syncItemArea) {
        context2d.setTextAlign(Context2d.TextAlign.CENTER);
        context2d.setFont("12px Arial");
        context2d.setFillStyle(ClientBase.getInstance().getBaseHtmlColor(mouseOverItem.getBase()));
        context2d.setShadowOffsetX(1);
        context2d.setShadowOffsetY(1);
        context2d.setShadowColor("black");
        int relativeX = syncItemArea.getPosition().getX() - viewRect.getX();
        int relativeY = syncItemArea.getPosition().getY() - viewRect.getY() - syncItemArea.getBoundingBox().getRadius();
        SimpleGuild simpleGuild = ClientBase.getInstance().getGuild(mouseOverItem.getBase());
        if (simpleGuild != null) {
            context2d.fillText(ClientBase.getInstance().getBaseName(mouseOverItem.getBase()), relativeX, relativeY - 12);
            context2d.fillText("[" + simpleGuild.getName() + "]", relativeX, relativeY);
        } else {
            context2d.fillText(ClientBase.getInstance().getBaseName(mouseOverItem.getBase()), relativeX, relativeY);
        }
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

    private void showHealthBar(ItemTypeSpriteMap itemTypeSpriteMap, Index relativeImagePosition, SyncBaseItem syncBaseItem, double alpha) {
        context2d.setGlobalAlpha(alpha);
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
        context2d.setGlobalAlpha(1.0);
    }
}
