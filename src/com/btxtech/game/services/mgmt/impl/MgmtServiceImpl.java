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
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.DbViewDTO;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.StartupData;
import com.btxtech.game.wicket.pages.mgmt.Startup;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:59:59 PM
 */
@Component("mgmtService")
public class MgmtServiceImpl implements MgmtService, ApplicationListener {
    public static final String LOG_DIR_NAME = "logs";
    public static final File LOG_DIR;
    public static final String TEST_MODE_PROPERTY = "testmode";
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
    private static Log log = LogFactory.getLog(MgmtServiceImpl.class);
    private HibernateTemplate hibernateTemplate;
    private Boolean testMode;
    private StartupData startupData;

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Resource(name = "readonlyDataSource")
    @Autowired()
    public void setSessionFactory(DataSource dataSource) {
        readonlyJdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public DbViewDTO queryDb(String sql) {
        final DbViewDTO dbViewDTO = new DbViewDTO();
        readonlyJdbcTemplate.setMaxRows(1000);
        readonlyJdbcTemplate.setQueryTimeout(30000);
        readonlyJdbcTemplate.query(sql, new RowMapper() {

            @Override
            public Object mapRow(ResultSet resultSet, int i) throws SQLException {
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
    public void saveQuery(final String query) {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                SavedQuery savedQuery = new SavedQuery();
                savedQuery.setQuery(query);
                session.saveOrUpdate(savedQuery);
                return null;
            }
        });
    }

    @Override
    public List<String> getSavedQueris() {
        List<SavedQuery> list = (List<SavedQuery>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(SavedQuery.class);
                return criteria.list();
            }
        });
        ArrayList<String> result = new ArrayList<String>();
        for (SavedQuery savedQuery : list) {
            result.add(savedQuery.getQuery());
        }
        return result;
    }

    @Override
    public void removeSavedQuery(final String query) {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(SavedQuery.class);
                criteria.add(Restrictions.like("query", query));
                List list = criteria.list();
                if (list.size() != 1) {
                    throw new IllegalStateException("Only one entry expected for: " + query + ". Entries found: " + list.size());
                }
                session.delete(list.get(0));
                return null;
            }
        });
    }

    @Override
    public List<File> getLogFiles() {
        return Arrays.asList(LOG_DIR.listFiles());
    }

    @Override
    public void backup() {
        GenericItemConverter converter = new GenericItemConverter(baseService, itemService, services, null, actionService);
        final BackupEntry backupEntry = converter.generateBackupEntry();
        // Save to db
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                session.saveOrUpdate(backupEntry);
                return null;
            }
        });
    }

    @Override
    public List<BackupSummary> getBackupSummary() {
        List<Object[]> list = (List<Object[]>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteriaEntries = session.createCriteria(BackupEntry.class);
                criteriaEntries.createCriteria("items", "genericItems");
                ProjectionList entryProjectionList = Projections.projectionList();
                entryProjectionList.add(Projections.groupProperty("timeStamp"));
                entryProjectionList.add(Projections.count("items"));
                entryProjectionList.add(Projections.countDistinct("genericItems.base"));
                criteriaEntries.setProjection(entryProjectionList);
                criteriaEntries.addOrder(Order.desc("timeStamp"));
                return criteriaEntries.list();
            }
        });

        ArrayList<BackupSummary> result = new ArrayList<BackupSummary>();
        for (Object[] objects : list) {
            Date date = new Date(((Timestamp) objects[0]).getTime());
            result.add(new BackupSummary(date, (Integer) objects[1], (Integer) objects[2]));
        }
        return result;
    }

    @Override
    public void restore(final Date date) throws NoSuchItemTypeException {
        List<BackupEntry> list = (List<BackupEntry>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(BackupEntry.class);
                criteria.add(Restrictions.eq("timeStamp", date));
                return criteria.list();
            }
        });
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No entry for " + date);
        }
        GenericItemConverter converter = new GenericItemConverter(baseService, itemService, services, serverEnergyService, actionService);
        converter.restorBackup(list.get(0));
        log.info("Restored to: " + date);
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

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try {
            if (applicationEvent instanceof ContextRefreshedEvent &&
                    applicationEvent.getSource() instanceof AbstractApplicationContext &&
                    ((AbstractApplicationContext) applicationEvent.getSource()).getParent() == null) {
                List<BackupSummary> backupSummaries = getBackupSummary();
                if (!backupSummaries.isEmpty()) {
                    restore(backupSummaries.get(0).getDate());
                } else {
                    actionService.setupAllMoneyStacks();
                }
            }
        } catch (Throwable t) {
            log.error("", t);
        }

    }

    @PreDestroy
    public void shutdown() {
        try {
            backup();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @PostConstruct
    public void startup() {
        try {
            getStartupData();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public StartupData getStartupData() {
        if (startupData == null) {
            List<StartupData> startups = hibernateTemplate.loadAll(StartupData.class);
            if (startups.isEmpty()) {
                log.info("Startup data does not exist. Create default.");
                startupData = new StartupData();
                startupData.setRegisterDialogDelay(2 * 60);
                startupData.setStartMoney(1000);
                startupData.setTutorialTimeout(3 * 60);
                startupData.setUserActionCollectionTime(5 * 60);
                saveStartupData(startupData);
            } else if (startups.size() > 1) {
                log.error("More than one startup data detected.");
            } else {
                startupData = startups.get(0);
            }
        }
        return startupData;
    }

    @Override
    public void saveStartupData(StartupData startupData) {
        this.startupData = startupData;
        hibernateTemplate.saveOrUpdate(startupData);
    }

    public boolean isTestMode() {
        if (testMode == null) {
            testMode = System.getProperty(TEST_MODE_PROPERTY) != null && Boolean.parseBoolean(System.getProperty(TEST_MODE_PROPERTY));
        }
        return testMode;
    }
}

