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
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 12.05.2010
 * Time: 20:35:01
 */
public abstract class CrudTableListProvider<T extends CrudChild> implements IDataProvider<T> {
    private List<T> list;

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return getList().subList((int) first, (int) first + (int) count).iterator();
    }

    @Override
    public long size() {
        return getList().size();
    }

    @Override
    public IModel<T> model(final T object) {
        return new CompoundPropertyModel<T>(new SerializableModel(object));
    }

    @Override
    public void detach() {
        list = null;
    }

    public List<T> getList() {
        if (list == null) {
            list = sortList(createList());
        }
        return list;
    }

    public void refresh() {
        list = sortList(createList());
    }

    abstract protected List<T> createList();

    abstract protected List<T> sortList(List<T> list);

    private class SerializableModel implements IModel<T> {
        private T modelObject;
        private Serializable serializable;

        private SerializableModel(T modelObject) {
            this.modelObject = modelObject;
            serializable = modelObject.getId();
        }

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
    }

}
