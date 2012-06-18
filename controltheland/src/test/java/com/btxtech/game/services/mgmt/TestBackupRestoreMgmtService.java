package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncMovable;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestBackupRestoreMgmtService extends AbstractServiceTest {
    public static final int ITEM_COUNT = 100000;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private TerrainService terrainService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;

    @Test
    @DirtiesContext
    public void twoRegUserOneUnregUserAllOffline() throws Exception {
        configureGameMultipleLevel();
        System.out.println("**** twoRegUserOneUnregUserAllOffline ****");

        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        userGuidanceService.getDbLevel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, second level -> will be killed
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDbLevel();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        SimpleBase unregKillBase = getMyBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U2", "test", "test", "test");
        userService.login("U2", "test");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        SimpleBase u2Base = getMyBase();
        Index buildPos = collisionService.getFreeRandomPosition(itemService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true, false);
        sendBuildCommand(getFirstSynItemId(u2Base, TEST_START_BUILDER_ITEM_ID), buildPos, TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(u2Base, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(u2Base, TEST_ATTACK_ITEM_ID), getFirstSynItemId(unregKillBase, TEST_START_BUILDER_ITEM_ID));
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U3 real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U3", "test", "test", "test");
        userService.login("U3", "test");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Unregistered base, fist level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDbLevel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForActionServiceDone();
        Thread.sleep(3000); // Wait for XP

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Base> oldBases = baseService.getBases();
        List<UserState> oldUserStates = userService.getAllUserStates();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        assertBackupSummery(1, 15, 3, 3);
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Base> newBases = baseService.getBases();
        List<UserState> newUserStates = userService.getAllUserStates();
        Assert.assertEquals(3, newBases.size());
        Assert.assertEquals(3, newUserStates.size());
        verifyUserStates(newUserStates, oldUserStates);
        verifyBases(newBases, oldBases);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        assertBackupSummery(2, 15, 3, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        assertBackupSummery(3, 15, 3, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        newBases = baseService.getBases();
        newUserStates = userService.getAllUserStates();
        Assert.assertEquals(3, newBases.size());
        Assert.assertEquals(3, newUserStates.size());
        verifyUserStates(newUserStates, oldUserStates);
        verifyBases(newBases, oldBases);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        Assert.assertEquals(3, backupSummaries.size());
        mgmtService.deleteBackupEntry(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        Assert.assertEquals(2, backupSummaries.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        Assert.assertEquals(2, backupSummaries.size());
        mgmtService.deleteBackupEntry(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        Assert.assertEquals(1, backupSummaries.size());
        mgmtService.restore(backupSummaries.get(0).getDate());
        newBases = baseService.getBases();
        newUserStates = userService.getAllUserStates();
        Assert.assertEquals(3, newBases.size());
        Assert.assertEquals(3, newUserStates.size());
        verifyUserStates(newUserStates, oldUserStates);
        verifyBases(newBases, oldBases);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void onlineUnregUser() throws Exception {
        configureGameMultipleLevel();
        System.out.println("**** onlineUnregUser ****");

        // Unreg user online, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 11, 1, 0);

        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Base> newBases = baseService.getBases();
        List<UserState> newUserStates = userService.getAllUserStates();
        Assert.assertEquals(0, newUserStates.size());
        Assert.assertEquals(1, newBases.size());
        Base base = newBases.get(0);
        Assert.assertTrue(base.isAbandoned());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void bot() throws Exception {
        configureGameMultipleLevel();
        System.out.println("**** bot ****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalBot(new Rectangle(4000, 4000, 3000, 3000)).createBotConfig(itemService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForBotToBuildup(botConfig);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertEquals(4, baseService.getBases().get(0).getItems().size());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 10, 0, 0);

        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        waitForActionServiceDone();
        waitForBotToBuildup(botConfig);
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertEquals(4, baseService.getBases().get(0).getItems().size());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(2, 10, 0, 0);
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        waitForActionServiceDone();
        // TODO failed on: 18.06.2012
        waitForBotToBuildup(botConfig);
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertEquals(4, baseService.getBases().get(0).getItems().size());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void botAttacking() throws Exception {
        configureGameMultipleLevel();
        System.out.println("**** bot ****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalNoAttackBot(new Rectangle(4000, 4000, 3000, 3000)).createBotConfig(itemService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForBotToBuildup(botConfig);

        Assert.assertEquals(1, baseService.getBases().size());
        SimpleBase botBase = baseService.getBases().get(0).getSimpleBase();

        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        SimpleBase realUser = getMyBase();
        Index buildPos = collisionService.getFreeRandomPosition(itemService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true, false);
        sendBuildCommand(getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID), buildPos, TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(realUser, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(realUser, TEST_ATTACK_ITEM_ID), getFirstSynItemId(botBase, TEST_FACTORY_ITEM_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 13, 1, 1);
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        waitForActionServiceDone();
        waitForBotToBuildup(botConfig);
        Assert.assertEquals(2, baseService.getBases().size());
        Assert.assertEquals(1, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


    }

    @Test
    @DirtiesContext
    public void longPathToDestination() throws Exception {
        configureGameMultipleLevel();
        System.out.println("**** longPathToDestination ****");

        // U1 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), "", 0, 0);
        SimpleBase realUser = getMyBase();
        Id id = getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID);
        SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.getItem(id);
        // Fill artificial path to long
        List<Index> pathToDestination = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            pathToDestination.add(new Index(i, i));
        }
        syncBaseItem.getSyncMovable().setPathToDestination(pathToDestination, MathHelper.WEST);
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 11, 1, 1);
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        realUser = getMyBase();
        id = getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID);
        syncBaseItem = (SyncBaseItem) itemService.getItem(id);
        SyncMovable syncMovable = syncBaseItem.getSyncMovable();
        Assert.assertTrue(syncMovable.getPathToDestination() == null || syncMovable.getPathToDestination().isEmpty());
        // Fill artificial path ca. 820 cahracters
        pathToDestination = new ArrayList<>();
        for (int i = 0; i < 130; i++) {
            pathToDestination.add(new Index(i, i));
        }
        syncMovable.setPathToDestination(pathToDestination, MathHelper.WEST);
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(2, 11, 1, 1);
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        realUser = getMyBase();
        id = getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID);
        syncBaseItem = (SyncBaseItem) itemService.getItem(id);
        // Assert path has at lease more the 50 entries (original it was 130 but some are may be already achievement)
        Assert.assertTrue(syncBaseItem.getSyncMovable().getPathToDestination().size() > 50);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void verifyUserStates(List<UserState> newUserStates, List<UserState> oldUserStates) {
        Assert.assertEquals(oldUserStates.size(), newUserStates.size());
        for (UserState oldUserState : oldUserStates) {
            String oldUser = oldUserState.getUser();
            UserState newUserState = findUserState(oldUserState, newUserStates);
            String newUser = newUserState.getUser();
            Assert.assertEquals(oldUser, newUser);
            Assert.assertEquals(oldUserState.getXp(), newUserState.getXp());
            Assert.assertEquals(oldUserState.getDbLevelId(), newUserState.getDbLevelId());
            if (oldUserState.getBase() != null) {
                Assert.assertEquals(oldUserState.getBase().getSimpleBase(), newUserState.getBase().getSimpleBase());
            } else {
                Assert.assertNull(newUserState.getBase());
            }
        }
    }

    private UserState findUserState(UserState oldUserState, List<UserState> newUserStates) {
        UserState foundUserState = null;
        String oldUsr = oldUserState.getUser();
        for (UserState newUserState : newUserStates) {
            String newUsr = newUserState.getUser();
            if (newUsr != null && newUsr.equals(oldUsr)) {
                if (foundUserState != null) {
                    Assert.fail("Second matching UserState found: " + newUserState + " fist matching base: " + foundUserState);
                }
                foundUserState = newUserState;
            }
        }
        if (foundUserState == null) {
            Assert.fail("No matching UserState found for: " + oldUserState);
        }
        return foundUserState;
    }

    private void verifyBases(List<Base> newBases, List<Base> oldBases) {
        Assert.assertEquals(oldBases.size(), newBases.size());
        for (Base oldBase : oldBases) {
            Base newBase = findBase(oldBase, newBases);
            verifyBase(newBase, oldBase);
        }
    }

    private Base findBase(Base oldBase, List<Base> newBases) {
        Base foundBase = null;
        for (Base newBase : newBases) {
            if (newBase.getSimpleBase().equals(oldBase.getSimpleBase())) {
                if (foundBase != null) {
                    Assert.fail("Second matching base found: " + newBase + " fist matching base: " + foundBase);
                }
                foundBase = newBase;
            }
        }
        if (foundBase == null) {
            Assert.fail("No matching base found for: " + oldBase);
        }
        return foundBase;
    }

    private void verifyBase(Base newBase, Base oldBase) {
        Assert.assertEquals(oldBase.getAccountBalance(), newBase.getAccountBalance(), 0.0);
        Assert.assertEquals(oldBase.getBaseId(), newBase.getBaseId());
        Assert.assertEquals(oldBase.getHouseSpace(), newBase.getHouseSpace());
        Assert.assertEquals(oldBase.getStartTime(), newBase.getStartTime());
        verifyItems(oldBase.getItems(), newBase.getItems());
    }

    private void verifyItems(Set<SyncBaseItem> oldItems, Set<SyncBaseItem> newItems) {
        Assert.assertEquals(oldItems.size(), newItems.size());
        // TODO compare and verify items
    }

    // TODO Bot save and restore
    // TODO XP + Market bought
    // TODO more items
    // TODO Current condition (DbAbstractComparisonBackup)
    // TODO Surrender
    // TODO In session of User/unregUser backup

    //@Test

    public void testBackup2() throws Exception {
        // userGuidanceService.promote(userService.getUserState(), 5);
        // userGuidanceService.promote(userService.getUserState(), 15);
        // mgmtService.backup();
        // List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        //  mgmtService.restore(backupSummaries.get(0).getDate());
    }

    // @Test

    public void testBackupSummary() throws AlreadyUsedException {
        mgmtService.getBackupSummary();
    }

    // @Test

    public void testRestore() throws AlreadyUsedException, NoSuchItemTypeException {
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
    }

    // @Test

    public void testBigBackup() throws AlreadyUsedException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        for (int i = 0; i < ITEM_COUNT; i++) {
            ItemType itemType = getRandomItemType();
            System.out.println("Creating: " + (i + 1) + " of " + ITEM_COUNT);
            itemService.createSyncObject(itemType, getRandomPosition(itemType), null, getBase(itemType), 0);
        }
        mgmtService.backup();
    }

    private SimpleBase getBase(ItemType itemType) throws AlreadyUsedException, NoSuchItemTypeException {
        if (itemType instanceof ResourceType) {
            return null;
        } else {
            throw new IllegalArgumentException("Unknown itemType: " + itemType);
        }
    }

    private Index getRandomPosition(ItemType itemType) {
        Rectangle rectangle = new Rectangle(0, 0, terrainService.getTerrainSettings().getPlayFieldXSize(), terrainService.getTerrainSettings().getPlayFieldYSize());
        return collisionService.getFreeRandomPosition(itemType, rectangle, 200, true, false);
    }

    public ItemType getRandomItemType() {
        int index = (int) (Math.random() * itemService.getItemTypes().size());
        return itemService.getItemTypes().get(index);
    }
}