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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.NoSuchElementException;


/**
 * User: beat
 * Date: 25.07.2010
 * Time: 12:50:08
 */
public class CrudChildServiceHelper<T extends CrudChild> implements Serializable {
    private Collection<T> children;
    private Class<T> childClass;
    private CrudParent crudParent;

    public CrudChildServiceHelper(Collection<T> children, Class<T> childClass, CrudParent crudParent) {
        this.children = children;
        this.childClass = childClass;
        this.crudParent = crudParent;
    }

    public Collection<T> readDbChildren() {
        return children;
    }

    public T readDbChild(Serializable id) {
        for (T child : children) {
            if (child.getId().equals(id)) {
                return child;
            }
        }
        throw new NoSuchElementException("Id: " + id);
    }

    public void deleteDbChild(T child) {
        children.remove(child);
    }

    public void updateDbChildren(Collection<T> children) {
        this.children.clear();
        for (T child : this.children) {
            child.setParent(crudParent);
            children.add(child);
        }
    }

    public T createDbChild() {
        return createDbChild(childClass);
    }

    public T createDbChild(Class<? extends T> createClass) {
        try {
            Constructor<? extends T> constructor = createClass.getConstructor();
            T t = constructor.newInstance();
            addChild(t);
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

    public void addChild(T t) {
        t.setParent(crudParent);
        t.init();
        initChild(t);
        children.add(t);
    }
}
