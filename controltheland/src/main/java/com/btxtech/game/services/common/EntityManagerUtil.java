package com.btxtech.game.services.common;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * User: beat
 * Date: 15.04.14
 * Time: 21:22
 */
public class EntityManagerUtil {

    public static boolean hasEntityManager(EntityManagerFactory entityManagerFactory) {
        return TransactionSynchronizationManager.getResource(entityManagerFactory) != null;
    }

    public static void createEntityManager4InternalCall(EntityManagerFactory entityManagerFactory) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
    }

    public static void closeEntityManager4InternalCall(EntityManagerFactory entityManagerFactory) {
        EntityManagerHolder entityManagerHolder = (EntityManagerHolder) TransactionSynchronizationManager.unbindResource(entityManagerFactory);
        EntityManagerFactoryUtils.closeEntityManager(entityManagerHolder.getEntityManager());
    }


}
