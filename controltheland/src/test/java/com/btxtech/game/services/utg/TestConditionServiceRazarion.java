package com.btxtech.game.services.utg;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.utg.ConditionServiceListener;
import com.btxtech.game.jsre.common.utg.config.ConditionConfig;
import com.btxtech.game.jsre.common.utg.config.ConditionTrigger;
import com.btxtech.game.jsre.common.utg.config.CountComparisonConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.finance.FinanceService;
import com.btxtech.game.services.finance.impl.FinanceServiceImpl;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.inventory.impl.GlobalInventoryServiceImpl;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemTypePossibility;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.mgmt.BackupSummary;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.user.InvitationService;
import com.btxtech.game.services.user.RegisterService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.user.impl.InvitationServiceImpl;
import com.btxtech.game.services.utg.condition.DbConditionConfig;
import com.btxtech.game.services.utg.condition.DbCountComparisonConfig;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import com.btxtech.game.wicket.pages.InvitationStart;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 08.09.13
 * Time: 17:15
 */
public class TestConditionServiceRazarion extends AbstractServiceTest {
    @Autowired
    private FinanceService financeService;
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private UserService userService;
    private boolean passed;
    private UserState userState;

    @Test
    @DirtiesContext
    public void checkCallFinanceService() throws Exception {
        configureSimplePlanetNoResources();

        ServerConditionService serverConditionServiceMock = EasyMock.createStrictMock(ServerConditionService.class);
        serverConditionServiceMock.onRazarionIncreased(createUserStateMatcher("U1"), EasyMock.eq(false), EasyMock.eq(100));
        serverConditionServiceMock.onRazarionIncreased(createUserStateMatcher("U1"), EasyMock.eq(false), EasyMock.eq(1000));
        EasyMock.replay(serverConditionServiceMock);
        setPrivateField(FinanceServiceImpl.class, financeService, "serverConditionService", serverConditionServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        String userIdString = Integer.toString(getUserId());
        financeService.razarionBought(100, getUserState());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        financeService.razarionBought(userIdString, "RAZ1000", "5", "USD", "1", "payer email", "finance@razarion.com", "Completed", "1");
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConditionServiceMock);
    }

    @Test
    @DirtiesContext
    public void checkCallBoxPicket() throws Exception {
        configureSimplePlanetNoResources();

        // Setup box
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBoxItemType dbBoxItemType = (DbBoxItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBoxItemType.class);
        dbBoxItemType.setTerrainType(TerrainType.LAND);
        setupImages(dbBoxItemType, 1);
        dbBoxItemType.setBounding(new BoundingBox(80, ANGELS_1));
        dbBoxItemType.setTtl(5000);
        DbBoxItemTypePossibility dbBoxItemTypePossibility1 = dbBoxItemType.getBoxPossibilityCrud().createDbChild();
        dbBoxItemTypePossibility1.setPossibility(1.0);
        dbBoxItemTypePossibility1.setRazarion(99);
        serverItemTypeService.saveDbItemType(dbBoxItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ServerConditionService serverConditionServiceMock = EasyMock.createStrictMock(ServerConditionService.class);
        serverConditionServiceMock.onRazarionIncreased(createUserStateMatcher("qaywsx"), EasyMock.eq(true), EasyMock.eq(99));
        EasyMock.replay(serverConditionServiceMock);
        setPrivateField(GlobalInventoryServiceImpl.class, globalInventoryService, "serverConditionService", serverConditionServiceMock);

        ServerItemService serverItemService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("qaywsx");
        createBase(new Index(1000, 1000));
        SyncBaseItem bulldozer = (SyncBaseItem) serverItemService.getItem(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID));
        SyncBoxItem syncBoxItem = (SyncBoxItem) serverItemService.createSyncObject(serverItemTypeService.getDbItemTypeCrud().readDbChild(dbBoxItemType.getId()).createItemType(), new Index(2000, 2000), null, null);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        globalInventoryService.onSyncBoxItemPicked(syncBoxItem, bulldozer);

        EasyMock.verify(serverConditionServiceMock);
    }

