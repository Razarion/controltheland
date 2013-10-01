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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: beat Date: 12.05.2010 Time: 20:35:01
 */
public abstract class DetachHashListProvider<T> implements IDataProvider<T> {
    private List<T> list;
    private Map<Integer, T> hashMap = new HashMap<>();

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return getList().subList((int)first, (int)first + (int)count).iterator();
    }

    @Override
    public long size() {
        return getList().size();
    }

    @Override
    public IModel<T> model(T object) {
        final int hash = object.hashCode();
        return new CompoundPropertyModel<>(new IModel<T>() {
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
        hashMap.clear();
    }

    public void forceReload() {
        detach();
        getList();
    }

    public List<T> getList() {
        if (list == null) {
            list = createList();
            for (T t : list) {
                hashMap.put(t.hashCode(), t);
            }
        }
        return list;
    }

    protected abstract List<T> createList();

    public void doSort(Comparator<T> comparator) {
        Collections.sort(list, comparator);
    }

    /**
     * Override in subclasses
     *
     * @param hash for retrieving the item
     * @return the item
     */
    protected T getObject4Hash(int hash) {
        getList();
        T t = hashMap.get(hash);
        if (t == null) {
            throw new IllegalArgumentException("No entry for " + hash);
        }
        return t;
    }
}
