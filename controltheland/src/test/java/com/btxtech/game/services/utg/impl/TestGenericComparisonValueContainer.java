package com.btxtech.game.services.utg.impl;

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.utg.condition.GenericComparisonValueContainer;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.impl.DbBackupEntry;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.statistics.StatisticsEntry;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.DbGenericComparisonValue;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: 07.02.2012
 * Time: 19:40:31
 */
public class TestGenericComparisonValueContainer extends AbstractServiceTest {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void saveRestore1() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer save = new GenericComparisonValueContainer();
        save.addChild(GenericComparisonValueContainer.Key.REMAINING_COUNT, 1);
        save(save, null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer restore = restore(null);
        Assert.assertEquals(1, ((Number) restore.getValue(GenericComparisonValueContainer.Key.REMAINING_COUNT)).intValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void saveRestore2() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer save = new GenericComparisonValueContainer();
        save.addChild(GenericComparisonValueContainer.Key.REMAINING_COUNT, 1);
        GenericComparisonValueContainer saveContainer = save.createChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        saveContainer.addChild(GenericComparisonValueContainer.Key.REMAINING_TIME, 2);
        saveContainer.addChild(GenericComparisonValueContainer.Key.REMAINING_COUNT, 3);
        save(save, null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer restore = restore(null);
        Assert.assertEquals(1, ((Number) restore.getValue(GenericComparisonValueContainer.Key.REMAINING_COUNT)).intValue());
        GenericComparisonValueContainer restoreContainer = restore.getChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        Assert.assertEquals(2, ((Number) restoreContainer.getValue(GenericComparisonValueContainer.Key.REMAINING_TIME)).intValue());
        Assert.assertEquals(3, ((Number) restoreContainer.getValue(GenericComparisonValueContainer.Key.REMAINING_COUNT)).intValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void saveRestore3() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer save = new GenericComparisonValueContainer();
        save.addChild(GenericComparisonValueContainer.Key.REMAINING_COUNT, 1);
        save.addChild(GenericComparisonValueContainer.Key.REMAINING_TIME, 2);
        save(save, null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer restore = restore(null);
        Assert.assertEquals(1, ((Number) restore.getValue(GenericComparisonValueContainer.Key.REMAINING_COUNT)).intValue());
        Assert.assertEquals(2, ((Number) restore.getValue(GenericComparisonValueContainer.Key.REMAINING_TIME)).intValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void saveRestore4() throws Exception {
        configureItemTypes();
        // Save
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer save = new GenericComparisonValueContainer();
        GenericComparisonValueContainer itemCounts = save.createChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        itemCounts.addChild(serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1);
        itemCounts.addChild(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 2);
        itemCounts.addChild(serverItemTypeService.getItemType(TEST_CONTAINER_ITEM_ID), 3);
        save(save, serverItemTypeService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        GenericComparisonValueContainer restore = restore(serverItemTypeService);
        GenericComparisonValueContainer restoreItemCounts = restore.getChildContainer(GenericComparisonValueContainer.Key.REMAINING_ITEM_TYPES);
        Assert.assertEquals(1, ((Number) restoreItemCounts.getValue(serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID))).intValue());
        Assert.assertEquals(2, ((Number) restoreItemCounts.getValue(serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID))).intValue());
        Assert.assertEquals(3, ((Number) restoreItemCounts.getValue(serverItemTypeService.getItemType(TEST_CONTAINER_ITEM_ID))).intValue());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void save(final GenericComparisonValueContainer save, final ServerItemTypeService serverItemTypeService) {
        // Save
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                DbBackupEntry dbBackupEntry = new DbBackupEntry();
                UserState userState = new UserState();
                DbUserState dbUserState = new DbUserState(dbBackupEntry, userService.getUser(userState.getUser()), userState, null, null, null, null, null, null);
                dbUserState.addDbGenericComparisonValue(new DbGenericComparisonValue(1, save, serverItemTypeService));
                statisticsService.createAndAddBackup(dbUserState, userState);
                dbUserState.setStatisticsEntry(new StatisticsEntry());
                Set<DbUserState> dbUserStates = new HashSet<>();
                dbUserStates.add(dbUserState);
                dbBackupEntry.setUserStates(dbUserStates);
                sessionFactory.getCurrentSession().save(dbBackupEntry);
            }
        });
        System.out.println("----SAVE DONE---");
    }

    private GenericComparisonValueContainer restore(ServerItemTypeService serverItemTypeService) throws NoSuchItemTypeException {
        // restore
        List<DbBackupEntry> backupEntries = HibernateUtil.loadAll(sessionFactory, DbBackupEntry.class);
        Assert.assertEquals(1, backupEntries.size());
        DbBackupEntry dbBackupEntry = backupEntries.get(0);
        Assert.assertEquals(1, dbBackupEntry.getUserStates().size());
        DbUserState dbUserState = CommonJava.getFirst(dbBackupEntry.getUserStates());
        Assert.assertEquals(1, dbUserState.getDbGenericComparisonValues().size());
        DbGenericComparisonValue dbGenericComparisonValue = CommonJava.getFirst(dbUserState.getDbGenericComparisonValues());
        return dbGenericComparisonValue.createGenericComparisonValueContainer(serverItemTypeService);
    }
}
