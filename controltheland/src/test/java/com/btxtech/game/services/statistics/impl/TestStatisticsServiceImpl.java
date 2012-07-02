package com.btxtech.game.services.statistics.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.NestedNullSafeBeanComparator;
import com.btxtech.game.services.common.ReadonlyListContentProvider;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.statistics.CurrentStatisticEntry;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.Collections;
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
    @Autowired
    private UserService userService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private MgmtService mgmtService;

    @Test
    @DirtiesContext
    public void simple() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        SimpleBase unregBase = new SimpleBase(1);
        UserState unregUserState = new UserState();
        unregUserState.setDbLevelId(TEST_LEVEL_1_SIMULATED);
        SimpleBase regBase = new SimpleBase(2);
        UserState regUserState = new UserState();
        regUserState.setDbLevelId(TEST_LEVEL_1_SIMULATED);
        regUserState.setUser("xxx");

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.getUserState(unregBase)).andReturn(unregUserState).anyTimes();
        EasyMock.expect(baseService.getUserState(regBase)).andReturn(regUserState).anyTimes();
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "baseService", baseService);

        UserService userService = EasyMock.createNiceMock(UserService.class);
        EasyMock.expect(userService.getAllUserStates()).andReturn(Arrays.asList(regUserState, unregUserState));
        setPrivateField(StatisticsServiceImpl.class, statisticsService, "userService", userService);

        SyncBaseItem item1RegBase = EasyMock.createNiceMock(SyncBaseItem.class);
        EasyMock.expect(item1RegBase.getBase()).andReturn(regBase).anyTimes();

        EasyMock.replay(baseService, userService, item1RegBase);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        ReadonlyListContentProvider<CurrentStatisticEntry> provider = statisticsService.getCmsCurrentStatistics();
        List<CurrentStatisticEntry> entries = provider.readDbChildren();
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "xxx", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, null, null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void normalFight() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ReadonlyListContentProvider<CurrentStatisticEntry> provider = statisticsService.getCmsCurrentStatistics();
        List<CurrentStatisticEntry> entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u1", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Build items
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test unregistered user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.getUserState(); // unregistered user
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(3, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        assertEntry(2, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, null, null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test unregistered user expired
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Build
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
        Id u1Builder = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(u1Builder, new Index(3000, 3000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Id u1Factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(u1Factory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_1_2_REAL_ID);
        baseService.depositResource(3.0, getMyBase());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3000, TEST_LEVEL_3_REAL, 0, "u1", (long) 1, 3, 1088, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // move away
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        Id target1 = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        Id target2 = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendMoveCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 3000));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Kill builder
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), target1);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(40); // Wait for XpService thread
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3002, TEST_LEVEL_3_REAL, 1, "u1", (long) 1, 3, 1088, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 1, 998, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Kill factory
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), target2);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(40);
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3007, TEST_LEVEL_3_REAL, 3, "u1", (long) 1, 3, 1088, 0, 0, 1, 1, 0, 0, 0, 0, 1, 2, 0, 1, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Fake a bot factory
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase botBase = baseService.createBotBase(new BotConfig(1, 1, null, null, "bot", null, null, null, null));
        Id targetBotFactory = itemService.createSyncObject(itemService.getItemType(TEST_FACTORY_ITEM), new Index(3000, 1000), null, botBase, 0).getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Kill bot factory
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), targetBotFactory);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(40);
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3012, TEST_LEVEL_3_REAL, 5, "u1", (long) 1, 3, 1088, 1, 0, 1, 1, 0, 0, 0, 0, 1, 2, 1, 1, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Fake a bot unit
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        botBase = baseService.createBotBase(new BotConfig(1, 1, null, null, "bot", null, null, null, null));
        Id targetBotUnit = itemService.createSyncObject(itemService.getItemType(TEST_START_BUILDER_ITEM_ID), new Index(3000, 2000), null, botBase, 0).getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Kill bot unit
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        Id u1AttackerId = getFirstSynItemId(TEST_ATTACK_ITEM_ID);
        sendAttackCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), targetBotUnit);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(40);
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3015, TEST_LEVEL_3_REAL, 6, "u1", (long) 1, 3, 1088, 1, 1, 1, 1, 0, 0, 0, 0, 1, 2, 2, 1, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Move away
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        sendMoveCommand(u1AttackerId, new Index(1000, 4000));
        sendMoveCommand(u1Builder, new Index(2000, 4000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Fake a bot unit and attack unit
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        botBase = baseService.createBotBase(new BotConfig(1, 1, null, null, "bot", null, null, null, null));
        SyncBaseItem botAttackUnit = (SyncBaseItem) itemService.createSyncObject(itemService.getItemType(TEST_ATTACK_ITEM_ID), new Index(3000, 2000), null, botBase, 0);
        botAttackUnit.setHealth(Integer.MAX_VALUE);
        sendAttackCommand(botAttackUnit.getId(), u1AttackerId);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(40);
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3015, TEST_LEVEL_3_REAL, 6, "u1", (long) 1, 2, 1088, 1, 1, 1, 1, 0, 1, 0, 0, 1, 2, 2, 1, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Bot attack factory
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sendAttackCommand(botAttackUnit.getId(), u1Builder);
        waitForActionServiceDone();
        sendAttackCommand(botAttackUnit.getId(), u1Factory);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Thread.sleep(40);
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3015, TEST_LEVEL_3_REAL, 6, "u1", null, null, null, 1, 1, 1, 1, 1, 2, 0, 0, 1, 2, 2, 1, 1, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void sellItemsAndLoseBase() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ReadonlyListContentProvider<CurrentStatisticEntry> provider = statisticsService.getCmsCurrentStatistics();
        List<CurrentStatisticEntry> entries = provider.readDbChildren();
        Assert.assertEquals(1, entries.size());
        assertEntry(0, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u1", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        Assert.assertEquals(1, entries.size());
        assertEntry(0, entries, 2004, TEST_LEVEL_2_REAL, 1, "u1", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Sell
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        itemService.sellItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        Assert.assertEquals(1, entries.size());
        assertEntry(0, entries, 2004, TEST_LEVEL_2_REAL, 1, "u1", (long) 1, 1, 999, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Sell last unit
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        itemService.sellItem(getFirstSynItemId(TEST_FACTORY_ITEM_ID));
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        Assert.assertEquals(1, entries.size());
        assertEntry(0, entries, 2004, TEST_LEVEL_2_REAL, 1, "u1", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void saverRestore() throws Exception {
        configureGameMultipleLevel();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u1", "xxx", "xxx", "");
        userService.login("u1", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("u2", "xxx", "xxx", "");
        userService.login("u2", "xxx");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ReadonlyListContentProvider<CurrentStatisticEntry> provider = statisticsService.getCmsCurrentStatistics();
        List<CurrentStatisticEntry> entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u1", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u1", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Build items
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u2", "xxx");
        userGuidanceService.onTutorialFinished(TEST_LEVEL_TASK_1_1_SIMULATED_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test unregistered user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.getUserState(); // unregistered user
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(3, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        assertEntry(2, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, null, null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Test unregistered user expired
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Build
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("u1", "xxx");
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_2_2_REAL_ID);
        Id u1Builder = getFirstSynItemId(TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(u1Builder, new Index(3000, 3000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Id u1Factory = getFirstSynItemId(TEST_FACTORY_ITEM_ID);
        sendFactoryCommand(u1Factory, TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        userGuidanceService.activateQuest(TEST_LEVEL_TASK_1_2_REAL_ID);
        baseService.depositResource(3.0, getMyBase());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        //Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 3000, TEST_LEVEL_3_REAL, 0, "u1", (long) 1, 3, 1088, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore to previous date
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(3).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u1", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore to previous date
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(2).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 1000, TEST_LEVEL_1_SIMULATED, 0, "u2", null, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore to previous date
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(1).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        provider = statisticsService.getCmsCurrentStatistics();
        entries = provider.readDbChildren();
        orderByUserName(entries);
        Assert.assertEquals(2, entries.size());
        assertEntry(0, entries, 2000, TEST_LEVEL_2_REAL, 0, "u1", (long) 1, 1, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertEntry(1, entries, 2004, TEST_LEVEL_2_REAL, 1, "u2", (long) 1, 2, 998, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @SuppressWarnings("unchecked")
    private void orderByUserName(List<CurrentStatisticEntry> entries) {
        Collections.sort(entries, new NestedNullSafeBeanComparator("userName", true));
    }

    private void assertEntry(int index, List<CurrentStatisticEntry> entries,
                             int score, int dbLevelId, int xp,
                             String userName, Long baseUpTime, Integer itemCount, Integer money,
                             int killedStructureBot, int killedUnitsBot, int killedStructurePlayer, int killedUnitsPlayer,
                             int lostStructureBot, int lostUnitsBot, int lostStructurePlayer, int lostUnitsPlayer,
                             int builtStructures, int builtUnits,
                             int basesDestroyedBot, int basesDestroyedPlayer,
                             int baseLostBot, int baseLostPlayer) {
        CurrentStatisticEntry currentStatisticEntry = entries.get(index);
        Assert.assertEquals(userGuidanceService.getDbLevel(dbLevelId), currentStatisticEntry.getLevel());
        Assert.assertEquals(score, currentStatisticEntry.getScore());
        Assert.assertEquals(xp, currentStatisticEntry.getXp());
        Assert.assertEquals(userName, currentStatisticEntry.getUserName());
        if (baseUpTime == null) {
            Assert.assertNull(currentStatisticEntry.getBaseUpTime());
        } else {
            Assert.assertNotNull(currentStatisticEntry.getBaseUpTime());
        }
        Assert.assertEquals(itemCount, currentStatisticEntry.getItemCount());
        Assert.assertEquals(money, currentStatisticEntry.getMoney());
        Assert.assertEquals(killedStructureBot, currentStatisticEntry.getKilledStructureBot());
        Assert.assertEquals(killedUnitsBot, currentStatisticEntry.getKilledUnitsBot());
        Assert.assertEquals(killedStructurePlayer, currentStatisticEntry.getKilledStructurePlayer());
        Assert.assertEquals(killedUnitsPlayer, currentStatisticEntry.getKilledUnitsPlayer());
        Assert.assertEquals(lostStructureBot, currentStatisticEntry.getLostStructureBot());
        Assert.assertEquals(lostUnitsBot, currentStatisticEntry.getLostUnitsBot());
        Assert.assertEquals(lostStructurePlayer, currentStatisticEntry.getLostStructurePlayer());
        Assert.assertEquals(lostUnitsPlayer, currentStatisticEntry.getLostUnitsPlayer());
        Assert.assertEquals(builtStructures, currentStatisticEntry.getBuiltStructures());
        Assert.assertEquals(builtUnits, currentStatisticEntry.getBuiltUnits());
        Assert.assertEquals(basesDestroyedBot, currentStatisticEntry.getBasesDestroyedBot());
        Assert.assertEquals(basesDestroyedPlayer, currentStatisticEntry.getBasesDestroyedPlayer());
        Assert.assertEquals(baseLostBot, currentStatisticEntry.getBasesLostBot());
        Assert.assertEquals(baseLostPlayer, currentStatisticEntry.getBasesLostPlayer());
    }
}