    @Test
    @DirtiesContext
    public void checkCallFriendInvitation() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Preparation
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("Host");
        int hostId = getUserId();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        dbLevel.setFriendInvitationBonus(29);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        ServerConditionService serverConditionServiceMock = EasyMock.createStrictMock(ServerConditionService.class);
        serverConditionServiceMock.onRazarionIncreased(createUserStateMatcher("Host"), EasyMock.eq(false), EasyMock.eq(29));
        EasyMock.replay(serverConditionServiceMock);
        setPrivateField(InvitationServiceImpl.class, invitationService, "serverConditionService", serverConditionServiceMock);

        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter("/game_cms_invitation/?user=" + hostId + "&type=mail");
        getWicketTester().startPage(InvitationStart.class);
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.register("Invitee", "xxx", "xxx", "");
        String verificationId = getUser().getVerificationId();
        endHttpRequestAndOpenSessionInViewFilter();
        beginHttpRequestAndOpenSessionInViewFilter();
        registerService.onVerificationPageCalled(verificationId);
        loginUser("Invitee");
        userGuidanceService.promote(getUserState(), TEST_LEVEL_2_REAL_ID);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        EasyMock.verify(serverConditionServiceMock);
    }

    @Test
    @DirtiesContext
    public void testConditionServiceNoPlanetInteraction() throws Exception {
        configureSimplePlanetNoResources();
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.RAZARION_INCREASED, new CountComparisonConfig(20), null, null, false);
        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        serverConditionService.activateCondition(conditionConfig, userState, 1);
        passed = false;

        Assert.assertFalse(passed);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Test inside a web request
        serverConditionService.onRazarionIncreased(userState, false, 1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Assert.assertFalse(passed);
        serverConditionService.onRazarionIncreased(userState, false, 18);
        Assert.assertFalse(passed);
        serverConditionService.onRazarionIncreased(userState, false, 2);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void testConditionServicePlanetInteraction() throws Exception {
        configureSimplePlanetNoResources();
        ConditionConfig conditionConfig = new ConditionConfig(ConditionTrigger.RAZARION_INCREASED, new CountComparisonConfig(20), null, null, false);
        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        serverConditionService.activateCondition(conditionConfig, userState, 1);
        passed = false;

        Assert.assertFalse(passed);
        serverConditionService.onRazarionIncreased(userState, true, 22);
        Assert.assertFalse(passed);
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1");
        createBase(new Index(1000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        serverConditionService.onRazarionIncreased(userState, true, 22);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void testConditionServiceLevelTask() throws Exception {
        configureSimplePlanetNoResources();
        // setup level task for razarion
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        dbConditionConfig.setConditionTrigger(ConditionTrigger.RAZARION_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(30);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals(userState, actor);
                Assert.assertEquals(1, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertFalse(passed);
        serverConditionService.onRazarionIncreased(userState, false, 15);
        Assert.assertFalse(passed);
        serverConditionService.onRazarionIncreased(userState, false, 15);
        Assert.assertTrue(passed);
    }

    @Test
    @DirtiesContext
    public void testConditionServiceLevelTaskAndBackupRestore() throws Exception {
        configureSimplePlanetNoResources();
        // setup level task for razarion
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbLevel dbLevel = userGuidanceService.getDbLevelCrud().readDbChild(TEST_LEVEL_2_REAL_ID);
        DbLevelTask dbLevelTask = dbLevel.getLevelTaskCrud().createDbChild();
        DbConditionConfig dbConditionConfig = new DbConditionConfig();
        dbLevelTask.setDbConditionConfig(dbConditionConfig);
        dbConditionConfig.setConditionTrigger(ConditionTrigger.RAZARION_INCREASED);
        DbCountComparisonConfig dbCountComparisonConfig = new DbCountComparisonConfig();
        dbCountComparisonConfig.setCount(30);
        dbConditionConfig.setDbAbstractComparisonConfig(dbCountComparisonConfig);
        userGuidanceService.getDbLevelCrud().updateDbChild(dbLevel);
        final int levelTaskId = dbLevelTask.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        passed = false;
        serverConditionService.setConditionServiceListener(new ConditionServiceListener<UserState, Integer>() {
            @Override
            public void conditionPassed(UserState actor, Integer identifier) {
                Assert.assertEquals("U1", userService.getUserName(actor));
                Assert.assertEquals(levelTaskId, (int) identifier);
                passed = true;
            }
        });

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userState = getUserState();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertFalse(passed);
        serverConditionService.onRazarionIncreased(userState, false, 15);
        Assert.assertFalse(passed);
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
        assertBackupSummery(1, 0, 0, 1);
        backupService.restore(backupSummaries.get(0).getDate());
        UserState newUserState = userService.getUserState(userService.getUser("U1"));
        Assert.assertNotSame(userState, newUserState);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        Assert.assertFalse(passed);
        serverConditionService.onRazarionIncreased(newUserState, false, 15);
        Assert.assertTrue(passed);
    }
}