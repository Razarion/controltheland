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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Collection;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: beat
 * Date: 06.02.2011
 * Time: 15:08:50
 */
@Component(value = "crudServiceHelperSpringTransaction")
@Scope("prototype")
public class CrudServiceHelperSpringTransactionImpl<T extends CrudChild> implements CrudServiceHelper<T> {
    private HibernateTemplate hibernateTemplate;
    private Class<T> childClass;

    public CrudServiceHelperSpringTransactionImpl(Class<T> childClass) {
        this.childClass = childClass;
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Collection<T> readDbChildren() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(childClass);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public T readDbChild(Serializable id) {
        T t = (T) hibernateTemplate.get(childClass, id);
        if (t == null) {
            throw new IllegalArgumentException("No child found for: " + id);
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
        hibernateTemplate.saveOrUpdateAll(children);
    }

    @Override
    @Transactional
    public void updateDbChild(T t) {
        hibernateTemplate.update(t);
    }

    @Override
    @Transactional
    public void createDbChild() {
        createDbChild(childClass);
    }

    @Override
    @Transactional
    public void createDbChild(Class<? extends T> createClass) {
        try {
            Constructor<? extends T> constructor = createClass.getConstructor();
            T t = constructor.newInstance();
            addChild(t);
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
    @Transactional
    public void addChild(T t) {
        t.init();
        hibernateTemplate.save(t);
    }
}
