package com.btxtech.game.jsre.client.control.task;

import com.btxtech.game.jsre.client.ClientClipHandler;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;

/**
 * User: beat
 * Date: 14.11.12
 * Time: 15:58
 */
public class ImageSpriteMapPreloaderStartupTask extends AbstractStartupTask{
    public ImageSpriteMapPreloaderStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        ClientClipHandler.getInstance().preloadImageSpriteMaps();
    }
}
