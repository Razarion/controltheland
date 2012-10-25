package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:40
 */
public abstract class AbstractRenderTask {
    public abstract void render(long timeStamp, Collection<SyncItem> itemsInView, Rectangle viewRect, Rectangle tileViewRect);
}
