package com.btxtech.game.services.common;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * User: beat
 * Date: 13.03.2011
 * Time: 23:35:06
 */
@Component(value = "ruServiceHelper")
@Scope("prototype")
public class RuServiceHelper<T> {
    private HibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional
    public void updateDbEntity(T entity) {
        hibernateTemplate.update(entity);
    }

    @Transactional
    public T readDbChild(Serializable id, Class<T> clazz) {
        return hibernateTemplate.get(clazz, id);
    }


}
