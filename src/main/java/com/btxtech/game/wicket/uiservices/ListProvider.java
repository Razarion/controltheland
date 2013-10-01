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
public abstract class ListProvider<T> implements IDataProvider<T> {
    private List<T> list;
    private List<T> lastModifiedList;

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return getList().subList((int) first, (int) first + (int) count).iterator();
    }

    @Override
    public long size() {
        return getList().size();
    }

    @Override
    public IModel<T> model(T object) {
        return new CompoundPropertyModel<>(object);
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
