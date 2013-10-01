package com.btxtech.game.services.common;

import java.io.Serializable;

/**
 * User: beat
 * Date: 02.04.2011
 * Time: 12:51:32
 */
public class NoSuchChildException extends RuntimeException {
    public NoSuchChildException(Serializable id, Class clazz) {
        super("No such child: " + id + " Class: " + clazz);
    }
}
