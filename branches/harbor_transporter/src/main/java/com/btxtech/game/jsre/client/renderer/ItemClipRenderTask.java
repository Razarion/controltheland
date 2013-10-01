package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.effects.ItemEffect;
import com.btxtech.game.jsre.client.effects.ItemEffectHandler;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.Collection;

/**
 * User: beat
 * Date: 01.11.12
 * Time: 13:14
 */
public class ItemClipRenderTask extends AbstractClipRenderTask {
    private Context2d context2d;

    public ItemClipRenderTask(Context2d context2d) {
        this.context2d = context2d;
    }

    @Override
    public void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, Rectangle tileViewRect) {
        Collection<ItemEffect> itemEffects = ItemEffectHandler.getInstance().getClips(timeStamp, viewRect,itemsInView);
        for (ItemEffect itemEffect : itemEffects) {
             renderClip(context2d, itemEffect);
        }
    }
}
