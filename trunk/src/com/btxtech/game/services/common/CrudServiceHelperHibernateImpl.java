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

import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
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
                return criteria.list();
            }
        });
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
        try {
            Constructor<T> constructor = childClass.getConstructor();
            T t = constructor.newInstance();
            t.init();
            hibernateTemplate.save(t);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
