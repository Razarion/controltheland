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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * User: beat
 * Date: 25.07.2010
 * Time: 12:50:08
 */
public class CrudServiceHelperCollectionImpl<T extends CrudChild> implements CrudServiceHelper<T>, Serializable {
    private Collection<T> children;
    private Class<T> childClass;
    private CrudParent crudParent;
    private Log log = LogFactory.getLog(CrudServiceHelperCollectionImpl.class);

    public CrudServiceHelperCollectionImpl(Collection<T> children, Class<T> childClass, CrudParent crudParent) {
        this.children = children;
        this.childClass = childClass;
        this.crudParent = crudParent;
    }

    @Override
    public Collection<T> readDbChildren() {
        return children;
    }

    @Override
    public void deleteDbChild(T child) {
        children.remove(child);
    }

    @Override
    public void updateDbChildren(Collection<T> children) {
        throw new NotImplementedException();
    }

    @Override
    public void updateDbChild(T crudChild) {
        throw new NotImplementedException();
    }

    @Override
    public void createDbChild() {
        try {
            Constructor<T> constructor = childClass.getConstructor();
            T t = constructor.newInstance();
            t.setParent(crudParent);
            t.init();
            initChild(t);
            children.add(t);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    protected void initChild(T t) {
    }
}
