package com.btxtech.game.jsre.client.common;

/**
 * User: beat
 * Date: 30.09.13
 * Time: 13:50
 */
public interface IndexCallback {
    /**
     *
     * @param index  the index
     * @return true if iteration should continue
     */
    boolean onIndex(Index index);
}
