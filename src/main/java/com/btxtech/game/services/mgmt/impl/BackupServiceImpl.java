package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.SecurityRoles;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * User: beat
 * Date: 08.08.13
 * Time: 01:24
 */
@Component("backupService")
public class BackupServiceImpl implements BackupService, SmartLifecycle {
    @Autowired
    private GenericItemConverter genericItemConverter;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PlanetSystemService planetSystemService;
    private static Log log = LogFactory.getLog(MgmtServiceImpl.class);
    private boolean isRunning = false;

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
    public void backup() {
        long time = System.currentTimeMillis();
        DbBackupEntry dbBackupEntry = genericItemConverter.generateBackupEntry();
        // Save to db
        sessionFactory.getCurrentSession().save(dbBackupEntry);
        log.info("Time used for backup: " + (System.currentTimeMillis() - time) + "ms. Items: " + dbBackupEntry.getItemCount() + " Bases: " + dbBackupEntry.getBaseCount() + " UserStates: " + dbBackupEntry.getUserStateCount());
        genericItemConverter.clear();
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @SuppressWarnings("unchecked")
    public List<BackupSummary> getBackupSummary() {
        Criteria criteriaEntries = sessionFactory.getCurrentSession().createCriteria(DbBackupEntry.class);
        criteriaEntries.createCriteria("items", "itemAlias", JoinType.LEFT_OUTER_JOIN);
        criteriaEntries.createCriteria("userStates", "userStateAlias", JoinType.LEFT_OUTER_JOIN);
        ProjectionList entryProjectionList = Projections.projectionList();
        entryProjectionList.add(Projections.groupProperty("timeStamp"));
        entryProjectionList.add(Projections.countDistinct("itemAlias.id"));
        entryProjectionList.add(Projections.countDistinct("itemAlias.base"));
        entryProjectionList.add(Projections.countDistinct("userStateAlias.id"));
        criteriaEntries.setProjection(entryProjectionList);
        criteriaEntries.addOrder(Order.desc("timeStamp"));

        ArrayList<BackupSummary> result = new ArrayList<>();
        for (Object[] objects : (Collection<Object[]>) criteriaEntries.list()) {
            Date date = new Date(((Timestamp) objects[0]).getTime());
            result.add(new BackupSummary(date, (Long) objects[1], (Long) objects[2], (Long) objects[3]));
        }
        return result;
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @SuppressWarnings("unchecked")
    public void restore(final Date date) throws NoSuchItemTypeException {
        long time = System.currentTimeMillis();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBackupEntry.class);
        criteria.add(Restrictions.eq("timeStamp", date));
        List<DbBackupEntry> list = criteria.list();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No entry for " + date);
        }
        DbBackupEntry dbBackupEntry = list.get(0);
        genericItemConverter.restoreBackup(dbBackupEntry);

        log.info("Restored to: " + date);
        log.info("Time used for restore: " + (System.currentTimeMillis() - time) + "ms. Items: " + dbBackupEntry.getItemCount() + " Bases: " + dbBackupEntry.getBaseCount() + " UserStates: " + dbBackupEntry.getUserStateCount());
        genericItemConverter.clear();
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
    @SuppressWarnings("unchecked")
    public void deleteBackupEntry(final Date date) throws NoSuchItemTypeException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DbBackupEntry.class);
        criteria.add(Restrictions.eq("timeStamp", date));
        List<DbBackupEntry> list = criteria.list();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No entry for " + date);
        }
        DbBackupEntry dbBackupEntry = list.get(0);
        sessionFactory.getCurrentSession().delete(dbBackupEntry);
        log.info("Backup entry deleted: " + date);
    }

    @Override
    @Transactional
    public void stop(Runnable callback) {
        // Stop can have already an session open
        try {
            if (HibernateUtil.hasOpenSession(sessionFactory)) {
                backup();
            } else {
                HibernateUtil.openSession4InternalCall(sessionFactory);
                try {
                    backup();
                } finally {
                    HibernateUtil.closeSession4InternalCall(sessionFactory);
                }
            }
        } catch (Throwable t) {
            log.error("", t);
        }
        isRunning = false;
        callback.run();
    }

    @Override
    public void start() {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            planetSystemService.activate();
            List<BackupSummary> backupSummaries = getBackupSummary();
            if (!backupSummaries.isEmpty()) {
                try {
                    restore(backupSummaries.get(0).getDate());
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            isRunning = true;
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public void stop() {
        // Is not called because it is an SmartLifecycle implementation
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}
