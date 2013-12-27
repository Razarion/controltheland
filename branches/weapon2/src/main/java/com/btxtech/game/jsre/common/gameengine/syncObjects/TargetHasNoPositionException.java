package com.btxtech.game.jsre.common.gameengine.syncObjects;

/**
 * User: beat
 * Date: 22.02.13
 * Time: 16:42
 */
public class TargetHasNoPositionException extends Exception {
    public TargetHasNoPositionException(SyncItem syncItem) {
        super("SyncItem has no position: " + syncItem);
    }
}
