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

package com.btxtech.game.services.common.impl;

import com.btxtech.game.services.common.ContentSortList;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.NoSuchChildException;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 06.02.2011
 * Time: 15:08:50
 */
@Component(value = "crudRootServiceHelper")
@Scope("prototype")
public class CrudRootServiceHelperImpl<T extends CrudChild> implements CrudRootServiceHelper<T> {
    private HibernateTemplate hibernateTemplate;
    private Class<T> childClass;
    private String orderColumn;
    private boolean setOrderColumn;
    private boolean orderAsc;
    private String userField;
    private Map<Object, Criterion> criterionMap = new HashMap<Object, Criterion>();
    @Autowired
    private UserService userService;

    @Override
    public void init(Class<T> childClass) {
        init(childClass, null, false, false, null);
    }

    @Override
    public void init(Class<T> childClass, String orderColumn, boolean setOrderColumn, boolean orderAsc, String userField) {
        this.childClass = childClass;
        this.orderColumn = orderColumn;
        this.setOrderColumn = setOrderColumn;
        this.orderAsc = orderAsc;
        this.userField = userField;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Collection<T> readDbChildren(final ContentSortList contentSortList) {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(childClass);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                for (Criterion criterion : criterionMap.values()) {
                    criteria.add(criterion);
                }
                if (contentSortList != null) {
                    for (Order order : contentSortList.generateHibernateOrders()) {
                        criteria.addOrder(order);
                    }
                } else if (orderColumn != null) {
                    if (orderAsc) {
                        criteria.addOrder(Order.asc(orderColumn));
                    } else {
                        criteria.addOrder(Order.desc(orderColumn));
                    }
                }
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Collection<T> readDbChildren() {
        return readDbChildren(null);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = NoSuchChildException.class)
    public T readDbChild(Serializable id) {
        T t = hibernateTemplate.get(childClass, id);
        if (t == null) {
            throw new NoSuchChildException("Class: " + childClass + " id: " + id);
        }
        return t;
    }

    @Override
    @Transactional
    public void deleteDbChild(T child) {
        hibernateTemplate.delete(child);
    }

    @Override
    @Transactional
    public void updateDbChildren(Collection<T> children) {
        if (orderColumn != null) {
            int index = 0;
            for (T child : children) {
                try {
                    Field field = childClass.getDeclaredField(orderColumn);
                    field.setAccessible(true);
                    field.set(child, index);
                    field.setAccessible(false);
                    index++;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        hibernateTemplate.saveOrUpdateAll(children);
    }

    @Override
    @Transactional
    public void updateDbChild(T t) {
        hibernateTemplate.update(t);
    }

    @Override
    @Transactional
    public T createDbChild() {
        return createDbChild(childClass);
    }

    @Override
    @Transactional
    public T createDbChild(UserService userService) {
        return createDbChild();
    }

    @Override
    @Transactional
    public T createDbChild(Class<? extends T> createClass) {
        try {
            Constructor<? extends T> constructor = createClass.getConstructor();
            T t = constructor.newInstance();
            t.init(userService);
            if (userField != null) {
                setUser(userService, userField, childClass, t);
            }
            addChild(t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setUser(UserService userService, String userField, Class childClass, Object child) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(childClass, userField);
        field.setAccessible(true);
        User user = userService.getUser();
        if (user == null) {
            throw new IllegalStateException("No current user");
        }
        field.set(child, user);
        field.setAccessible(false);
    }

    private static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        Class tmpClass = clazz;
        while (tmpClass != null) {
            for (Field field : tmpClass.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            tmpClass = tmpClass.getSuperclass();
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found on class " + clazz);
    }

    @Override
    @Transactional
    public T copyDbChild(Serializable copyFromId) {
        T copyFrom = readDbChild(copyFromId);
        Class<? extends CrudChild> copyFromClass = copyFrom.getClass();
        try {
            Constructor<? extends CrudChild> constructor = copyFromClass.getConstructor(copyFromClass);
            T t = (T) constructor.newInstance(copyFrom);
            addChild(t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    @Transactional
    public void deleteAllChildren() {
        hibernateTemplate.deleteAll(readDbChildren());
    }

    @Override
    public void putCriterion(Object key, Criterion criterion) {
        criterionMap.put(key, criterion);
    }

    @Override
    public void removeCriterion(Object key) {
        criterionMap.remove(key);
    }

    @Transactional
    private void addChild(T t) {
        if (orderColumn != null && setOrderColumn) {
            int nextFreeIndex = hibernateTemplate.execute(new HibernateCallback<Integer>() {
                @Override
                public Integer doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                    Criteria criteria = session.createCriteria(childClass);
                    criteria.setProjection(Projections.max(orderColumn));
                    Number number = (Number) criteria.list().get(0);
                    if (number != null) {
                        return number.intValue() + 1;
                    } else {
                        return 0;
                    }
                }
            });
            try {
                Field field = childClass.getDeclaredField(orderColumn);
                field.setAccessible(true);
                field.set(t, nextFreeIndex);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        hibernateTemplate.save(t);
    }
}
