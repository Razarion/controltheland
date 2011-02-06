/*
 * Copyright (c) 2011.
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

package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.common.CrudChild;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 20:35:01
 */
public abstract class CrudTableListProvider<T extends CrudChild> implements IDataProvider<T> {
    private List<T> list;
    private List<T> lastModifiedList;

    @Override
    public Iterator<? extends T> iterator(int first, int count) {
        return getList().subList(first, first + count).iterator();
    }

    @Override
    public int size() {
        return getList().size();
    }

    @Override
    public IModel<T> model(final T object) {
        return new CompoundPropertyModel<T>(new IModel<T>() {
            private T modelObject = object;
            private Serializable serializable = object.getId();

            @Override
            public T getObject() {
                if (modelObject == null) {
                    for (T t : getList()) {
                        if (t.getId().equals(serializable)) {
                            modelObject = t;
                            return modelObject;
                        }
                    }
                    throw new IllegalStateException("CRUD child does not exist: " + serializable);
                }

                return modelObject;
            }

            @Override
            public void setObject(T object) {
                modelObject = object;
                serializable = object.getId();
            }

            @Override
            public void detach() {
                modelObject = null;
            }
        });
    }

    @Override
    public void detach() {
        list = null;
    }

    private List<T> getList() {
        if (list == null) {
            list = createList();
            lastModifiedList = list;
        }
        return list;
    }

    public List<T> getLastModifiedList() {
        if (lastModifiedList == null) {
            throw new NullPointerException();
        }

        return lastModifiedList;
    }

    abstract protected List<T> createList();
}
