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

package com.btxtech.game.services.common;

import org.hibernate.criterion.Criterion;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 06.02.2011
 * Time: 15:08:50
 */
public interface CrudRootServiceHelper<T extends CrudChild> extends ContentProvider<T> {
    void init(Class<T> childClass);

    void init(Class<T> childClass, String orderColumn, boolean setOrderColumn, boolean orderAsc, String userColumn);

    Collection<T> readDbChildren();

    Collection<T> readDbChildren(ContentSortList contentSortList);

    T readDbChild(Serializable id);

    void deleteDbChild(T child);

    void updateDbChildren(Collection<T> children);

    void updateDbChild(T t);

    T createDbChild();

    T createDbChild(Class<? extends T> createClass);

    T copyDbChild(Serializable copyFromId);

    void deleteAllChildren();

    void putCriterion(Object key, Criterion criterion);

    void removeCriterion(Object key);
}
