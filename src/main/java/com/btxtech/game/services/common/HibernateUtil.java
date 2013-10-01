package com.btxtech.game.services.common;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 16.06.2011
 * Time: 14:42:02
 */
public class HibernateUtil {
    public static Object deproxy(Object maybeProxy) throws ClassCastException {
        if (maybeProxy == null) {
            return null;
        }
        if (maybeProxy instanceof HibernateProxy) {
            return ((HibernateProxy) maybeProxy).getHibernateLazyInitializer().getImplementation();
        } else {
            return maybeProxy;
        }
    }

    public static <T> T deproxy(Object maybeProxy, Class<T> baseClass) throws ClassCastException {
        if (maybeProxy == null) {
            return null;
        }
        if (maybeProxy instanceof HibernateProxy) {
            return baseClass.cast(((HibernateProxy) maybeProxy).getHibernateLazyInitializer().getImplementation());
        } else {
            return baseClass.cast(maybeProxy);
        }
    }

    public static boolean hasOpenSession(SessionFactory sessionFactory) {
        return TransactionSynchronizationManager.getResource(sessionFactory) != null;
    }

    public static void openSession4InternalCall(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        SessionHolder sessionHolder = new SessionHolder(session);
        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
    }

    public static void closeSession4InternalCall(SessionFactory sessionFactory) {
        sessionFactory.getCurrentSession().close();
        TransactionSynchronizationManager.unbindResource(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadAll(SessionFactory sessionFactory, Class<T> theClass) {
        return sessionFactory.getCurrentSession().createCriteria(theClass).list();
    }

    public static void saveOrUpdateAll(SessionFactory sessionFactory, Collection entities) {
        for (Object entity : entities) {
            sessionFactory.getCurrentSession().saveOrUpdate(entity);
        }
    }

    public static void deleteAll(SessionFactory sessionFactory, Collection entities) {
        for (Object entity : entities) {
            sessionFactory.getCurrentSession().delete(entity);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(SessionFactory sessionFactory, Class<T> theClass, Serializable id) {
        return (T) sessionFactory.getCurrentSession().get(theClass, id);
    }
}
