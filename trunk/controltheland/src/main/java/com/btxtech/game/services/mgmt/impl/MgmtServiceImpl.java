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

package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.DbViewDTO;
import com.btxtech.game.services.mgmt.MemoryUsageHistory;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.StartupData;
import com.btxtech.game.services.resource.ResourceService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:59:59 PM
 */
@Component("mgmtService")
public class MgmtServiceImpl implements MgmtService, ApplicationListener {
    public static final String LOG_DIR_NAME = "logs";
    public static final File LOG_DIR;
    private static final int MEMORY_SAMPLE_SIZE = 1440;
    private static final int MEMORY_SAMPLE_DELAY_SECONDS = 60;
    private Date startTime = new Date();
    private JdbcTemplate readonlyJdbcTemplate;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ServerServices services;
    @Autowired
    private ActionService actionService;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private BotService botService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private GenericItemConverter genericItemConverter;
    @Autowired
    private SessionFactory sessionFactory;
    private static Log log = LogFactory.getLog(MgmtServiceImpl.class);
    private boolean testMode;
    private boolean noGameEngine;
    private StartupData startupData;
    private MemoryUsageContainer heapMemory = new MemoryUsageContainer(MEMORY_SAMPLE_SIZE);
    private MemoryUsageContainer noHeapMemory = new MemoryUsageContainer(MEMORY_SAMPLE_SIZE);
    private ScheduledFuture memoryGrabber;

    static {
        File tmpLogDir = null;
        try {
            String userHome = System.getProperty("user.home");
            tmpLogDir = new File(userHome, LOG_DIR_NAME);
        } catch (Throwable t) {
            log.error("", t);
        }
        LOG_DIR = tmpLogDir;
    }

