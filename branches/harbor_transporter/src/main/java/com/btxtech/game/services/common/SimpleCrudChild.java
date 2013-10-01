package com.btxtech.game.services.common;

import com.btxtech.game.services.user.UserService;

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
    public void init(UserService userService) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParent(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getParent() {
        return null;
    }
}
