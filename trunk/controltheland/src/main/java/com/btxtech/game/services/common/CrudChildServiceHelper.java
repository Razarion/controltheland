/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.common;

import com.btxtech.game.services.user.UserService;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;


/**
 * User: beat
 * Date: 25.07.2010
 * Time: 12:50:08
 */
public class CrudChildServiceHelper<T extends CrudChild> implements Serializable, ContentProvider<T> {
    private Collection<T> children;
    private Class<T> childClass;
    private CrudParent crudParent;

    public CrudChildServiceHelper(Collection<T> children, Class<T> childClass, CrudParent crudParent) {
        this.children = children;
        this.childClass = childClass;
        this.crudParent = crudParent;
    }

    @Override
    public Collection<T> readDbChildren() {
        return children;
    }

    @Override
    public Collection<T> readDbChildren(ContentSortList contentSortList) {
        return children;
    }

    @Override
    public T readDbChild(Serializable id) {
        for (T child : children) {
            if (child.getId().equals(id)) {
                return child;
            }
        }
        throw new NoSuchChildException(id, childClass);
    }

    @Override
    public void deleteDbChild(T child) {
        children.remove(child);
    }

    public void updateDbChildren(Collection<T> children) {
        this.children.clear();
        for (T child : children) {
            child.setParent(crudParent);
            this.children.add(child);
        }
    }

    public T createDbChild() {
        return createDbChild(childClass, null);
    }

    public T createDbChild(Class<? extends T> createClass) {
        return createDbChild(createClass, null);
    }

    public T createDbChild(UserService userService) {
        return createDbChild(childClass, userService);
    }

    public T createDbChild(Class<? extends T> createClass, UserService userService) {
        try {
            Constructor<? extends T> constructor = createClass.getConstructor();
            T t = constructor.newInstance();
            addChild(t, userService);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void initChild(T t) {
    }

    public void deleteAllChildren() {
        children.clear();
    }

    public void addChild(T t, UserService userService) {
        t.setParent(crudParent);
        t.init(userService);
        initChild(t);
        children.add(t);
    }

    public void copyTo(CrudChildServiceHelper<T> crudChildServiceHelper) {
        crudChildServiceHelper.children.clear();
        try {
            for (T child : children) {
                Class childClass = child.getClass();
                Constructor<? extends T> constructor = childClass.getConstructor(childClass);
                T t = constructor.newInstance(child);
                t.setParent(crudChildServiceHelper.crudParent);
                crudChildServiceHelper.children.add(t);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateDbChild(T t) {
        throw new UnsupportedOperationException();
    }
}
