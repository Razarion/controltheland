package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 24.10.12
 * Time: 11:44
 */
public class ObjectHolder<T> {
    private T object;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
