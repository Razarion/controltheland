package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.effects.Explosion;
import com.btxtech.game.jsre.client.effects.ExplosionHandler;
import com.btxtech.game.jsre.common.ImageLoader;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItemArea;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

import java.util.Map;

/**
 * User: beat
 * Date: 31.07.12
 * Time: 19:43
 */
public class ExplosionRenderTask extends AbstractRenderTask {
    private Context2d context2d;
    private ImageElement imageElement;
    private ImageLoader<Integer> imageLoader;

    public ExplosionRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Rectangle viewRect, Rectangle tileViewRect) {
        if (imageElement == null) {
            loadImage();
            return;
        }
        for (Explosion explosion : ExplosionHandler.getInstance().getExplosions(timeStamp, viewRect)) {
            // Draw Item
            if (explosion.isItemVisible()) {
                SyncItem syncItem = explosion.getSyncItem();
                ImageElement itemImageElement = ItemTypeImageLoaderContainer.getInstance().getImage(explosion.getSyncItem().getItemType());
                if (itemImageElement != null) {
                    SyncItemArea syncItemArea = syncItem.getSyncItemArea();
                    ItemTypeSpriteMap itemTypeSpriteMap = syncItem.getItemType().getItemTypeSpriteMap();
                    Index absoluteImagePosition = syncItemArea.getTopLeftFromImagePosition();
                    Index relativeImagePosition = new Index(absoluteImagePosition.getX() - viewRect.getX(), absoluteImagePosition.getY() - viewRect.getY());
                    Index offset = itemTypeSpriteMap.getItemTypeImageOffset(syncItem, timeStamp);
                    context2d.drawImage(itemImageElement,
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
            // Draw Explosion
            context2d.setGlobalAlpha(explosion.getAlpha());
            int relativeX = explosion.getAbsoluteImageStart().getX() - viewRect.getX();
            int relativeY = explosion.getAbsoluteImageStart().getY() - viewRect.getY();
            context2d.drawImage(imageElement,
                    0, 0, // Source pos
                    80, 80, // Source size
                    relativeX, relativeY,// Canvas pos
                    explosion.getFrameWidth(), explosion.getFrameHeight() // Canvas size
            );
            context2d.setGlobalAlpha(1.0);
        }
    }

    private void loadImage() {
        if (imageLoader != null) {
            return;
        }
        imageLoader = new ImageLoader<Integer>();
        imageLoader.addImageUrl(ImageHandler.getExplosion(), 1);
        imageLoader.startLoading(new ImageLoader.Listener<Integer>() {
            @Override
            public void onLoaded(Map<Integer, ImageElement> imageElements) {
                imageElement = imageElements.get(1);
                imageLoader = null;
            }
        });
    }
}
