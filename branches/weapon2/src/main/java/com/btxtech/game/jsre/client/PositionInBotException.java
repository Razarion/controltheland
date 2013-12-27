package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: 02.05.13
 * Time: 01:46
 */
public class PositionInBotException extends Exception {
    /**
     * Used by GWT
     */
    PositionInBotException() {
    }

    public PositionInBotException(Index startPoint) {
        super("Position is in bot realm: " + startPoint);
    }
}
