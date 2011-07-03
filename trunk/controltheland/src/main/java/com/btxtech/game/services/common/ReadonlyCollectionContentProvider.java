package com.btxtech.game.services.common;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 02.07.2011
 * Time: 20:04:06
 */
public class ReadonlyCollectionContentProvider<T extends CrudChild> implements ContentProvider<T> {
    private Collection<T> collection;

    public ReadonlyCollectionContentProvider(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    public Collection<T> readDbChildren() {
        return collection;
    }

    @Override
    public T readDbChild(Serializable id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T createDbChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteDbChild(T child) {
        throw new UnsupportedOperationException();
    }
}
