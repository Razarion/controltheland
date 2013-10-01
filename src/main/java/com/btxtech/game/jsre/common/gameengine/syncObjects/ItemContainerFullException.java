package com.btxtech.game.jsre.common.gameengine.syncObjects;

/**
 * User: beat
 * Date: 15.03.2012
 * Time: 15:12:49
 */
public class ItemContainerFullException extends Exception {
    public ItemContainerFullException(SyncItemContainer syncItemContainer, int count) {
        super("Item container is full (count " + count + ") " + syncItemContainer.getSyncBaseItem());
    }
}
