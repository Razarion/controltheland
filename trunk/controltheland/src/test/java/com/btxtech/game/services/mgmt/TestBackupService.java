package com.btxtech.game.services.mgmt;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.ClientDateUtil;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.MathHelper;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncMovable;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.jsre.common.tutorial.TutorialConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerEnergyService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.user.impl.RegisterServiceImpl;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestBackupService extends AbstractServiceTest {
    public static final int ITEM_COUNT = 100000;
    @Autowired
    private BackupService backupService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private ServerUnlockService unlockService;

    @Test
    @DirtiesContext
    public void twoRegUserOneUnregUserAllOffline() throws Exception {
        configureMultiplePlanetsAndLevels();
        System.out.println("**** twoRegUserOneUnregUserAllOffline ****");

        // U1 no real base, first level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.getDbLevel();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered base, second level -> will be killed
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.getDbLevel();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        SimpleBase unregKillBase = getOrCreateBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        SimpleBase u2Base = createBase(new Index(2000, 2000));
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        Index buildPos = serverPlanetServices.getCollisionService().getFreeRandomPosition(serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true, false);
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
        createAndLoginUser("U3");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        createBase(new Index(3000, 3000));
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
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        // TODO failed on 23.08.2013
        createBase(new Index(4000, 4000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForActionServiceDone(TEST_PLANET_1_ID);
        Thread.sleep(3000); // Wait for XP

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Base> oldBases = serverPlanetServices.getBaseService().getBases();
        List<UserState> oldUserStates = userService.getAllUserStates();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        // TODO failed 03.01.2012
        assertBackupSummery(1, 5, 3, 3);
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Base> newBases = serverPlanetServices.getBaseService().getBases();
        List<UserState> newUserStates = userService.getAllUserStates();
        Assert.assertEquals(3, newBases.size());
        Assert.assertEquals(3, newUserStates.size());
        verifyUserStates(newUserStates, oldUserStates);
        verifyBases(newBases, oldBases);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        assertBackupSummery(2, 5, 3, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        assertBackupSummery(3, 5, 3, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        newBases = serverPlanetServices.getBaseService().getBases();
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
        backupSummaries = backupService.getBackupSummary();
        Assert.assertEquals(3, backupSummaries.size());
        backupService.deleteBackupEntry(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        Assert.assertEquals(2, backupSummaries.size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Delete backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        Assert.assertEquals(2, backupSummaries.size());
        backupService.deleteBackupEntry(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        Assert.assertEquals(1, backupSummaries.size());
        backupService.restore(backupSummaries.get(0).getDate());
        newBases = serverPlanetServices.getBaseService().getBases();
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
        configureMultiplePlanetsAndLevels();
        System.out.println("**** onlineUnregUser ****");

        // Unreg user online, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        createBase(new Index(1000, 1000));
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 1, 1, 0);

        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        List<Base> newBases = serverPlanetServices.getBaseService().getBases();
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
        configureMultiplePlanetsAndLevels();
        System.out.println("**** bot ****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(4000, 4000, 3000, 3000)).createBotConfig(serverItemTypeService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // TODO failed on: 07.07.2012
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        Assert.assertEquals(1, serverPlanetServices.getBaseService().getBases().size());
        Assert.assertEquals(4, serverPlanetServices.getBaseService().getBases().get(0).getItems().size());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 0, 0, 0);

        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        waitForActionServiceDone(TEST_PLANET_1_ID);
        // TODO failed on: 21.06.2012, 03.07.2012, 07,07.2012
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        Assert.assertEquals(1, serverPlanetServices.getBaseService().getBases().size());
        Assert.assertEquals(4, serverPlanetServices.getBaseService().getBases().get(0).getItems().size());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(2, 0, 0, 0);
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        waitForActionServiceDone(TEST_PLANET_1_ID);
        // TODO failed on: 18.06.2012
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        Assert.assertEquals(1, serverPlanetServices.getBaseService().getBases().size());
        Assert.assertEquals(4, serverPlanetServices.getBaseService().getBases().get(0).getItems().size());
        Assert.assertEquals(0, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void botAttacking() throws Exception {
        configureMultiplePlanetsAndLevels();
        System.out.println("**** bot ****");

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalNoAttackBot(TEST_PLANET_1_ID, new Rectangle(4000, 4000, 3000, 3000)).createBotConfig(serverItemTypeService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);

        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        Assert.assertEquals(1, serverPlanetServices.getBaseService().getBases().size());
        SimpleBase botBase = serverPlanetServices.getBaseService().getBases().get(0).getSimpleBase();

        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        SimpleBase realUser = getOrCreateBase();
        Index buildPos = serverPlanetServices.getCollisionService().getFreeRandomPosition(serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), new Rectangle(0, 0, 100000, 100000), 400, true, false);
        sendBuildCommand(getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID), buildPos, TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(realUser, TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        sendAttackCommand(getFirstSynItemId(realUser, TEST_ATTACK_ITEM_ID), getFirstSynItemId(botBase, TEST_FACTORY_ITEM_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 3, 1, 1);
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        // TODO failed on: 11.01.2013
        // com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException: Can not find free position. itemType: ItemType: TestResourceItem region Id: 5 itemFreeRange: 100
        backupService.restore(backupSummaries.get(0).getDate());
        waitForActionServiceDone(TEST_PLANET_1_ID);
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        Assert.assertEquals(2, serverPlanetServices.getBaseService().getBases().size());
        Assert.assertEquals(1, userService.getAllUserStates().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void longPathToDestination() throws Exception {
        configureMultiplePlanetsAndLevels();
        System.out.println("**** longPathToDestination ****");
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType builderType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(TEST_START_BUILDER_ITEM_ID);
        builderType.getDbMovableType().setSpeed(1);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(builderType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U1 no real base, second level
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        getMovableService().sendTutorialProgress(TutorialConfig.TYPE.TUTORIAL, "", userGuidanceService.getDefaultLevelTaskId(), 0, "", 0, 0);
        SimpleBase realUser = getOrCreateBase();
        Id id = getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID);
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        SyncBaseItem syncBaseItem = (SyncBaseItem) serverPlanetServices.getItemService().getItem(id);
        // Fill artificial path to long
        List<Index> pathToDestination = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            pathToDestination.add(new Index(i, i));
        }
        syncBaseItem.getSyncMovable().setPathToDestination(pathToDestination, MathHelper.WEST);
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 1, 1, 1);
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        realUser = getOrCreateBase();
        id = getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID);
        syncBaseItem = (SyncBaseItem) serverPlanetServices.getItemService().getItem(id);
        SyncMovable syncMovable = syncBaseItem.getSyncMovable();
        Assert.assertTrue(syncMovable.getPathToDestination() == null || syncMovable.getPathToDestination().isEmpty());
        // Fill artificial path ca. 820 cahracters
        pathToDestination = new ArrayList<>();
        for (int i = 0; i < 130; i++) {
            pathToDestination.add(new Index(i, i));
        }
        syncMovable.setPathToDestination(pathToDestination, MathHelper.WEST);
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(2, 1, 1, 1);
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        realUser = getOrCreateBase();
        id = getFirstSynItemId(realUser, TEST_START_BUILDER_ITEM_ID);
        syncBaseItem = (SyncBaseItem) serverPlanetServices.getItemService().getItem(id);
        // Assert path has at lease more the 50 entries (original it was 130 but some are may be already achievement)
        // TODO failed on 04.07.2012, 09.07.2012, 27.07.2012
        Assert.assertTrue("Size is: " + syncBaseItem.getSyncMovable().getPathToDestination().size(), syncBaseItem.getSyncMovable().getPathToDestination().size() > 50);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void resourceAfterRestore() throws Exception {
        configureMultiplePlanetsAndLevels();

        assertWholeItemCount(TEST_PLANET_1_ID, 10);
        assertWholeItemCount(TEST_PLANET_2_ID, 5);
        assertWholeItemCount(TEST_PLANET_3_ID, 6);

        Collection<SyncResourceItem> resourceP1Before = getAllResourceItems(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID);
        Collection<SyncResourceItem> resourceP2Before = getAllResourceItems(TEST_PLANET_2_ID, TEST_RESOURCE_ITEM_ID);
        Collection<SyncResourceItem> resourceP3Before = getAllResourceItems(TEST_PLANET_3_ID, TEST_RESOURCE_ITEM_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        assertBackupSummery(1, 0, 0, 0);
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 10);
        assertWholeItemCount(TEST_PLANET_2_ID, 5);
        assertWholeItemCount(TEST_PLANET_3_ID, 6);

        Collection<SyncResourceItem> resourceP1After = getAllResourceItems(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID);
        Collection<SyncResourceItem> resourceP2After = getAllResourceItems(TEST_PLANET_2_ID, TEST_RESOURCE_ITEM_ID);
        Collection<SyncResourceItem> resourceP3After = getAllResourceItems(TEST_PLANET_3_ID, TEST_RESOURCE_ITEM_ID);

        resourceP1Before.retainAll(resourceP1After);
        resourceP2Before.retainAll(resourceP2After);
        resourceP3Before.retainAll(resourceP3After);

        Assert.assertTrue(resourceP1Before.isEmpty());
        Assert.assertTrue(resourceP2Before.isEmpty());
        Assert.assertTrue(resourceP3Before.isEmpty());
    }

    @Test
    @DirtiesContext
    public void resourceCollectingRestore() throws Exception {
        configureSimplePlanet();

        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Id harvester = getFirstSynItemId(TEST_HARVESTER_ITEM_ID);
        Id resource = getFirstResourceItem(TEST_PLANET_1_ID, TEST_RESOURCE_ITEM_ID);
        ((SyncResourceItem) planetSystemService.getServerPlanetServices().getItemService().getItem(resource)).setAmount(999999999999.0);
        SyncBaseItem harvesterItem = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(harvester);
        sendCollectCommand(harvester, resource);
        Assert.assertTrue(harvesterItem.getSyncHarvester().getTarget() != null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 3, 1, 1);
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        harvester = getFirstSynItemId(TEST_HARVESTER_ITEM_ID);
        harvesterItem = (SyncBaseItem) planetSystemService.getServerPlanetServices().getItemService().getItem(harvester);
        Assert.assertFalse(harvesterItem.getSyncHarvester().getTarget() != null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void unverifiedUser() throws Exception {
        configureSimplePlanetNoResources();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -1);
        gregorianCalendar.add(GregorianCalendar.SECOND, -10);

        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("U1", "xxx", "xxx", "fake");
        User user = userService.getUser();
        user.setAwaitingVerification();
        setPrivateField(User.class, user, "awaitingVerificationDate", gregorianCalendar.getTime());
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_HARVESTER_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        assertWholeItemCount(TEST_PLANET_1_ID, 3);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        List<Base> oldBases = serverPlanetServices.getBaseService().getBases();
        List<UserState> oldUserStates = userService.getAllUserStates();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(1, 3, 1, 1);
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<Base> newBases = serverPlanetServices.getBaseService().getBases();
        List<UserState> newUserStates = userService.getAllUserStates();
        Assert.assertEquals(1, newBases.size());
        Assert.assertEquals(1, newUserStates.size());
        verifyUserStates(newUserStates, oldUserStates);
        verifyBases(newBases, oldBases);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Remove unverified users
        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 100);
        ((RegisterServiceImpl) deAopProxy(registerService)).cleanup();
        ((RegisterServiceImpl) deAopProxy(registerService)).init();
        Thread.sleep(200);
        setPrivateStaticField(RegisterServiceImpl.class, "CLEANUP_DELAY", 1 * ClientDateUtil.MILLIS_IN_DAY);

        oldBases = serverPlanetServices.getBaseService().getBases();
        oldUserStates = userService.getAllUserStates();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        assertBackupSummery(2, 3, 1, 0);
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        newBases = serverPlanetServices.getBaseService().getBases();
        newUserStates = userService.getAllUserStates();
        Assert.assertEquals(1, newBases.size());
        Assert.assertEquals(0, newUserStates.size());
        verifyUserStates(newUserStates, oldUserStates);
        verifyBases(newBases, oldBases);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore before user has been cleared
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(1).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        newBases = serverPlanetServices.getBaseService().getBases();
        newUserStates = userService.getAllUserStates();
        Assert.assertEquals(1, newBases.size());
        Assert.assertEquals(0, newUserStates.size());
        verifyUserStates(newUserStates, oldUserStates);
        verifyBases(newBases, oldBases);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testLocale() throws Exception {
        configureSimplePlanetNoResources();

        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.FRANCE);
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // U2 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.ENGLISH);
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // U3 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getMockHttpServletRequest().addPreferredLocale(Locale.GERMAN);
        createAndLoginUser("U3");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Backup
        backupAndRestore();

        Assert.assertEquals(Locale.FRANCE, userService.getUserState(userService.getUser("U1")).getLocale());
        Assert.assertEquals(Locale.ENGLISH, userService.getUserState(userService.getUser("U2")).getLocale());
        Assert.assertEquals(Locale.GERMAN, userService.getUserState(userService.getUser("U3")).getLocale());
    }

    @Test
    @DirtiesContext
    public void testUnlockedItem() throws Exception {
        configureSimplePlanetNoResources();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType attacker = serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        attacker.setUnlockCrystals(10);
        serverItemTypeService.saveDbItemType(attacker);
        DbBaseItemType factory = serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID);
        factory.setUnlockCrystals(8);
        serverItemTypeService.saveDbItemType(factory);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        getUserState().setCrystals(100);
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2 & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        createBase(new Index(2000, 200));
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        getUserState().setCrystals(100);
        unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setCrystals(100);
        createBase(new Index(3000, 3000)); // Create base
        unlockService.unlockItemType(TEST_ATTACK_ITEM_ID);
        unlockService.unlockItemType(TEST_FACTORY_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertFalse(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore before user unlocked something
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(5).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        createBase(new Index(5000, 5000));
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), getOrCreateBase()));
        Assert.assertTrue(unlockService.isItemLocked((BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID), getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testUnlockedQuest() throws Exception {
        configureSimplePlanetNoResources();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask1 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask1.setUnlockCrystals(10);
        setupCondition(dbLevelTask1);
        DbLevelTask dbLevelTask2 = dbLevel.getLevelTaskCrud().createDbChild();
        dbLevelTask2.setUnlockCrystals(20);
        setupCondition(dbLevelTask2);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        userGuidanceService.activateLevels();
        QuestInfo questInfo1 = dbLevelTask1.createQuestInfo(Locale.ENGLISH);
        QuestInfo questInfo2 = dbLevelTask2.createQuestInfo(Locale.ENGLISH);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        getUserState().setCrystals(100);
        Assert.assertTrue(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        unlockService.unlockQuest(dbLevelTask1.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        unlockService.unlockQuest(dbLevelTask2.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2 & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        createBase(new Index(2000, 2000));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        getUserState().setCrystals(100);
        unlockService.unlockQuest(dbLevelTask1.getId());
        unlockService.unlockQuest(dbLevelTask2.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setCrystals(100);
        createBase(new Index(3000, 3000)); // Create base
        unlockService.unlockQuest(dbLevelTask1.getId());
        unlockService.unlockQuest(dbLevelTask2.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        Assert.assertFalse(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertFalse(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore before user unlocked something
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(5).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        createBase(new Index(4000, 4000));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo1, getOrCreateBase()));
        Assert.assertTrue(unlockService.isQuestLocked(questInfo2, getOrCreateBase()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    @Test
    @DirtiesContext
    public void testUnlockedPlanet() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Prepare
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PlanetLiteInfo planetInfo1 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID).createPlanetInfo().getPlanetLiteInfo();
        DbPlanet dbPlanet2 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_2_ID);
        dbPlanet2.setUnlockCrystals(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet2);
        planetSystemService.deactivatePlanet(TEST_PLANET_2_ID);
        planetSystemService.activatePlanet(TEST_PLANET_2_ID);
        PlanetLiteInfo planetInfo2 = dbPlanet2.createPlanetInfo().getPlanetLiteInfo();
        DbPlanet dbPlanet3 = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_3_ID);
        dbPlanet3.setUnlockCrystals(15);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet3);
        planetSystemService.deactivatePlanet(TEST_PLANET_3_ID);
        planetSystemService.activatePlanet(TEST_PLANET_3_ID);
        PlanetLiteInfo planetInfo3 = dbPlanet3.createPlanetInfo().getPlanetLiteInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // U1 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL);
        getOrCreateBase(); // Create base
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        getUserState().setCrystals(100);
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        unlockService.unlockPlanet(planetInfo2.getPlanetId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        unlockService.unlockPlanet(planetInfo3.getPlanetId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // U2 reg user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL);
        createBase(new Index(2000, 2000)); // Create base
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2 & modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        getUserState().setCrystals(100);
        unlockService.unlockPlanet(planetInfo2.getPlanetId());
        unlockService.unlockPlanet(planetInfo3.getPlanetId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Unregistered user
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        getUserState().setCrystals(100);
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL);
        createBase(new Index(3000, 3000)); // Create base
        unlockService.unlockPlanet(planetInfo2.getPlanetId());
        unlockService.unlockPlanet(planetInfo3.getPlanetId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify U2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertFalse(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Restore before user unlocked something
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(5).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify U1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo2, getUserState()));
        Assert.assertTrue(unlockService.isPlanetLocked(planetInfo3, getUserState()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

    private void setupCondition(DbLevelTask dbLevelTask1) {
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbConditionConfig.setConditionTrigger(ConditionTrigger.MONEY_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(3);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        dbLevelTask1.setDbConditionConfig(dbConditionConfig);
    }

    @Test
    @DirtiesContext
    public void testEnergy() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        // Build consumer
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_CONSUMER_TYPE_ID);
        waitForActionServiceDone();
        ServerEnergyService serverEnergyService = planetSystemService.getServerPlanetServices().getEnergyService();
        Assert.assertEquals(20, serverEnergyService.getConsuming());
        Assert.assertEquals(0, serverEnergyService.getGenerating());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        // Verify and build generator
        serverEnergyService = planetSystemService.getServerPlanetServices().getEnergyService();
        Assert.assertEquals(20, serverEnergyService.getConsuming());
        Assert.assertEquals(0, serverEnergyService.getGenerating());
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_GENERATOR_TYPE_ID);
        waitForActionServiceDone();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        // Verify and sell consumer
        serverEnergyService = planetSystemService.getServerPlanetServices().getEnergyService();
        Assert.assertEquals(20, serverEnergyService.getConsuming());
        Assert.assertEquals(30, serverEnergyService.getGenerating());
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_CONSUMER_TYPE_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        // Verify and sell generator
        serverEnergyService = planetSystemService.getServerPlanetServices().getEnergyService();
        Assert.assertEquals(0, serverEnergyService.getConsuming());
        Assert.assertEquals(30, serverEnergyService.getGenerating());
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(getOrCreateBase(), TEST_GENERATOR_TYPE_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        // Verify
        serverEnergyService = planetSystemService.getServerPlanetServices().getEnergyService();
        Assert.assertEquals(0, serverEnergyService.getConsuming());
        Assert.assertEquals(0, serverEnergyService.getGenerating());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void backupAndRestore() throws NoSuchItemTypeException {
        // Backup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Restore
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void verifyUserStates(List<UserState> newUserStates, List<UserState> oldUserStates) {
        Assert.assertEquals(oldUserStates.size(), newUserStates.size());
        for (UserState oldUserState : oldUserStates) {
            Integer oldUser = oldUserState.getUser();
            UserState newUserState = findUserState(oldUserState, newUserStates);
            Integer newUser = newUserState.getUser();
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
        Integer oldUsr = oldUserState.getUser();
        for (UserState newUserState : newUserStates) {
            Integer newUsr = newUserState.getUser();
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
        // backupService.backup();
        // List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        //  backupService.restore(backupSummaries.get(0).getDate());
    }

    // @Test

    public void testBackupSummary() {
        backupService.getBackupSummary();
    }

    // @Test

    public void testRestore() throws NoSuchItemTypeException {
        List<BackupSummary> backupSummaries = backupService.getBackupSummary();
        backupService.restore(backupSummaries.get(0).getDate());
    }

    // @Test

    public void testBigBackup() throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID);
        for (int i = 0; i < ITEM_COUNT; i++) {
            ItemType itemType = getRandomItemType();
            System.out.println("Creating: " + (i + 1) + " of " + ITEM_COUNT);
            serverPlanetServices.getItemService().createSyncObject(itemType, getRandomPosition(itemType), null, getBase(itemType));
        }
        backupService.backup();
    }

    private SimpleBase getBase(ItemType itemType) throws NoSuchItemTypeException {
        if (itemType instanceof ResourceType) {
            return null;
        } else {
            throw new IllegalArgumentException("Unknown itemType: " + itemType);
        }
    }

    private Index getRandomPosition(ItemType itemType) {
        Assert.fail();
        return null;
        // TODO Rectangle rectangle = new Rectangle(0, 0, terrainService.getTerrainSettings().getPlayFieldXSize(), terrainService.getTerrainSettings().getPlayFieldYSize());
        // TODO return collisionService.getFreeRandomPosition(itemType, rectangle, 200, true, false);
    }

    public ItemType getRandomItemType() {
        int index = (int) (Math.random() * serverItemTypeService.getItemTypes().size());
        return serverItemTypeService.getItemTypes().get(index);
    }

    @Test
    @DirtiesContext
    public void saveStorablePacket() throws Exception {
        configureSimplePlanet();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        StorablePacket storablePacketU1 = new StorablePacket();
        storablePacketU1.setType(StorablePacket.Type.GUILD_LOST);
        UserState userStateU1 = getUserState();
        planetSystemService.sendPacket(userStateU1, storablePacketU1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        UserState userStateR1 = getUserState();
        Assert.assertEquals(1, userStateR1.getStorablePackets().size());
        StorablePacket storablePacketR1 = CommonJava.getFirst(userStateR1.getStorablePackets());
        Assert.assertEquals(storablePacketU1.getType(), storablePacketR1.getType());
        Assert.assertFalse(System.identityHashCode(userStateU1) == System.identityHashCode(userStateR1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U2");
        StorablePacket storablePacketU2 = new StorablePacket();
        storablePacketU2.setType(StorablePacket.Type.GUILD_LOST);
        UserState userStateU2 = getUserState();
        planetSystemService.sendPacket(userStateU2, storablePacketU2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        backupAndRestore();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U2");
        UserState userStateR2 = getUserState();
        Assert.assertEquals(1, userStateR2.getStorablePackets().size());
        StorablePacket storablePacketR2 = CommonJava.getFirst(userStateR2.getStorablePackets());
        Assert.assertEquals(storablePacketU2.getType(), storablePacketR2.getType());
        Assert.assertFalse(System.identityHashCode(storablePacketU2) == System.identityHashCode(storablePacketR2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}