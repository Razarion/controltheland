/*
 * Copyright (c) 2010.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * User: beat
 * Date: 24.07.2010
 * Time: 11:56:11
 */
@Deprecated
public class CrudServiceHelperHibernateImpl<T extends CrudChild> implements CrudServiceHelper<T> {
    private HibernateTemplate hibernateTemplate;
    private Class<T> childClass;
    private Log log = LogFactory.getLog(CrudServiceHelperHibernateImpl.class);

    public CrudServiceHelperHibernateImpl(HibernateTemplate hibernateTemplate, Class<T> childClass) {
        this.hibernateTemplate = hibernateTemplate;
        this.childClass = childClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<T> readDbChildren() {
        return hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(childClass);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                addAdditionalReadCriteria(criteria);
                return criteria.list();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public T readDbChild(Serializable id) {
        T t = hibernateTemplate.get(childClass, id);
        if (t == null) {
            throw new IllegalArgumentException("No child found for: " + id);
        }
        return t;
    }

    protected void addAdditionalReadCriteria(Criteria criteria) {
    }

    @Override
    public void deleteDbChild(T child) {
        hibernateTemplate.delete(child);
    }

    @Override
    public void updateDbChildren(Collection<T> children) {
        hibernateTemplate.saveOrUpdateAll(children);
    }

    @Override
    public void updateDbChild(T t) {
        hibernateTemplate.update(t);
    }

    @Override
    public void createDbChild() {
        createDbChild(childClass);
    }

    @Override
    public void createDbChild(Class<? extends T> createClass) {
        try {
            Constructor<? extends T> constructor = createClass.getConstructor();
            T t = constructor.newInstance();
            addChild(t);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void deleteAllChildren() {
        hibernateTemplate.deleteAll(readDbChildren());
    }

    protected void initChild(T t) {
    }

    @Override
    public void addChild(T t) {
        t.init();
        initChild(t);
        hibernateTemplate.save(t);
    }
}
