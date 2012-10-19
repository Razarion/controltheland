package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

/**
 * User: beat
 * Date: 19.10.12
 * Time: 20:10
 */
public class ItemMouseOverHandler {
    private static ItemMouseOverHandler INSTANCE = new ItemMouseOverHandler();
    private SyncBaseItem mouseOver;

    public static ItemMouseOverHandler getInstance() {
        return INSTANCE;
    }

    private ItemMouseOverHandler() {
    }

    public SyncBaseItem getMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(SyncItem mouseOver) {
        if (mouseOver instanceof SyncBaseItem) {
            this.mouseOver = (SyncBaseItem) mouseOver;
        } else {
            this.mouseOver = null;
        }
    }
}
