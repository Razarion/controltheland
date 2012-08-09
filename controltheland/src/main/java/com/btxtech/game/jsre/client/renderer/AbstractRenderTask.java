package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.common.Rectangle;

/**
 * User: beat
 * Date: 29.07.12
 * Time: 11:40
 */
public abstract class AbstractRenderTask {
    public abstract void render(long timeStamp, Rectangle viewRect, Rectangle tileViewRect);
}
