package com.btxtech.game.services.statistics.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ContentSortList;
import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.statistics.CurrentStatisticEntry;
import com.btxtech.game.services.statistics.DbStatisticsEntry;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 18.09.2011
 * Time: 21:45:43
 */
public class TestStatisticsServiceImpl extends AbstractServiceTest {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    private StatisticsServiceImpl getImpl() throws Exception {
        if (AopUtils.isJdkDynamicProxy(statisticsService)) {
            return (StatisticsServiceImpl) ((Advised) statisticsService).getTargetSource().getTarget();
        } else {
            return (StatisticsServiceImpl) statisticsService;
        }
    }

    @Test
    @DirtiesContext
    public void simpleSave() throws Exception {
        User user = new User();
        user.registerUser("xxx", "", "");
        getHibernateTemplate().save(user);

        SimpleBase unregBase = new SimpleBase(1);
        UserState userState1 = new UserState();
        SimpleBase regBase = new SimpleBase(2);
        UserState userState2 = new UserState();
        userState2.setUser(user);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.getUserState(unregBase)).andReturn(userState1).anyTimes();
        EasyMock.expect(baseService.getUserState(regBase)).andReturn(userState2).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        Date date = new Date();
        statisticsService.onMoneyEarned(unregBase, 0.5);
        statisticsService.onMoneyEarned(regBase, 0.8);
        Assert.assertTrue(getHibernateTemplate().loadAll(DbStatisticsEntry.class).isEmpty());
        getImpl().moveCacheToDb();

        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0.8, dbStatisticsEntry.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.dayStart(date), dbStatisticsEntry.getDate());
    }

    @Test
    @DirtiesContext
    public void sumUpSimpleSave() throws Exception {
        User user = new User();
        user.registerUser("xxx", "", "");
        getHibernateTemplate().save(user);

        SimpleBase regBase = new SimpleBase(2);
        UserState userState2 = new UserState();
        userState2.setUser(user);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.getUserState(regBase)).andReturn(userState2).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        Date date = new Date();
        statisticsService.onMoneyEarned(regBase, 0.8);
        statisticsService.onMoneyEarned(regBase, 1.2);
        statisticsService.onMoneyEarned(regBase, 2.0);
        Assert.assertTrue(getHibernateTemplate().loadAll(DbStatisticsEntry.class).isEmpty());
        getImpl().moveCacheToDb();

        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(4.0, dbStatisticsEntry.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.dayStart(date), dbStatisticsEntry.getDate());
    }

    @Test
    @DirtiesContext
    public void threeUsers() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);

        User user2 = new User();
        user2.registerUser("yyy", "", "");
        getHibernateTemplate().save(user2);

        User user3 = new User();
        user3.registerUser("zzz", "", "");
        getHibernateTemplate().save(user3);

        SimpleBase base1 = new SimpleBase(1);
        UserState userState1 = new UserState();
        userState1.setUser(user1);

        SimpleBase base2 = new SimpleBase(2);
        UserState userState2 = new UserState();
        userState2.setUser(user2);

        SimpleBase base3 = new SimpleBase(3);
        UserState userState3 = new UserState();
        userState3.setUser(user3);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.getUserState(base1)).andReturn(userState1).anyTimes();
        EasyMock.expect(baseService.getUserState(base2)).andReturn(userState2).anyTimes();
        EasyMock.expect(baseService.getUserState(base3)).andReturn(userState3).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        Date date = new Date();
        statisticsService.onMoneyEarned(base1, 0.8);
        statisticsService.onMoneyEarned(base2, 0.3);
        statisticsService.onMoneyEarned(base1, 1.2);
        statisticsService.onMoneyEarned(base3, 2.1);
        statisticsService.onMoneyEarned(base2, 10.1);
        Assert.assertTrue(getHibernateTemplate().loadAll(DbStatisticsEntry.class).isEmpty());
        getImpl().moveCacheToDb();

        Assert.assertEquals(3, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry1 = null;
        DbStatisticsEntry dbStatisticsEntry2 = null;
        DbStatisticsEntry dbStatisticsEntry3 = null;

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        for (DbStatisticsEntry dbStatisticsEntry : getHibernateTemplate().loadAll(DbStatisticsEntry.class)) {
            if (dbStatisticsEntry.getUser().equals(user1)) {
                dbStatisticsEntry1 = dbStatisticsEntry;
            } else if (dbStatisticsEntry.getUser().equals(user2)) {
                dbStatisticsEntry2 = dbStatisticsEntry;
            } else if (dbStatisticsEntry.getUser().equals(user3)) {
                dbStatisticsEntry3 = dbStatisticsEntry;
            } else {
                Assert.fail("Unexpected user: " + dbStatisticsEntry.getUser());
            }
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertNotNull(dbStatisticsEntry1);
        Assert.assertNotNull(dbStatisticsEntry2);
        Assert.assertNotNull(dbStatisticsEntry3);

        Assert.assertEquals(2.0, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.dayStart(date), dbStatisticsEntry1.getDate());
        Assert.assertEquals(10.4, dbStatisticsEntry2.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.dayStart(date), dbStatisticsEntry2.getDate());
        Assert.assertEquals(2.1, dbStatisticsEntry3.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.dayStart(date), dbStatisticsEntry3.getDate());
    }

    @Test
    @DirtiesContext
    public void killedStructureBot() throws Exception {
        configureMinimalGame();

        User user = new User();
        user.registerUser("xxx", "", "");
        getHibernateTemplate().save(user);

        SimpleBase botBase = new SimpleBase(1);
        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(user);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(botBase)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(1, 1, 1), botBase);
        statisticsService.onItemKilled(target, actorBase);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(1, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void killedUnitsBot() throws Exception {
        configureMinimalGame();

        User user = new User();
        user.registerUser("xxx", "", "");
        getHibernateTemplate().save(user);

        SimpleBase botBase = new SimpleBase(1);
        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(user);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(botBase)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        SyncBaseItem target = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(500, 500), new Id(1, 1, 1), botBase);
        statisticsService.onItemKilled(target, actorBase);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(1, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void killedStructurePlayer() throws Exception {
        configureMinimalGame();

        User actorUser = new User();
        actorUser.registerUser("xxx", "", "");
        getHibernateTemplate().save(actorUser);
        User targetUser = new User();
        targetUser.registerUser("yyy", "", "");
        getHibernateTemplate().save(targetUser);

        SimpleBase targetBase = new SimpleBase(1);
        UserState targetUserState = new UserState();
        targetUserState.setUser(targetUser);
        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(actorUser);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(targetBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.expect(baseService.getUserState(targetBase)).andReturn(targetUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        SyncBaseItem target = createSyncBaseItem(TEST_SIMPLE_BUILDING_ID, new Index(500, 500), new Id(1, 1, 1), targetBase);
        statisticsService.onItemKilled(target, actorBase);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(1, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void killedUnitsPlayer() throws Exception {
        configureMinimalGame();

        User actorUser = new User();
        actorUser.registerUser("xxx", "", "");
        getHibernateTemplate().save(actorUser);
        User targetUser = new User();
        targetUser.registerUser("yyy", "", "");
        getHibernateTemplate().save(targetUser);

        SimpleBase targetBase = new SimpleBase(1);
        UserState targetUserState = new UserState();
        targetUserState.setUser(targetUser);
        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(actorUser);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(targetBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.expect(baseService.getUserState(targetBase)).andReturn(targetUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        SyncBaseItem target = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(500, 500), new Id(1, 1, 1), targetBase);
        statisticsService.onItemKilled(target, actorBase);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(1, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void builtStructures() throws Exception {
        configureMinimalGame();

        User actorUser = new User();
        actorUser.registerUser("xxx", "", "");
        getHibernateTemplate().save(actorUser);

        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(actorUser);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        SyncBaseItem createdItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(500, 500), new Id(1, 1, 1), actorBase);
        statisticsService.onItemCreated(createdItem);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(1, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void baseLostPlayer() throws Exception {
        configureMinimalGame();

        User actorUser = new User();
        actorUser.registerUser("xxx", "", "");
        getHibernateTemplate().save(actorUser);
        User targetUser = new User();
        targetUser.registerUser("yyy", "", "");
        getHibernateTemplate().save(targetUser);

        SimpleBase targetBase = new SimpleBase(1);
        UserState targetUserState = new UserState();
        targetUserState.setUser(targetUser);
        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(actorUser);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(targetBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.expect(baseService.getUserState(targetBase)).andReturn(targetUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        statisticsService.onBaseKilled(targetBase, actorBase);

        getImpl().moveCacheToDb();
        Assert.assertEquals(2, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Iterator<DbStatisticsEntry> iterator = getHibernateTemplate().loadAll(DbStatisticsEntry.class).iterator();
        DbStatisticsEntry entry1 = iterator.next();
        DbStatisticsEntry entry2 = iterator.next();
        DbStatisticsEntry actorEntry;
        DbStatisticsEntry targetEntry;
        if (entry1.getUser().getUsername().equals(actorUser.getUsername())) {
            actorEntry = entry1;
            targetEntry = entry2;
        } else {
            actorEntry = entry2;
            targetEntry = entry1;
        }
        // Actor
        Assert.assertEquals(0, actorEntry.getBasesDestroyedBot());
        Assert.assertEquals(1, actorEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, actorEntry.getOwnBaseLost());
        Assert.assertEquals(0, actorEntry.getBuiltStructures());
        Assert.assertEquals(0, actorEntry.getBuiltUnits());
        Assert.assertEquals(0, actorEntry.getKilledStructureBot());
        Assert.assertEquals(0, actorEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, actorEntry.getKilledUnitsBot());
        Assert.assertEquals(0, actorEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, actorEntry.getLevelCompleted());
        Assert.assertEquals(0, actorEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, actorEntry.getMoneySpent(), 0.00001);
        // Target
        Assert.assertEquals(0, targetEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, targetEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(1, targetEntry.getOwnBaseLost());
        Assert.assertEquals(0, targetEntry.getBuiltStructures());
        Assert.assertEquals(0, targetEntry.getBuiltUnits());
        Assert.assertEquals(0, targetEntry.getKilledStructureBot());
        Assert.assertEquals(0, targetEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, targetEntry.getKilledUnitsBot());
        Assert.assertEquals(0, targetEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, targetEntry.getLevelCompleted());
        Assert.assertEquals(0, targetEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, targetEntry.getMoneySpent(), 0.00001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void baseLostBot() throws Exception {
        configureMinimalGame();

        User user = new User();
        user.registerUser("xxx", "", "");
        getHibernateTemplate().save(user);

        SimpleBase botBase = new SimpleBase(1);
        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(user);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(botBase)).andReturn(true).anyTimes();
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        statisticsService.onBaseKilled(botBase, actorBase);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(1, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void builtUnits() throws Exception {
        configureMinimalGame();

        User actorUser = new User();
        actorUser.registerUser("xxx", "", "");
        getHibernateTemplate().save(actorUser);

        SimpleBase actorBase = new SimpleBase(2);
        UserState actorUserState = new UserState();
        actorUserState.setUser(actorUser);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isBot(actorBase)).andReturn(false).anyTimes();
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        SyncBaseItem createdItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(500, 500), new Id(1, 1, 1), actorBase);
        statisticsService.onItemCreated(createdItem);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(1, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void moneySpent() throws Exception {
        configureMinimalGame();

        User actorUser = new User();
        actorUser.registerUser("xxx", "", "");
        getHibernateTemplate().save(actorUser);

        SimpleBase actorBase = new SimpleBase(1);
        UserState actorUserState = new UserState();
        actorUserState.setUser(actorUser);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.getUserState(actorBase)).andReturn(actorUserState).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        statisticsService.onMoneySpent(actorBase, 0.5);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0.5, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void levelCompleted() throws Exception {
        configureMinimalGame();

        User actorUser = new User();
        actorUser.registerUser("xxx", "", "");
        getHibernateTemplate().save(actorUser);

        UserState actorUserState = new UserState();
        actorUserState.setUser(actorUser);

        statisticsService.onLevelPromotion(actorUserState);

        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        DbStatisticsEntry dbStatisticsEntry = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedBot());
        Assert.assertEquals(0, dbStatisticsEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getOwnBaseLost());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltStructures());
        Assert.assertEquals(0, dbStatisticsEntry.getBuiltUnits());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructureBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledStructurePlayer());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsBot());
        Assert.assertEquals(0, dbStatisticsEntry.getKilledUnitsPlayer());
        Assert.assertEquals(1, dbStatisticsEntry.getLevelCompleted());
        Assert.assertEquals(0, dbStatisticsEntry.getMoneyEarned(), 0.00001);
        Assert.assertEquals(0, dbStatisticsEntry.getMoneySpent(), 0.00001);
    }

    @Test
    @DirtiesContext
    public void multipleMoveCacheToDb() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);

        SimpleBase base1 = new SimpleBase(1);
        UserState userState1 = new UserState();
        userState1.setUser(user1);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.getUserState(base1)).andReturn(userState1).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        Date date = new Date();
        statisticsService.onMoneyEarned(base1, 0.7);
        Assert.assertTrue(getHibernateTemplate().loadAll(DbStatisticsEntry.class).isEmpty());
        getImpl().moveCacheToDb();
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbStatisticsEntry entry1 = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(0.7, entry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.dayStart(date), entry1.getDate());
        Assert.assertEquals(user1.getUsername(), entry1.getUser().getUsername());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        statisticsService.onMoneyEarned(base1, 1.5);
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        getImpl().moveCacheToDb();
        Assert.assertEquals(2, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        entry1 = getHibernateTemplate().load(DbStatisticsEntry.class, entry1.getId());
        List<DbStatisticsEntry> entries = getHibernateTemplate().loadAll(DbStatisticsEntry.class);
        entries.remove(entry1);
        Assert.assertEquals(1, entries.size());
        DbStatisticsEntry entry2 = entries.get(0);
        Assert.assertEquals(DateUtil.dayStart(date), entry1.getDate());
        Assert.assertEquals(DateUtil.dayStart(date), entry2.getDate());
        Assert.assertEquals(user1.getUsername(), entry1.getUser().getUsername());
        Assert.assertEquals(user1.getUsername(), entry2.getUser().getUsername());
        Assert.assertEquals(0.7, entry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(1.5, entry2.getMoneyEarned(), 0.0001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        statisticsService.onMoneyEarned(base1, 2.2);
        Assert.assertEquals(2, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        getImpl().moveCacheToDb();
        Assert.assertEquals(3, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        entry1 = getHibernateTemplate().load(DbStatisticsEntry.class, entry1.getId());
        entry2 = getHibernateTemplate().load(DbStatisticsEntry.class, entry2.getId());
        entries = getHibernateTemplate().loadAll(DbStatisticsEntry.class);
        entries.remove(entry1);
        entries.remove(entry2);
        Assert.assertEquals(1, entries.size());
        DbStatisticsEntry entry3 = entries.get(0);
        Assert.assertEquals(DateUtil.dayStart(date), entry1.getDate());
        Assert.assertEquals(DateUtil.dayStart(date), entry2.getDate());
        Assert.assertEquals(DateUtil.dayStart(date), entry3.getDate());
        Assert.assertEquals(user1.getUsername(), entry1.getUser().getUsername());
        Assert.assertEquals(user1.getUsername(), entry2.getUser().getUsername());
        Assert.assertEquals(user1.getUsername(), entry3.getUser().getUsername());
        Assert.assertEquals(0.7, entry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(1.5, entry2.getMoneyEarned(), 0.0001);
        Assert.assertEquals(2.2, entry3.getMoneyEarned(), 0.0001);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void closeDayIfNecessary() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);

        SimpleBase base1 = new SimpleBase(1);
        UserState userState1 = new UserState();
        userState1.setUser(user1);

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.getUserState(base1)).andReturn(userState1).anyTimes();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        Date date = new Date();
        statisticsService.onMoneyEarned(base1, 3.4);
        Assert.assertTrue(getHibernateTemplate().loadAll(DbStatisticsEntry.class).isEmpty());
        statisticsService.onMoneyEarned(base1, 5.6);
        Assert.assertTrue(getHibernateTemplate().loadAll(DbStatisticsEntry.class).isEmpty());

        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.dayStart(date).getTime());
        statisticsService.onMoneyEarned(base1, 1.2);
        Assert.assertEquals(1, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        DbStatisticsEntry entry1 = getHibernateTemplate().loadAll(DbStatisticsEntry.class).get(0);
        Assert.assertEquals(9.0, entry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.removeOneDay(DateUtil.dayStart(date)), entry1.getDate());

        getImpl().moveCacheToDb();
        Assert.assertEquals(2, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        List<DbStatisticsEntry> entries = getHibernateTemplate().loadAll(DbStatisticsEntry.class);
        entries.remove(entry1);
        Assert.assertEquals(1, entries.size());
        DbStatisticsEntry entry2 = entries.get(0);
        Assert.assertEquals(9.0, entry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.removeOneDay(DateUtil.dayStart(date)), entry1.getDate());
        Assert.assertEquals(1.2, entry2.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DateUtil.dayStart(date), entry2.getDate());
    }

    @Test
    @DirtiesContext
    public void endOfDayProcessingEmpty() throws Exception {
        getImpl().endOfDayProcessing(DateUtil.createDate(2011, Calendar.SEPTEMBER, 2));
        Assert.assertTrue(getHibernateTemplate().loadAll(DbStatisticsEntry.class).isEmpty());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(statisticsService.getDayStatistics().readDbChildren().isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void endOfDayProcessingMakeWholeDays() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);

        Date date = DateUtil.createDate(2011, Calendar.SEPTEMBER, 2);

        createSaveDbStatisticsEntry(date, user1, 3.8, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date, user1, 42.7, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date, user1, 19.5, DbStatisticsEntry.Type.DAY);
        Assert.assertEquals(3, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        getImpl().endOfDayProcessing(date);
        Assert.assertEquals(2, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.addOneDay(date).getTime());
        Collection<DbStatisticsEntry> collection = statisticsService.getDayStatistics().readDbChildren();
        Assert.assertEquals(1, collection.size());
        DbStatisticsEntry dbStatisticsEntry = collection.iterator().next();
        Assert.assertEquals(date, dbStatisticsEntry.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry.getUser().getUsername());
        Assert.assertEquals(66.0, dbStatisticsEntry.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.DAY, dbStatisticsEntry.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void endOfDayProcessingMakeWholeDaysTwoUsers() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);
        User user2 = new User();
        user2.registerUser("yyy", "", "");
        getHibernateTemplate().save(user2);

        Date date = DateUtil.createDate(2011, Calendar.AUGUST, 18);

        createSaveDbStatisticsEntry(date, user1, 3.8, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date, user1, 42.7, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date, user2, 17, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date, user2, 18, DbStatisticsEntry.Type.DAY);
        Assert.assertEquals(4, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        getImpl().endOfDayProcessing(date);
        Assert.assertEquals(4, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.addOneDay(date).getTime());
        ContentSortList contentSortList = new ContentSortList();
        contentSortList.addDesc("moneyEarned");
        Collection<DbStatisticsEntry> collection = statisticsService.getDayStatistics().readDbChildren(contentSortList);

        Assert.assertEquals(2, collection.size());
        Iterator<DbStatisticsEntry> iterator = collection.iterator();
        DbStatisticsEntry dbStatisticsEntry1 = iterator.next();
        Assert.assertEquals(date, dbStatisticsEntry1.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry1.getUser().getUsername());
        Assert.assertEquals(46.5, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.DAY, dbStatisticsEntry1.getType());

        DbStatisticsEntry dbStatisticsEntry2 = iterator.next();
        Assert.assertEquals(date, dbStatisticsEntry2.getDate());
        Assert.assertEquals(user2.getUsername(), dbStatisticsEntry2.getUser().getUsername());
        Assert.assertEquals(35.0, dbStatisticsEntry2.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.DAY, dbStatisticsEntry2.getType());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void endOfDayProcessingMakeWholeDaysTwoDates() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);

        Date date1 = DateUtil.createDate(2011, Calendar.AUGUST, 18);
        Date date2 = DateUtil.createDate(2011, Calendar.AUGUST, 19);

        createSaveDbStatisticsEntry(date1, user1, 3.8, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date1, user1, 42.7, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date2, user1, 17, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(date2, user1, 18, DbStatisticsEntry.Type.DAY);
        Assert.assertEquals(4, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        getImpl().endOfDayProcessing(date2);
        Assert.assertEquals(3, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.addOneDay(date2).getTime());
        ContentSortList contentSortList = new ContentSortList();
        contentSortList.addDesc("moneyEarned");
        Collection<DbStatisticsEntry> collection = statisticsService.getDayStatistics().readDbChildren(contentSortList);

        Assert.assertEquals(1, collection.size());
        Iterator<DbStatisticsEntry> iterator = collection.iterator();
        DbStatisticsEntry dbStatisticsEntry1 = iterator.next();
        Assert.assertEquals(date2, dbStatisticsEntry1.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry1.getUser().getUsername());
        Assert.assertEquals(35, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.DAY, dbStatisticsEntry1.getType());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void endOfDayProcessingWeek() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);

        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 12), user1, 1.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 13), user1, 2.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 14), user1, 4.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 15), user1, 8.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 16), user1, 16.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 17), user1, 32.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 18), user1, 64.0, DbStatisticsEntry.Type.DAY);
        // Will be ignored, not processing week
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 19), user1, 128.0, DbStatisticsEntry.Type.DAY);

        Assert.assertEquals(8, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        getImpl().endOfDayProcessing(DateUtil.createDate(2011, Calendar.SEPTEMBER, 19));
        Assert.assertEquals(10, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 20).getTime());
        ContentSortList contentSortList = new ContentSortList();
        contentSortList.addDesc("moneyEarned");
        Collection<DbStatisticsEntry> collection = statisticsService.getWeekStatistics().readDbChildren(contentSortList);

        Assert.assertEquals(1, collection.size());
        Iterator<DbStatisticsEntry> iterator = collection.iterator();
        DbStatisticsEntry dbStatisticsEntry1 = iterator.next();
        Assert.assertEquals(DateUtil.createDate(2011, Calendar.SEPTEMBER, 12), dbStatisticsEntry1.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry1.getUser().getUsername());
        Assert.assertEquals(127, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.WEEK, dbStatisticsEntry1.getType());

        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 19).getTime());
        Assert.assertEquals(0, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 20).getTime());
        Assert.assertEquals(1, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 21).getTime());
        Assert.assertEquals(1, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 22).getTime());
        Assert.assertEquals(1, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 23).getTime());
        Assert.assertEquals(1, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 24).getTime());
        Assert.assertEquals(1, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 25).getTime());
        Assert.assertEquals(1, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 26).getTime());
        Assert.assertEquals(1, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 27).getTime());
        Assert.assertEquals(0, statisticsService.getWeekStatistics().readDbChildren(contentSortList).size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void endOfDayProcessingAllTime() throws Exception {
        User user1 = new User();
        user1.registerUser("xxx", "", "");
        getHibernateTemplate().save(user1);

        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 1), user1, 2.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 2), user1, 2.0, DbStatisticsEntry.Type.DAY);
        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 3), user1, 2.0, DbStatisticsEntry.Type.DAY);
        Assert.assertEquals(3, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        getImpl().endOfDayProcessing(DateUtil.createDate(2011, Calendar.SEPTEMBER, 4));
        Assert.assertEquals(4, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 20).getTime());
        ContentSortList contentSortList = new ContentSortList();
        contentSortList.addDesc("moneyEarned");
        Collection<DbStatisticsEntry> collection = statisticsService.getAllTimeStatistics().readDbChildren(contentSortList);
        Assert.assertEquals(1, collection.size());
        Iterator<DbStatisticsEntry> iterator = collection.iterator();
        DbStatisticsEntry dbStatisticsEntry1 = iterator.next();
        Assert.assertEquals(DateUtil.createDate(2011, Calendar.SEPTEMBER, 4), dbStatisticsEntry1.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry1.getUser().getUsername());
        Assert.assertEquals(6, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.ALL_TIME, dbStatisticsEntry1.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        getImpl().endOfDayProcessing(DateUtil.createDate(2011, Calendar.SEPTEMBER, 4));
        Assert.assertEquals(4, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 20).getTime());
        contentSortList = new ContentSortList();
        contentSortList.addDesc("moneyEarned");
        collection = statisticsService.getAllTimeStatistics().readDbChildren(contentSortList);
        Assert.assertEquals(1, collection.size());
        iterator = collection.iterator();
        dbStatisticsEntry1 = iterator.next();
        Assert.assertEquals(DateUtil.createDate(2011, Calendar.SEPTEMBER, 4), dbStatisticsEntry1.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry1.getUser().getUsername());
        Assert.assertEquals(6, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.ALL_TIME, dbStatisticsEntry1.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        getImpl().endOfDayProcessing(DateUtil.createDate(2011, Calendar.SEPTEMBER, 5));
        Assert.assertEquals(5, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 20).getTime());
        contentSortList = new ContentSortList();
        contentSortList.addDesc("moneyEarned");
        collection = statisticsService.getAllTimeStatistics().readDbChildren(contentSortList);
        Assert.assertEquals(1, collection.size());
        iterator = collection.iterator();
        dbStatisticsEntry1 = iterator.next();
        Assert.assertEquals(DateUtil.createDate(2011, Calendar.SEPTEMBER, 5), dbStatisticsEntry1.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry1.getUser().getUsername());
        Assert.assertEquals(6, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.ALL_TIME, dbStatisticsEntry1.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        createSaveDbStatisticsEntry(DateUtil.createDate(2011, Calendar.SEPTEMBER, 6), user1, 2.0, DbStatisticsEntry.Type.DAY);
        Assert.assertEquals(6, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());
        getImpl().endOfDayProcessing(DateUtil.createDate(2011, Calendar.SEPTEMBER, 6));
        Assert.assertEquals(6, getHibernateTemplate().loadAll(DbStatisticsEntry.class).size());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "nextDay", DateUtil.createDate(2011, Calendar.SEPTEMBER, 20).getTime());
        contentSortList = new ContentSortList();
        contentSortList.addDesc("moneyEarned");
        collection = statisticsService.getAllTimeStatistics().readDbChildren(contentSortList);
        Assert.assertEquals(1, collection.size());
        iterator = collection.iterator();
        dbStatisticsEntry1 = iterator.next();
        Assert.assertEquals(DateUtil.createDate(2011, Calendar.SEPTEMBER, 6), dbStatisticsEntry1.getDate());
        Assert.assertEquals(user1.getUsername(), dbStatisticsEntry1.getUser().getUsername());
        Assert.assertEquals(8, dbStatisticsEntry1.getMoneyEarned(), 0.0001);
        Assert.assertEquals(DbStatisticsEntry.Type.ALL_TIME, dbStatisticsEntry1.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private DbStatisticsEntry createSaveDbStatisticsEntry(final Date date, final User user, final double moneyEarned, final DbStatisticsEntry.Type type) {
        SessionFactoryUtils.initDeferredClose(getHibernateTemplate().getSessionFactory());
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(getTransactionManager());
            return transactionTemplate.execute(new TransactionCallback<DbStatisticsEntry>() {

                @Override
                public DbStatisticsEntry doInTransaction(TransactionStatus status) {
                    DbStatisticsEntry entry = new DbStatisticsEntry();
                    entry.setDate(date);
                    entry.setUser(user);
                    entry.setMoneyEarned(moneyEarned);
                    entry.setType(type);
                    getHibernateTemplate().save(entry);
                    return entry;
                }
            });
        } finally {
            SessionFactoryUtils.processDeferredClose(getHibernateTemplate().getSessionFactory());
        }
    }

    @Test
    @DirtiesContext
    public void currentStatisticsEmpty() throws Exception {
        List<UserState> userStates = new ArrayList<UserState>();

        UserService userServiceMock = EasyMock.createMock(UserService.class);
        EasyMock.expect(userServiceMock.getAllUserStates()).andReturn(userStates).once();
        EasyMock.replay(userServiceMock);

        setPrivateField(StatisticsServiceImpl.class, statisticsService, "userService", userServiceMock);

        ReadonlyListContentProvider<CurrentStatisticEntry> current = statisticsService.getCurrentStatistics();
        Assert.assertEquals(0, current.readDbChildren().size());
    }

    @Test
    @DirtiesContext
    public void currentStatistics() throws Exception {
        configureMinimalGame();

        List<UserState> userStates = new ArrayList<UserState>();
        UserState userState = new UserState();
        userState.setCurrentAbstractLevel(userGuidanceService.getDbLevel(TEST_LEVEL_1_SIMULATED));
        userStates.add(userState);

        userState = new UserState();
        Base base1 = new Base(userState, 1);
        base1.setAccountBalance(1234);
        setPrivateField(Base.class, base1, "startTime", new Date(System.currentTimeMillis() - DateUtil.MILLIS_IN_HOUR));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1, 1)));
        base1.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1, 1)));
        userState.setBase(base1);
        userState.setCurrentAbstractLevel(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL));
        userStates.add(userState);

        User user = new User();
        user.registerUser("xxx", "", "");
        userState = new UserState();
        userState.setUser(user);
        Base base2 = new Base(userState, 2);
        base2.setAccountBalance(90);
        setPrivateField(Base.class, base2, "startTime", new Date(System.currentTimeMillis() - DateUtil.MILLIS_IN_MINUTE));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(1, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(2, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(3, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(4, 1, 1)));
        base2.addItem(createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(100, 100), new Id(5, 1, 1)));
        userState.setBase(base2);
        userState.setCurrentAbstractLevel(userGuidanceService.getDbLevel(TEST_LEVEL_2_REAL));
        userStates.add(userState);

        UserService userServiceMock = EasyMock.createMock(UserService.class);
        EasyMock.expect(userServiceMock.getAllUserStates()).andReturn(userStates).once();
        EasyMock.replay(userServiceMock);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "userService", userServiceMock);

        BaseService baseService = EasyMock.createMock(BaseService.class);
        EasyMock.expect(baseService.getBaseName(base1.getSimpleBase())).andReturn("Base 1").once();
        EasyMock.expect(baseService.getBaseName(base2.getSimpleBase())).andReturn("RegUser").once();
        EasyMock.replay(baseService);
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        ReadonlyListContentProvider<CurrentStatisticEntry> current = statisticsService.getCurrentStatistics();
        Assert.assertEquals(3, current.readDbChildren().size());

        CurrentStatisticEntry entry = current.readDbChildren().get(0);
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, entry.getLevel().getName());
        Assert.assertEquals(null, entry.getUser());
        Assert.assertEquals(null, entry.getMoney());
        Assert.assertEquals(null, entry.getBaseName());
        Assert.assertEquals(null, entry.getBaseUpTime());
        Assert.assertEquals(null, entry.getItemCount());
        entry = current.readDbChildren().get(1);
        Assert.assertEquals(TEST_LEVEL_2_REAL, entry.getLevel().getName());
        Assert.assertEquals(null, entry.getUser());
        Assert.assertEquals(1234, (int) entry.getMoney());
        Assert.assertEquals("Base 1", entry.getBaseName());
        Assert.assertEquals(DateUtil.MILLIS_IN_HOUR, DateUtil.stripOfMillis(entry.getBaseUpTime()));
        Assert.assertEquals(2, (int) entry.getItemCount());
        entry = current.readDbChildren().get(2);
        Assert.assertEquals(TEST_LEVEL_2_REAL, entry.getLevel().getName());
        Assert.assertEquals("xxx", entry.getUser().getUsername());
        Assert.assertEquals(90, (int) entry.getMoney());
        Assert.assertEquals("RegUser", entry.getBaseName());
        Assert.assertEquals(DateUtil.MILLIS_IN_MINUTE, DateUtil.stripOfMillis(entry.getBaseUpTime()));
        Assert.assertEquals(5, (int) entry.getItemCount());
    }
}