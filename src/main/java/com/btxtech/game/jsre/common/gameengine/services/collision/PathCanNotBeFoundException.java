package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Index;

/**
 * User: beat
 * Date: 02.10.2011
 * Time: 16:38:22
 */
public class PathCanNotBeFoundException extends RuntimeException {
    public PathCanNotBeFoundException(String message, Index start, Index destination) {
        super(message + " start: " + start + " destination: " + destination);
    }
}
