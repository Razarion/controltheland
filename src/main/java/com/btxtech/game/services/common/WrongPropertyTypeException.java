package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 05.06.13
 * Time: 01:43
 */
public class WrongPropertyTypeException extends Exception {
    public WrongPropertyTypeException(Class expectedType, PropertyServiceEnum propertyServiceEnum) {
        super("Wrong property type exception. Expected: " + expectedType + " Property: " + propertyServiceEnum);
    }
}
