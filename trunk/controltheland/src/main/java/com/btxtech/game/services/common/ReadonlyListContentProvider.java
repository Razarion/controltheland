package com.btxtech.game.services.common;

import com.btxtech.game.services.user.UserService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 25.07.2011
 * Time: 20:04:06
 */
public class ReadonlyListContentProvider<T extends CrudChild> implements ContentProvider<T> {
    private Map<Serializable, T> map;
    private List<T> list;

    public ReadonlyListContentProvider(List<T> list) {
        this.list = list;
        map = new HashMap<Serializable, T>();
        for (T t : list) {
            map.put(t.getId(), t);
        }
    }

    @Override
    public List<T> readDbChildren() {
        return list;
    }

    @Override
    public Collection<T> readDbChildren(ContentSortList contentSortList) {
        List<T> sortedList = new ArrayList<T>(list);
        if (contentSortList != null) {
            // ComparatorChain 
            final ContentSort contentSort = contentSortList.getContentSorts().get(0);
            final NestedNullSafeBeanComparator beanComparator = new NestedNullSafeBeanComparator(contentSort.getPropertyName(), false);

            Collections.sort(sortedList, new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    if (contentSort.isAsc()) {
                        return beanComparator.compare(o1, o2);
                    } else {
                        return beanComparator.compare(o2, o1);
                    }
                }
            });
        }
        return sortedList;
    }

    @Override
    public T readDbChild(Serializable id) {
        T t = map.get(id);
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
