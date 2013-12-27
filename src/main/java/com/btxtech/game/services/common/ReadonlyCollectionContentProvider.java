package com.btxtech.game.services.common;

import com.btxtech.game.services.user.UserService;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 02.07.2011
 * Time: 20:04:06
 */
public class ReadonlyCollectionContentProvider<T extends CrudChild> implements ContentProvider<T> {
    private Map<Serializable, T> collection;

    public ReadonlyCollectionContentProvider(Collection<T> collection) {
        this.collection = new HashMap<Serializable, T>();
        for (T t : collection) {
            this.collection.put(t.getId(), t);
        }
    }

    @Override
    public Collection<T> readDbChildren() {
        return collection.values();
    }

    @Override
    public Collection<T> readDbChildren(ContentSortList contentSortList) {
        return readDbChildren();
    }

    @Override
    public T readDbChild(Serializable id) {
        T t = collection.get(id);
        if (t == null) {
            throw new NoSuchChildException(id, null);
        }
        return t;
    }

    @Override
    public T createDbChild(UserService userService) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteDbChild(T child) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDbChild(T t) {
        throw new UnsupportedOperationException();
    }
}
