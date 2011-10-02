package com.btxtech.game.services.collision;

/**
 * User: beat
 * Date: 02.10.2011
 * Time: 16:38:22
 */
public class PathCanNotBeFoundException extends RuntimeException {
    public PathCanNotBeFoundException(String message) {
        super(message);
    }
}
