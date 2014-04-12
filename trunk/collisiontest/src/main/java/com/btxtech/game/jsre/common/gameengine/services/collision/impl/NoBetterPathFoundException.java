package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: 16.03.14
 * Time: 22:54
 */
public class NoBetterPathFoundException extends Exception {
    public NoBetterPathFoundException(Index startTile, Index endTile) {
        super("startTile: " + startTile + " endTile: " + endTile);
    }
}