    public MgmtServiceImpl() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("Memory grabber "));
        memoryGrabber = scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    Date date = new Date();
                    MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
                    heapMemory.addSample(date,
                            bytesToMega(memoryUsage.getInit()),
                            bytesToMega(memoryUsage.getUsed()),
                            bytesToMega(memoryUsage.getCommitted()),
                            bytesToMega(memoryUsage.getMax()));
                    memoryUsage = memoryMXBean.getNonHeapMemoryUsage();
                    noHeapMemory.addSample(date,
                            bytesToMega(memoryUsage.getInit()),
                            bytesToMega(memoryUsage.getUsed()),
                            bytesToMega(memoryUsage.getCommitted()),
                            bytesToMega(memoryUsage.getMax()));
                } catch (Throwable t) {
                    log.error("Memory grabber", t);
                }
            }
        }, 0, MEMORY_SAMPLE_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    private long bytesToMega(long bytes) {
        return (long) (bytes / 1000000.0);
    }


    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Resource(name = "readonlyDataSource")
    @Autowired
    public void setSessionFactory(DataSource dataSource) {
        readonlyJdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public DbViewDTO queryDb(String sql) {
        final DbViewDTO dbViewDTO = new DbViewDTO();
        readonlyJdbcTemplate.setMaxRows(1000);
        readonlyJdbcTemplate.setQueryTimeout(30000);
        readonlyJdbcTemplate.query(sql, new RowMapper<Void>() {

            @Override
            public Void mapRow(ResultSet resultSet, int i) throws SQLException {
                dbViewDTO.newRow();
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();
                for (int column = 0; column < columnCount; column++) {
                    if (i == 0) {
                        dbViewDTO.addHeader(resultSetMetaData.getColumnName(column + 1));
                    }
                    dbViewDTO.addDataCell(resultSet.getString(column + 1));
                }
                return null;
            }
        });
        return dbViewDTO;

    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveQuery(final String query) {
        SavedQuery savedQuery = new SavedQuery();
        savedQuery.setQuery(query);
        sessionFactory.getCurrentSession().saveOrUpdate(savedQuery);
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public List<String> getSavedQueris() {
        @SuppressWarnings("unchecked")
        List<SavedQuery> list = HibernateUtil.loadAll(sessionFactory, SavedQuery.class);
        ArrayList<String> result = new ArrayList<String>();
        for (SavedQuery savedQuery : list) {
            result.add(savedQuery.getQuery());
        }
        return result;
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void removeSavedQuery(final String query) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SavedQuery.class);
        criteria.add(Restrictions.like("query", query));
        List list = criteria.list();
        if (list.size() != 1) {
            throw new IllegalStateException("Only one entry expected for: " + query + ". Entries found: " + list.size());
        }
        sessionFactory.getCurrentSession().delete(list.get(0));
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public List<File> getLogFiles() {
        return Arrays.asList(LOG_DIR.listFiles());
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
    public void backup() {
        long time = System.currentTimeMillis();
        BackupEntry backupEntry = genericItemConverter.generateBackupEntry();
        // Save to db
        sessionFactory.getCurrentSession().save(backupEntry);
        log.info("Time used for backup: " + (System.currentTimeMillis() - time) + "ms. Items: " + backupEntry.getItemCount() + " Bases: " + backupEntry.getBaseCount() + " UserStates: " + backupEntry.getUserStateCount());
        genericItemConverter.clear();
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @SuppressWarnings("unchecked")
    public List<BackupSummary> getBackupSummary() {
        Criteria criteriaEntries = sessionFactory.getCurrentSession().createCriteria(BackupEntry.class);
        criteriaEntries.createCriteria("items", "itemAlias", CriteriaSpecification.LEFT_JOIN);
        criteriaEntries.createCriteria("userStates", "userStateAlias", CriteriaSpecification.LEFT_JOIN);
        ProjectionList entryProjectionList = Projections.projectionList();
        entryProjectionList.add(Projections.groupProperty("timeStamp"));
        entryProjectionList.add(Projections.countDistinct("itemAlias.id"));
        entryProjectionList.add(Projections.countDistinct("itemAlias.base"));
        entryProjectionList.add(Projections.countDistinct("userStateAlias.id"));
        criteriaEntries.setProjection(entryProjectionList);
        criteriaEntries.addOrder(Order.desc("timeStamp"));

        ArrayList<BackupSummary> result = new ArrayList<BackupSummary>();
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
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BackupEntry.class);
        criteria.add(Restrictions.eq("timeStamp", date));
        List<BackupEntry> list = criteria.list();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No entry for " + date);
        }
        BackupEntry backupEntry = list.get(0);
        genericItemConverter.restoreBackup(backupEntry);

        log.info("Restored to: " + date);
        log.info("Time used for restore: " + (System.currentTimeMillis() - time) + "ms. Items: " + backupEntry.getItemCount() + " Bases: " + backupEntry.getBaseCount() + " UserStates: " + backupEntry.getUserStateCount());
        genericItemConverter.clear();
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
    @SuppressWarnings("unchecked")
    public void deleteBackupEntry(final Date date) throws NoSuchItemTypeException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BackupEntry.class);
        criteria.add(Restrictions.eq("timeStamp", date));
        List<BackupEntry> list = criteria.list();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No entry for " + date);
        }
        BackupEntry backupEntry = list.get(0);
        sessionFactory.getCurrentSession().delete(backupEntry);
        log.info("Backup entry deleted: " + date);
    }

    public String getLogFile(String name) {
        try {
            File logFile = new File(LOG_DIR, name);
            StringBuffer fileData = new StringBuffer(1000);
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            char[] buf = new char[1024];
            int numRead;
            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }
            reader.close();
            return fileData.toString();
        } catch (IOException e) {
            log.error("", e);
            return "Unable reading logfile: " + e.toString();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (noGameEngine) {
            return;
        }
        if (applicationEvent instanceof ContextRefreshedEvent &&
                applicationEvent.getSource() instanceof AbstractApplicationContext &&
                ((AbstractApplicationContext) applicationEvent.getSource()).getParent() == null) {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            try {
                userGuidanceService.init2();
                List<BackupSummary> backupSummaries = getBackupSummary();
                if (!backupSummaries.isEmpty()) {
                    try {
                        restore(backupSummaries.get(0).getDate());
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
                resourceService.activate();
                botService.activate();
            } catch (Throwable t) {
                log.error("", t);
            } finally {
                HibernateUtil.closeSession4InternalCall(sessionFactory);
            }
        }
    }

    @PreDestroy
    @Transactional
    public void shutdown() {
        try {
            HibernateUtil.openSession4InternalCall(sessionFactory);
            backup();
            if (memoryGrabber != null) {
                memoryGrabber.cancel(false);
                memoryGrabber = null;
            }
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @PostConstruct
    public void startup() {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            testMode = Utils.isTestModeStatic();
            if (!testMode) {
                LogManager.getLogger("com.btxtech").setLevel(Level.INFO);
            }
            noGameEngine = System.getProperty(Utils.TEST_MODE_NO_GAME_ENGINE) != null && Boolean.parseBoolean(System.getProperty(Utils.TEST_MODE_NO_GAME_ENGINE));
            startupData = readStartupData();
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            HibernateUtil.closeSession4InternalCall(sessionFactory);
        }
    }

    @Override
    public StartupData getStartupData() {
        return startupData;
    }

    @Override
    public StartupData readStartupData() {
        @SuppressWarnings("unchecked")
        List<StartupData> startups = HibernateUtil.loadAll(sessionFactory, StartupData.class);
        if (startups.isEmpty()) {
            log.info("Startup data does not exist. Create default.");
            StartupData startupData = new StartupData();
            startupData.setRegisterDialogDelay(2 * 60);
            saveStartupData(startupData);
            return startupData;
        } else {
            if (startups.size() > 1) {
                log.error("More than one startup data detected. Get first");
            }
            return startups.get(0);
        }
    }

    @Override
    @Transactional
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveStartupData(StartupData startupData) {
        this.startupData = startupData;
        sessionFactory.getCurrentSession().saveOrUpdate(startupData);
    }

    @Override
    public boolean isTestMode() {
        return testMode;
    }

    @Override
    public boolean isNoGameEngine() {
        return noGameEngine;
    }

    @Override
    public MemoryUsageHistory getHeapMemoryUsageHistory() {
        return heapMemory.generateMemoryUsageHistory();
    }

    @Override
    public MemoryUsageHistory getNoHeapMemoryUsageHistory() {
        return noHeapMemory.generateMemoryUsageHistory();
    }
}

