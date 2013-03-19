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
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ExtendedVelocityEngineUtils;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.ClientPerfmonDto;
import com.btxtech.game.services.mgmt.DbServerDebugEntry;
import com.btxtech.game.services.mgmt.DbViewDTO;
import com.btxtech.game.services.mgmt.MemoryUsageHistory;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.RequestHelper;
import com.btxtech.game.services.mgmt.StartupData;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.velocity.app.VelocityEngine;
import org.apache.wicket.Page;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat
 * Date: Aug 2, 2009
 * Time: 1:59:59 PM
 */
@Component("mgmtService")
public class MgmtServiceImpl implements MgmtService, SmartLifecycle {
    public static final String REPLY_EMAIL = "no-reply@razarion.com";
    private static final int MEMORY_SAMPLE_SIZE = 200;
    private static final int MEMORY_SAMPLE_DELAY_SECONDS = 120;
    private Date startTime = new Date();
    private JdbcTemplate readonlyJdbcTemplate;
    @Autowired
    private GenericItemConverter genericItemConverter;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestHelper requestHelper;
    private static Log log = LogFactory.getLog(MgmtServiceImpl.class);
    private StartupData startupData;
    private MemoryUsageContainer heapMemory = new MemoryUsageContainer(MEMORY_SAMPLE_SIZE);
    private MemoryUsageContainer noHeapMemory = new MemoryUsageContainer(MEMORY_SAMPLE_SIZE);
    private ScheduledThreadPoolExecutor memoryGrabberThreadPool;
    private boolean isRunning = false;
    private Map<String, ClientPerfmonDto> clientPerfmonEntries = new HashMap<>();

    public MgmtServiceImpl() {
        memoryGrabberThreadPool = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("Memory grabber " + getClass().getName() + " "));
        memoryGrabberThreadPool.scheduleAtFixedRate(new Runnable() {
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
        ArrayList<String> result = new ArrayList<>();
        for (SavedQuery savedQuery : list) {
            result.add(savedQuery.getQuery());
        }
        return result;
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    @Transactional
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

    @PreDestroy
    @Transactional
    public void shutdown() {
        try {
            if (memoryGrabberThreadPool != null) {
                memoryGrabberThreadPool.shutdownNow();
                memoryGrabberThreadPool = null;
            }
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @PostConstruct
    public void startup() {
        ExceptionHandler.init(applicationContext);
        HibernateUtil.openSession4InternalCall(sessionFactory);
        try {
            if (!Utils.isTestModeStatic()) {
                LogManager.getLogger("com.btxtech").setLevel(Level.INFO);
            }
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
    public MemoryUsageHistory getHeapMemoryUsageHistory() {
        return heapMemory.generateMemoryUsageHistory();
    }

    @Override
    public MemoryUsageHistory getNoHeapMemoryUsageHistory() {
        return noHeapMemory.generateMemoryUsageHistory();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
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
    public void saveClientPerfmonData(String sessionId, Map<PerfmonEnum, Integer> workTimes, int totalTime) {
        ClientPerfmonDto clientPerfmonDto = clientPerfmonEntries.get(sessionId);
        if (clientPerfmonDto != null) {
            clientPerfmonDto.setWorkTimes(workTimes, totalTime);
        } else {
            clientPerfmonDto = new ClientPerfmonDto(sessionId, workTimes, totalTime);
            clientPerfmonEntries.put(sessionId, clientPerfmonDto);
        }
    }

    @Override
    public List<ClientPerfmonDto> getClientPerfmonData() {
        List<ClientPerfmonDto> list = new ArrayList<>(clientPerfmonEntries.values());
        Collections.sort(list, new Comparator<ClientPerfmonDto>() {
            @Override
            public int compare(ClientPerfmonDto o1, ClientPerfmonDto o2) {
                return o2.getLastActivated().compareTo(o1.getLastActivated());
            }
        });
        return list;
    }

    @Override
    public ClientPerfmonDto getClientPerfmonData(String sessionId) {
        ClientPerfmonDto clientPerfmonDto = clientPerfmonEntries.get(sessionId);
        if (clientPerfmonDto == null) {
            throw new IllegalArgumentException("ClientPerfmonData for session id does not exist: " + sessionId);
        }
        return clientPerfmonDto;
    }

    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void sendEmail(final User user, final String subject, final String inString) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("User has no email address: " + user.getUsername());
        }
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(user.getEmail());
                message.setFrom(REPLY_EMAIL);
                message.setSubject(subject);
                Map<Object, Object> model = new HashMap<>();
                model.put("USER", user);
                String text = ExtendedVelocityEngineUtils.evaluate(velocityEngine, inString, model);
                message.setText(text, true);
            }
        };
        mailSender.send(preparator);
    }

    @Override
    @Transactional
    public void saveServerDebug(String category, HttpServletRequest request, Page cause, Throwable throwable) {
        saveServerDebug(category, request, cause != null ? cause.toString() : null, null, throwable);
    }

    @Override
    @Transactional
    public void saveServerDebug(String category, Throwable throwable) {
        HttpServletRequest request = null;
        try {
            request = requestHelper.getRequest();
        } catch (Exception ignore) {
            // Ignore
        }
        saveServerDebug(category, request, null, null, throwable);
    }

    private void saveServerDebug(String category, HttpServletRequest request, String causePage, String message, Throwable throwable) {
        try {
            DbServerDebugEntry dbServerDebugEntry = new DbServerDebugEntry(category, message, throwable.getMessage(), ExceptionUtils.getFullStackTrace(throwable));
            if (request != null) {
                dbServerDebugEntry.setSessionId(request.getSession().getId());
                dbServerDebugEntry.setUserAgent(request.getHeader("user-agent"));
                dbServerDebugEntry.setRemoteAddress(request.getRemoteAddr());
                dbServerDebugEntry.setReferer(request.getHeader("Referer"));
                dbServerDebugEntry.setRequestUri(request.getRequestURI());
                dbServerDebugEntry.setQueryString(request.getQueryString());
            }
            try {
                dbServerDebugEntry.setUserName(userService.getUserName());
            } catch (Exception ignore) {
                // Ignore
            }
            dbServerDebugEntry.setCausePage(causePage);
            dbServerDebugEntry.setThread(Thread.currentThread().getName());
            // @Transaction opens session
            sessionFactory.getCurrentSession().save(dbServerDebugEntry);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "Can not save dbServerDebugEntry. Category: " + category + " message: " + message + " throwable: " + throwable);
        }
    }
}

