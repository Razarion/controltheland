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

package com.btxtech.game.wicket.uiservices;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 20:35:01
 */
public abstract class DetachHashListProvider<T> implements IDataProvider<T> {
    private List<T> list;

    @Override
    public Iterator<? extends T> iterator(int first, int count) {
        return getList().subList(first, first + count).iterator();
    }

    @Override
    public int size() {
        return getList().size();
    }

    @Override
    public IModel<T> model(T object) {
        final int hash = object.hashCode();
        return new CompoundPropertyModel<T>(new IModel<T>() {
            private T object;

            @Override
            public T getObject() {
                if (object == null) {
                    object = getObject4Hash(hash);
                }
                return object;
            }

            @Override
            public void setObject(T object) {
                // Ignore
            }

            @Override
            public void detach() {
                object = null;
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
        }
        return list;
    }

    protected abstract List<T> createList();

    protected abstract T getObject4Hash(int hash);

}
