package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 05.06.13
 * Time: 01:24
 */
public class NoSuchPropertyException extends Exception {
    public NoSuchPropertyException(PropertyServiceEnum propertyServiceEnum) {
        super("No such property: " + propertyServiceEnum);
    }
}
