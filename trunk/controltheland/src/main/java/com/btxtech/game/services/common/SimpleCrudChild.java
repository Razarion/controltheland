package com.btxtech.game.services.common;

import java.io.Serializable;

/**
 * User: beat
 * Date: 03.07.2011
 * Time: 11:59:33
 */
public abstract class SimpleCrudChild<T> implements CrudChild<T> {
    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParent(T t) {
        throw new UnsupportedOperationException();
    }
}
