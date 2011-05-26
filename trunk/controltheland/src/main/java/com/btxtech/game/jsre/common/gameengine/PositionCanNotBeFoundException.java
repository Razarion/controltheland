package com.btxtech.game.jsre.common.gameengine;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 25.05.2011
 * Time: 18:37:08
 */
public class PositionCanNotBeFoundException extends RuntimeException {
    public PositionCanNotBeFoundException(Rectangle target, SyncBaseItem itemToPlace) {
        super("Position can not be found. Target: " + target + " Item to place: " + itemToPlace);
    }
}
