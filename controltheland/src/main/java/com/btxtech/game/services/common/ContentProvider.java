package com.btxtech.game.services.common;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 07.06.2011
 * Time: 15:44:08
 */
public interface ContentProvider<T> {
    Collection<T> readDbChildren();

    T readDbChild(Serializable id);

    T createDbChild();

     void deleteDbChild(T child);
}
