package com.btxtech.game.services.common;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void updateDbEntity(T entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public T readDbChild(Serializable id, Class<T> clazz) {
        return (T) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Transactional
    public <C extends CrudChild> void removeChildAndUpdate(T entity, CrudChildServiceHelper<C> crudChildServiceHelper, final C childToRemove) {
        // Delete and save in the same transaction
        crudChildServiceHelper.deleteDbChild(childToRemove);
        sessionFactory.getCurrentSession().update(entity);
        sessionFactory.getCurrentSession().flush();
    }

    @Transactional
    public <C extends CrudChild> void removeChildAndUpdate(T entity, CrudListChildServiceHelper<C> crudListChildServiceHelper, C childToRemove) {
        // Delete and save in the same transaction
        crudListChildServiceHelper.deleteDbChild(childToRemove);
        sessionFactory.getCurrentSession().update(entity);
        sessionFactory.getCurrentSession().flush();
    }
}
