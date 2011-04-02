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

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

/**
 * User: beat
 * Date: 06.02.2011
 * Time: 15:08:50
 */
@Component(value = "crudRootServiceHelper")
@Scope("prototype")
public class CrudRootServiceHelper<T extends CrudChild> {
    private HibernateTemplate hibernateTemplate;
    private Class<T> childClass;
    private String orderColumn;

    public void init(Class<T> childClass) {
        init(childClass, null);
    }

    public void init(Class<T> childClass, String orderColumn) {
        this.childClass = childClass;
        this.orderColumn = orderColumn;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Collection<T> readDbChildren() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(childClass);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                if (orderColumn != null) {
                    criteria.addOrder(Order.asc(orderColumn));
                }
                return criteria.list();
            }
        });
    }

    @Transactional(readOnly = true, noRollbackFor = NoSuchChildException.class)
    public T readDbChild(Serializable id) {
        T t = hibernateTemplate.get(childClass, id);
        if (t == null) {
            throw new NoSuchChildException(id);
        }
        return t;
    }

    @Transactional
    public void deleteDbChild(T child) {
        hibernateTemplate.delete(child);
    }

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

    @Transactional
    public void updateDbChild(T t) {
        hibernateTemplate.update(t);
    }

    @Transactional
    public T createDbChild() {
        return createDbChild(childClass);
    }

    @Transactional
    public T createDbChild(Class<? extends T> createClass) {
        try {
            Constructor<? extends T> constructor = createClass.getConstructor();
            T t = constructor.newInstance();
            addChild(t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteAllChildren() {
        hibernateTemplate.deleteAll(readDbChildren());
    }

    @Transactional
    public void addChild(T t) {
        t.init();
        if (orderColumn != null) {
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
