package com.btxtech.game.jsre.common;

/**
 * User: beat
 * Date: 24.10.12
 * Time: 11:44
 */
public class ObjectHolder<T> {
    private T object;

    public ObjectHolder() {
    }

    public ObjectHolder(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public void clearObject() {
        object = null;
    }

    public boolean hasObject() {
        return object != null;
    }
}
