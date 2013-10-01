package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.StartPointMode;
import com.btxtech.game.jsre.client.StartPointItemPlacer;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
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
public class StartPointItemPlacerRenderTask extends AbstractRenderTask {
    private Context2d context2d;

    public StartPointItemPlacerRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, final Rectangle tileViewRect) {
        if (!StartPointMode.getInstance().isActive()) {
            return;
        }
        StartPointItemPlacer startPointItemPlacer = StartPointMode.getInstance().getStartPointPlacer();

        // Draw circle
        context2d.setGlobalAlpha(0.5);
        context2d.beginPath();
        context2d.arc(startPointItemPlacer.getRelativeMiddlePos().getX(), startPointItemPlacer.getRelativeMiddlePos().getY(), startPointItemPlacer.getItemFreeRadius(), 0, 2 * Math.PI, false);
        if (startPointItemPlacer.isPositionValid()) {
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
        BaseItemType baseItemType = startPointItemPlacer.getBaseItemType();
        ItemTypeSpriteMap itemTypeSpriteMap = baseItemType.getItemTypeSpriteMap();
        Index offset = itemTypeSpriteMap.getCosmeticImageOffset();
        ImageElement imageElement = ItemTypeImageLoaderContainer.getInstance().getImage(baseItemType);
        if (imageElement != null) {
            context2d.setGlobalAlpha(0.5);
            context2d.drawImage(imageElement,
                    offset.getX(),
                    offset.getY(),
                    itemTypeSpriteMap.getImageWidth(),
                    itemTypeSpriteMap.getImageHeight(),
                    startPointItemPlacer.getRelativeMiddlePos().getX() - itemTypeSpriteMap.getImageWidth() / 2,
                    startPointItemPlacer.getRelativeMiddlePos().getY() - itemTypeSpriteMap.getImageHeight() / 2,
                    itemTypeSpriteMap.getImageWidth(),
                    itemTypeSpriteMap.getImageHeight());
            context2d.setGlobalAlpha(1.0);
        }


        // Draw Text
        String errorText = startPointItemPlacer.getErrorText();
        if (errorText != null) {
            context2d.setFont("20px Arial");
            context2d.setTextAlign(Context2d.TextAlign.CENTER);
            context2d.setShadowColor("#000000");
            context2d.setShadowOffsetX(2);
            context2d.setShadowOffsetY(2);
            context2d.setShadowBlur(2);
            context2d.setFillStyle("#FFFFFF");
            context2d.fillText(errorText, startPointItemPlacer.getRelativeMiddlePos().getX(), startPointItemPlacer.getRelativeMiddlePos().getY());
            context2d.setShadowColor(null);
            context2d.setShadowOffsetX(0);
            context2d.setShadowOffsetY(0);
            context2d.setShadowBlur(0);
        } else {
            context2d.setFont("20px Arial");
            context2d.setTextAlign(Context2d.TextAlign.CENTER);
            context2d.setShadowColor("#000000");
            context2d.setShadowOffsetX(2);
            context2d.setShadowOffsetY(2);
            context2d.setShadowBlur(2);
            context2d.setFillStyle("#FFFFFF");
            context2d.fillText(ClientI18nHelper.CONSTANTS.chooseYourStartPoint(), startPointItemPlacer.getRelativeMiddlePos().getX(), startPointItemPlacer.getRelativeMiddlePos().getY());
            context2d.setShadowColor(null);
            context2d.setShadowOffsetX(0);
            context2d.setShadowOffsetY(0);
            context2d.setShadowBlur(0);
        }
    }
}
