package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryItemPlacer;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class InventoryItemPlacerRenderTask extends AbstractRenderTask {
    private Context2d context2d;

    public InventoryItemPlacerRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        if (!CockpitMode.getInstance().hasInventoryItemPlacer()) {
            return;
        }
        InventoryItemPlacer inventoryItemPlacer = CockpitMode.getInstance().getInventoryItemPlacer();

        // Draw circle
        context2d.setGlobalAlpha(0.5);
        context2d.beginPath();
        context2d.arc(inventoryItemPlacer.getRelativeMiddlePos().getX(), inventoryItemPlacer.getRelativeMiddlePos().getY(), inventoryItemPlacer.getItemFreeRadius(), 0, 2 * Math.PI, false);
        if (inventoryItemPlacer.isPositionValid()) {
            context2d.setFillStyle("rgb(0, 200, 0)");
            context2d.fill();
        } else {
            context2d.setFillStyle("rgb(200, 0, 0)");
            context2d.fill();
        }
        context2d.setLineWidth(2);
        context2d.setStrokeStyle("black");
        context2d.stroke();
        context2d.setGlobalAlpha(1.0);

        // Draw Items
        BaseItemType baseItemType = inventoryItemPlacer.getBaseItemType();
        ItemTypeSpriteMap itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        Index offset = itemTypeSpriteMap.getCosmeticImageOffset();
        ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(baseItemType);
        if (imageElement != null) {
            for (Index index : inventoryItemPlacer.getRelativePositionsToPlace()) {
                context2d.drawImage(imageElement,
                        offset.getX(),
                        offset.getY(),
                        itemTypeSpriteMap.getImageWidth(),
                        itemTypeSpriteMap.getImageHeight(),
                        index.getX() - itemTypeSpriteMap.getImageWidth() / 2,
                        index.getY() - itemTypeSpriteMap.getImageHeight() / 2,
                        itemTypeSpriteMap.getImageWidth(),
                        itemTypeSpriteMap.getImageHeight());
            }
        }


        // Draw Text
        String errorText = inventoryItemPlacer.getErrorText();
        if (errorText != null) {
            context2d.setFont("20px Arial");
            context2d.setTextAlign(Context2d.TextAlign.CENTER);
            context2d.setShadowColor("#000000");
            context2d.setShadowOffsetX(2);
            context2d.setShadowOffsetY(2);
            context2d.setShadowBlur(2);
            context2d.setFillStyle("#FFFFFF");
            context2d.fillText(errorText, inventoryItemPlacer.getRelativeMiddlePos().getX(), inventoryItemPlacer.getRelativeMiddlePos().getY());
            context2d.setShadowColor(null);
            context2d.setShadowOffsetX(0);
            context2d.setShadowOffsetY(0);
            context2d.setShadowBlur(0);
        }
    }
}
