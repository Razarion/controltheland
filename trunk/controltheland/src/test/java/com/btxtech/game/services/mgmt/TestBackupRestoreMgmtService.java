package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.client.MovableService;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.RealityInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.services.BaseTestService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.market.MarketEntry;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestBackupRestoreMgmtService extends BaseTestService {
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
    @Autowired
    private MovableService movableService;
    @Autowired
    private BotService botService;

    @Test
    @DirtiesContext
    public void twoRegUserOneUnregUserAllOffline() throws Exception {
        configureMinimalGame();
        System.out.println("**** twoRegUserOneUnregUserAllOffline ****");

        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, second level -> will be willed
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase unregKillBase = ((RealityInfo) movableService.getGameInfo()).getBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U2", "test", "test", "test");
        userService.login("U2", "test");
        movableService.getGameInfo();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        SimpleBase u2Base = ((RealityInfo) movableService.getGameInfo()).getBase();
        Index buildPos = collisionService.getFreeRandomPosition(itemService.getItemType(TEST_FACTORY_ITEM_ID),new Rectangle(0,0,100000,100000), 400);
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
        movableService.getGameInfo();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        movableService.getGameInfo();        
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        // Unregistered base, fist level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForActionServiceDone();        

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
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
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
    }

    @Test
    @DirtiesContext
    public void onlineUnregUser() throws Exception {
        configureMinimalGame();
        System.out.println("**** onlineUnregUser ****");

        // Unreg user online, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        movableService.getGameInfo();
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
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
        configureMinimalGame();
        System.out.println("**** bot ****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig = setupMinimalBot();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        while (!botService.getBotRunner(dbBotConfig).isBuildup()) {
            Thread.sleep(100);
        }

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertEquals(3, baseService.getBases().get(0).getItems().size());
        Assert.assertTrue(baseService.getBases().get(0).getUserState().isBot());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        Assert.assertEquals(1, userService.getAllBotUserStates().size());
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertTrue(baseService.getBases().get(0).getUserState().isBot());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        Assert.assertEquals(1, userService.getAllBotUserStates().size());
        Assert.assertEquals(3, baseService.getBases().get(0).getItems().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        Assert.assertEquals(1, baseService.getBases().size());
        Assert.assertTrue(baseService.getBases().get(0).getUserState().isBot());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        Assert.assertEquals(1, userService.getAllBotUserStates().size());
        Assert.assertEquals(3, baseService.getBases().get(0).getItems().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void conditionServiceImpl() throws Exception {
        configureMinimalGame();
        System.out.println("**** twoRegUserOneUnregUserAllOffline ****");

        // U1 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.createUser("U1", "test", "test", "test");
        userService.login("U1", "test");
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, movableService.getGameInfo().getLevel().getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        mgmtService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = mgmtService.getBackupSummary();
        mgmtService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userService.login("U1", "test");
        Assert.assertEquals(TEST_LEVEL_1_SIMULATED, movableService.getGameInfo().getLevel().getName());        
        movableService.sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", "", 0, 0);
        Assert.assertEquals(TEST_LEVEL_2_REAL, movableService.getGameInfo().getLevel().getName());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void verifyUserStates(List<UserState> newUserStates, List<UserState> oldUserStates) {
        Assert.assertEquals(oldUserStates.size(), newUserStates.size());
        for (UserState oldUserState : oldUserStates) {
            User oldUser = oldUserState.getUser();
            DbBotConfig oldBotConfig = oldUserState.getBotConfig();
            UserState newUserState = findUserState(oldUserState, newUserStates);
            User newUser = newUserState.getUser();
            DbBotConfig newBotConfig = oldUserState.getBotConfig();
            if (newUser != null && newBotConfig != null) {
                Assert.fail("UserState is not allowed to have a User and a DbBotConfig");
            }
            if (newUser == null && newBotConfig == null) {
                Assert.fail("UserState is not allowed to have neither a User nor a DbBotConfig");
            }
            Assert.assertEquals(oldUser, newUser);
            Assert.assertEquals(oldBotConfig, newBotConfig);
            Assert.assertEquals(oldUserState.getCurrentAbstractLevel(), newUserState.getCurrentAbstractLevel());
            Assert.assertEquals(oldUserState.getCurrentAbstractLevel().getLevel(), newUserState.getCurrentAbstractLevel().getLevel());
            verifyUserItemTypeAccess(oldUserState.getUserItemTypeAccess(), newUserState.getUserItemTypeAccess());
            if (oldUserState.getBase() != null) {
                Assert.assertEquals(oldUserState.getBase().getSimpleBase(), newUserState.getBase().getSimpleBase());
            } else {
                Assert.assertNull(newUserState.getBase());
            }
        }
    }

    private void verifyUserItemTypeAccess(UserItemTypeAccess oldUserItemTypeAccess, UserItemTypeAccess newUserItemTypeAccess) {
        if (oldUserItemTypeAccess != null && newUserItemTypeAccess == null) {
            Assert.fail("UserItemTypeAccess do not match");
        } else if (oldUserItemTypeAccess == null && newUserItemTypeAccess != null) {
            Assert.fail("UserItemTypeAccess do not match");
        } else if (newUserItemTypeAccess == null) {
            return;
        }

        Assert.assertEquals(oldUserItemTypeAccess.getXp(), newUserItemTypeAccess.getXp());
        Collection<MarketEntry> tmpNewMarketEntry = new ArrayList<MarketEntry>(newUserItemTypeAccess.getAllowedItemTypes());
        for (MarketEntry marketEntry : oldUserItemTypeAccess.getAllowedItemTypes()) {
            if (!tmpNewMarketEntry.remove(marketEntry)) {
                Assert.fail("No MarketEntry in new UserItemTypeAccess found for: " + marketEntry);
            }
        }
        if (!tmpNewMarketEntry.isEmpty()) {
            Assert.fail("MarketEntry do not match");
        }
    }

    private UserState findUserState(UserState oldUserState, List<UserState> newUserStates) {
        UserState foundUserState = null;
        User oldUsr = oldUserState.getUser();
        DbBotConfig oldBotConfig = oldUserState.getBotConfig();
        for (UserState newUserState : newUserStates) {
            User newUsr = newUserState.getUser();
            DbBotConfig newBotConfig = oldUserState.getBotConfig();
            if (newUsr != null && newUsr.equals(oldUsr)) {
                if (foundUserState != null) {
                    Assert.fail("Second matching UserState found: " + newUserState + " fist matching base: " + foundUserState);
                }
                foundUserState = newUserState;
            } else if (newBotConfig != null && newBotConfig.equals(oldBotConfig)) {
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
        Assert.assertEquals(oldBase.getBaseHtmlColor(), newBase.getBaseHtmlColor());
        Assert.assertEquals(oldBase.getBaseId(), newBase.getBaseId());
        Assert.assertEquals(oldBase.getCreated(), newBase.getCreated());
        Assert.assertEquals(oldBase.getHouseSpace(), newBase.getHouseSpace());
        Assert.assertEquals(oldBase.getKills(), newBase.getKills());
        Assert.assertEquals(oldBase.getLost(), newBase.getLost());
        Assert.assertEquals(oldBase.getStartTime(), newBase.getStartTime());
        Assert.assertEquals(oldBase.getTotalEarned(), newBase.getTotalEarned(), 0.0);
        Assert.assertEquals(oldBase.getTotalSpent(), newBase.getTotalSpent(), 0.0);
        verifyItems(oldBase.getItems(), newBase.getItems());
    }

    private void verifyItems(HashSet<SyncBaseItem> oldItems, HashSet<SyncBaseItem> newItems) {
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
        return collisionService.getFreeRandomPosition(itemType, rectangle, 200);
    }

    public ItemType getRandomItemType() {
        int index = (int) (Math.random() * itemService.getItemTypes().size());
        return itemService.getItemTypes().get(index);
    }
}