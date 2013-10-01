package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.cockpit.CockpitMode;
import com.btxtech.game.jsre.client.cockpit.item.ToBeBuildPlacer;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:46
 */
public class ToBeBuildPlacerRenderTask extends AbstractRenderTask {
    private Context2d context2d;

    public ToBeBuildPlacerRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        CanvasElement canvas = context2d.getCanvas();
        context2d.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (!CockpitMode.getInstance().hasToBeBuildPlacer()) {
            return;
        }
        ToBeBuildPlacer toBeBuildPlacer = CockpitMode.getInstance().getToBeBuildPlacer();
        BaseItemType baseItemType = toBeBuildPlacer.getItemTypeToBuilt();
        ItemTypeSpriteMap itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();

        // Draw Item
        Index offset = itemTypeSpriteMap.getCosmeticImageOffset();
        ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(baseItemType);
        if (imageElement != null) {
            context2d.setGlobalAlpha(0.5);
            context2d.drawImage(imageElement,
                    offset.getX(),
                    offset.getY(),
                    itemTypeSpriteMap.getImageWidth(),
                    itemTypeSpriteMap.getImageHeight(),
                    toBeBuildPlacer.getRelativeMiddlePos().getX() - itemTypeSpriteMap.getImageWidth() / 2,
                    toBeBuildPlacer.getRelativeMiddlePos().getY() - itemTypeSpriteMap.getImageHeight() / 2,
                    itemTypeSpriteMap.getImageWidth(),
                    itemTypeSpriteMap.getImageHeight());
            context2d.setGlobalAlpha(1.0);
        }

        // Draw Cross
        if (!toBeBuildPlacer.isValidPosition()) {
            context2d.setLineWidth(10);
            context2d.setStrokeStyle("#FF0000");
            context2d.beginPath();
            context2d.moveTo(toBeBuildPlacer.getRelativeMiddlePos().getX() - itemTypeSpriteMap.getImageWidth() / 2, toBeBuildPlacer.getRelativeMiddlePos().getY() - itemTypeSpriteMap.getImageHeight() / 2);
            context2d.lineTo(toBeBuildPlacer.getRelativeMiddlePos().getX() + itemTypeSpriteMap.getImageWidth() / 2, toBeBuildPlacer.getRelativeMiddlePos().getY() + itemTypeSpriteMap.getImageHeight() / 2);
            context2d.moveTo(toBeBuildPlacer.getRelativeMiddlePos().getX() - itemTypeSpriteMap.getImageWidth() / 2, toBeBuildPlacer.getRelativeMiddlePos().getY() + itemTypeSpriteMap.getImageHeight() / 2);
            context2d.lineTo(toBeBuildPlacer.getRelativeMiddlePos().getX() + itemTypeSpriteMap.getImageWidth() / 2, toBeBuildPlacer.getRelativeMiddlePos().getY() - itemTypeSpriteMap.getImageHeight() / 2);
            context2d.stroke();
        }

        // Draw Text
        String errorText = toBeBuildPlacer.getErrorText();
        if (errorText != null) {
            context2d.setFont("20px Arial");
            context2d.setTextAlign(Context2d.TextAlign.CENTER);
            context2d.setShadowColor("#000000");
            context2d.setShadowOffsetX(2);
            context2d.setShadowOffsetY(2);
            context2d.setShadowBlur(2);
            context2d.setFillStyle("#FFFFFF");
            context2d.fillText(errorText, toBeBuildPlacer.getRelativeMiddlePos().getX(), toBeBuildPlacer.getRelativeMiddlePos().getY());
            context2d.setShadowColor(null);
            context2d.setShadowOffsetX(0);
            context2d.setShadowOffsetY(0);
            context2d.setShadowBlur(0);
        }
    }
}
