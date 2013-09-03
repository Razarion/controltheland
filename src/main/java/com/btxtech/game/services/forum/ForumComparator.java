package com.btxtech.game.services.forum;

import java.util.Comparator;
import java.util.Date;

/**
 * User: beat
 * Date: 18.07.2011
 * Time: 16:46:45
 */
public abstract class ForumComparator<T> implements Comparator<T> {
    @Override
    public int compare(T o1, T o2) {
        Date d1 = getDate(o1);
        Date d2 = getDate(o2);
        if (d1 == null && d2 == null) {
            return 0;
        } else if (d1 == null) {
            return 1;
        } else if (d2 == null) {
            return -1;
        } else {
            return d2.compareTo(d1);
        }
    }

    protected abstract Date getDate(T t);
}
