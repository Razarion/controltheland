package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.effects.MuzzleFlash;
import com.btxtech.game.jsre.client.effects.MuzzleFlashHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

/**
 * User: beat
 * Date: 31.07.12
 * Time: 13:45
 */
public class MuzzleFlashRenderTask extends AbstractRenderTask {
    private Context2d context2d;
    private MuzzleFlashImageHandler muzzleFlashImageHandler = new MuzzleFlashImageHandler();

    public MuzzleFlashRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Rectangle viewRect, Rectangle tileViewRect) {
        for (MuzzleFlash muzzleFlash : MuzzleFlashHandler.getInstance().getMuzzleFlashInRegion(timeStamp, viewRect)) {
            SyncBaseItem syncBaseItem = muzzleFlash.getSyncBaseItem();
            ImageElement imageElement = muzzleFlashImageHandler.getImage(syncBaseItem.getBaseItemType());
            if (imageElement == null) {
                continue;
            }
            Index relativeMuzzleStart = muzzleFlash.getAbsoluteMuzzleStart().sub(viewRect.getStart());
            context2d.save();
            context2d.translate(relativeMuzzleStart.getX(), relativeMuzzleStart.getY());
            context2d.rotate(-muzzleFlash.getMuzzleRotationAngel());
            context2d.drawImage(imageElement,
                    0, // Source X
                    0, // Source Y
                    muzzleFlash.getWidth(),// Source width
                    muzzleFlash.getHeight(),// Source height
                    -Math.round(muzzleFlash.getWidth() / 2.0), // Destination X
                    -muzzleFlash.getHeight(), // Destination X
                    muzzleFlash.getWidth(),// Destination width
                    muzzleFlash.getHeight()// Destination height
            );
            context2d.restore();
        }
        muzzleFlashImageHandler.startLoad();
    }
}
