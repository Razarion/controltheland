package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.common.info.StartPointInfo;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapPlanetInfo;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.packets.AccountBalancePacket;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.packets.BaseLostPacket;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestPlanetHelper;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.PropertyService;
import com.btxtech.game.services.common.PropertyServiceEnum;
import com.btxtech.game.services.common.TestGlobalServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: 07.04.2011
 * Time: 13:28:45
 */
public class TestBaseService extends AbstractServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private PropertyService propertyService;

    @Test
    @DirtiesContext
    public void testSellBaseItem() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        createAndLoginUser("U1");
        // $1000
        SimpleBase simpleBase = getOrCreateBase(); // Setup connection & create two account balance package
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        sendBuildCommand(id, new Index(400, 400), TEST_FACTORY_ITEM_ID);
        // $998
        waitForActionServiceDone();
        Assert.assertEquals(998, baseService.getBase(simpleBase).getAccountBalance(), 0.1);
        clearPackets();
        getMovableService().sellItem(START_UID_1, id);
        // $999
        AccountBalancePacket accountBalancePacket = new AccountBalancePacket();
        accountBalancePacket.setAccountBalance(998.5);
        assertPackagesIgnoreSyncItemInfoAndClear(accountBalancePacket/*, levelStatePacket*/);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSellLastBaseItem() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertNull(userService.getUser());
        createAndLoginUser("U1");
        SimpleBase simpleBase = getOrCreateBase(); // Setup connection
        Id id = getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID);
        clearPackets();
        getMovableService().sellItem(START_UID_1, id);

        Assert.assertNotNull(getMovableService().getRealGameInfo(START_UID_1, null).getStartPointInfo());
        Assert.assertNotNull(getMovableService().getSyncInfo(START_UID_1, false));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testGetBaseItems() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        SimpleBase simpleBase = getOrCreateBase(); // Setup connection
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        // TODO failed on 02.07.2012
        Assert.assertEquals(2, baseService.getBase().getItemCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        Assert.assertEquals(2, baseService.getBase().getItemCount());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void surrenderBase() throws Exception {
        configureSimplePlanetNoResources();
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getOrCreateBase(); // Setup connection
        sendBuildCommand(getFirstSynItemId(simpleBase, TEST_START_BUILDER_ITEM_ID), new Index(100, 100), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        Assert.assertEquals(2, baseService.getBase().getItemCount());
        assertWholeItemCount(TEST_PLANET_1_ID, 2);
        Assert.assertFalse(baseService.getBase(simpleBase).isAbandoned());
        baseService.surrenderBase(baseService.getBase(simpleBase));
        Assert.assertFalse(baseService.isAlive(simpleBase));
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void houseSpace() throws Exception {
        configureSimplePlanetNoResources();
        BaseItemType builderType = (BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID);
        BaseItemType factoryType = (BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID);
        BaseItemType houseType = (BaseItemType) serverItemTypeService.getItemType(TEST_HOUSE_ID);
        BaseItemType attackType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        SimpleBase simpleBase = getOrCreateBase(); // Setup connection
        BaseService baseService = planetSystemService.getServerPlanetServices().getBaseService();
        Assert.assertEquals(1, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(0, baseService.getHouseSpace(simpleBase));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, builderType, 19));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, builderType, 20));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 9));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 11));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, houseType, 100));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, attackType, 9));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, attackType, 11));
        // Build
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        Assert.assertEquals(3, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(0, baseService.getHouseSpace(simpleBase));
        // Build
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1500, 1500), TEST_HOUSE_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        assertWholeItemCount(TEST_PLANET_1_ID, 3);
        Assert.assertEquals(3, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(10, baseService.getHouseSpace(simpleBase));
        // Fabricate
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        Assert.assertEquals(5, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(10, baseService.getHouseSpace(simpleBase));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, builderType, 25));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, builderType, 26));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 12));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 13));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, houseType, 100));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, attackType, 12));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, attackType, 13));
        // Sell
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(TEST_FACTORY_ITEM_ID));
        assertWholeItemCount(TEST_PLANET_1_ID, 3);
        Assert.assertEquals(3, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(10, baseService.getHouseSpace(simpleBase));
        // Sell
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(TEST_ATTACK_ITEM_ID));
        assertWholeItemCount(TEST_PLANET_1_ID, 2);
        Assert.assertEquals(1, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(10, baseService.getHouseSpace(simpleBase));
        // Sell
        getMovableService().sellItem(START_UID_1, getFirstSynItemId(TEST_HOUSE_ID));
        assertWholeItemCount(TEST_PLANET_1_ID, 1);
        Assert.assertEquals(1, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(0, baseService.getHouseSpace(simpleBase));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, builderType, 19));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, builderType, 20));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 9));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 11));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, houseType, 100));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, attackType, 9));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, attackType, 11));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void houseSpaceRestore() throws Exception {
        configureSimplePlanetNoResources();
        BaseItemType builderType = (BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID);
        BaseItemType factoryType = (BaseItemType) serverItemTypeService.getItemType(TEST_FACTORY_ITEM_ID);
        BaseItemType houseType = (BaseItemType) serverItemTypeService.getItemType(TEST_HOUSE_ID);
        BaseItemType attackType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1000, 1000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(1500, 1500), TEST_HOUSE_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone(TEST_PLANET_1_ID);
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.restore(backupService.getBackupSummary().get(0).getDate());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "test");
        SimpleBase simpleBase = getOrCreateBase(); // Setup connection
        BaseService baseService = planetSystemService.getServerPlanetServices().getBaseService();
        Assert.assertEquals(5, baseService.getUsedHouseSpace(simpleBase));
        Assert.assertEquals(10, baseService.getHouseSpace(simpleBase));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, builderType, 25));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, builderType, 26));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 12));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, factoryType, 13));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, houseType, 100));
        Assert.assertFalse(baseService.isHouseSpaceExceeded(simpleBase, attackType, 12));
        Assert.assertTrue(baseService.isHouseSpaceExceeded(simpleBase, attackType, 13));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void depositResource() throws Exception {
        TestPlanetHelper testPlanetHelper = new TestPlanetHelper();
        ServerPlanetServicesImpl serverPlanetServices = new ServerPlanetServicesImpl();
        PlanetInfo planetInfo = new PlanetInfo();
        planetInfo.setPlanetIdAndName(1, "TestPlanet", null);
        planetInfo.setMaxMoney(10);
        serverPlanetServices.setPlanetInfo(planetInfo);
        testPlanetHelper.setServerPlanetServices(serverPlanetServices);

        Base testBase = new Base(new UserState(), testPlanetHelper, 1);

        ServerConditionService serverConditionServiceMock = EasyMock.createNiceMock(ServerConditionService.class);
        EasyMock.replay(serverConditionServiceMock);

        TestGlobalServices testGlobalServices = new TestGlobalServices();
        testGlobalServices.setServerConditionService(serverConditionServiceMock);
        testGlobalServices.setGuildService(guildService);

        BaseServiceImpl baseService = new BaseServiceImpl(testPlanetHelper);
        baseService.init(serverPlanetServices, testGlobalServices);
        baseService.restore(Collections.singletonList(testBase));

        // Start Test
        Assert.assertEquals(0.0, testBase.getAccountBalance());

        baseService.depositResource(1.0, testBase.getSimpleBase());
        Assert.assertEquals(1.0, testBase.getAccountBalance());
        baseService.depositResource(9.0, testBase.getSimpleBase());
        Assert.assertEquals(10.0, testBase.getAccountBalance());

        testBase.setAccountBalance(0);
        baseService.depositResource(20.0, testBase.getSimpleBase());
        Assert.assertEquals(10.0, testBase.getAccountBalance());

        testBase.setAccountBalance(10.0);
        baseService.depositResource(5.0, testBase.getSimpleBase());
        Assert.assertEquals(10.0, testBase.getAccountBalance());

        testBase.setAccountBalance(11.0);
        baseService.depositResource(1.0, testBase.getSimpleBase());
        Assert.assertEquals(10.0, testBase.getAccountBalance());

    }

    @Test
    @DirtiesContext
    public void fillGuildForFakeBasesAloneUnreg() throws Exception {
        configureMultiplePlanetsAndLevels();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase ownFakeBase = new SimpleBase(SimpleBase.FAKE_BASE_START_POINT, TEST_PLANET_1_ID);
        Collection<BaseAttributes> allBaseAttributes = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().createAllBaseAttributes4FakeBase(ownFakeBase, getUserState(), TEST_PLANET_1_ID);
        Assert.assertEquals(1, allBaseAttributes.size());
        BaseAttributes baseAttributes = CommonJava.getFirst(allBaseAttributes);
        Assert.assertEquals(ownFakeBase, baseAttributes.getSimpleBase());
        Assert.assertEquals("Your Base", baseAttributes.getName());
        Assert.assertFalse(baseAttributes.isBot());
        Assert.assertNull(baseAttributes.getSimpleGuild());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void fillGuildForFakeBasesAloneReg() throws Exception {
        configureMultiplePlanetsAndLevels();
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase ownFakeBase = new SimpleBase(SimpleBase.FAKE_BASE_START_POINT, TEST_PLANET_1_ID);
        Collection<BaseAttributes> allBaseAttributes = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().createAllBaseAttributes4FakeBase(ownFakeBase, getUserState(), TEST_PLANET_1_ID);
        Assert.assertEquals(1, allBaseAttributes.size());
        BaseAttributes baseAttributes = CommonJava.getFirst(allBaseAttributes);
        Assert.assertEquals(ownFakeBase, baseAttributes.getSimpleBase());
        Assert.assertEquals("U1", baseAttributes.getName());
        Assert.assertFalse(baseAttributes.isBot());
        Assert.assertNull(baseAttributes.getSimpleGuild());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void fillGuildForFakeBasesAloneRegBot() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig = setupMinimalNoAttackBot(TEST_PLANET_1_ID, new Rectangle(0, 0, 5000, 5000));
        waitForBotToBuildup(TEST_PLANET_1_ID, dbBotConfig.createBotConfig(serverItemTypeService));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase ownFakeBase = new SimpleBase(SimpleBase.FAKE_BASE_START_POINT, TEST_PLANET_1_ID);
        Collection<BaseAttributes> allBaseAttributes = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().createAllBaseAttributes4FakeBase(ownFakeBase, getUserState(), TEST_PLANET_1_ID);
        Assert.assertEquals(2, allBaseAttributes.size());
        for (BaseAttributes baseAttributes : allBaseAttributes) {
            if (baseAttributes.getSimpleBase().equals(ownFakeBase)) {
                Assert.assertEquals("U1", baseAttributes.getName());
                Assert.assertFalse(baseAttributes.isBot());
                Assert.assertNull(baseAttributes.getSimpleGuild());
            } else {
                Assert.assertTrue(baseAttributes.isBot());
                Assert.assertNull(baseAttributes.getSimpleGuild());
            }
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void fillGuildForFakeBasesUnreg() throws Exception {
        configureMultiplePlanetsAndLevels();

        for (int i = 0; i < 10; i++) {
            createOtherBase(new Index(500, 500 * (i + 1)), "user" + i, null);
        }

        for (int i = 0; i < 10; i++) {
            createOtherBase(new Index(1000, 500 * (i + 1)), null, null);
        }

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase ownFakeBase = new SimpleBase(SimpleBase.FAKE_BASE_START_POINT, TEST_PLANET_1_ID);
        Collection<BaseAttributes> allBaseAttributes = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().createAllBaseAttributes4FakeBase(ownFakeBase, getUserState(), TEST_PLANET_1_ID);
        Assert.assertEquals(21, allBaseAttributes.size());
        BaseAttributes baseAttributes = getOwnBase(allBaseAttributes, ownFakeBase);
        Assert.assertEquals(ownFakeBase, baseAttributes.getSimpleBase());
        Assert.assertEquals("Your Base", baseAttributes.getName());
        Assert.assertFalse(baseAttributes.isBot());
        Assert.assertNull(baseAttributes.getSimpleGuild());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void fillGuildForFakeBasesReg() throws Exception {
        configureMultiplePlanetsAndLevels();

        for (int i = 0; i < 10; i++) {
            createOtherBase(new Index(500, 500 * (i + 1)), "user" + i, null);
        }

        for (int i = 0; i < 10; i++) {
            createOtherBase(new Index(1000, 500 * (i + 1)), null, null);
        }

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        SimpleBase ownFakeBase = new SimpleBase(SimpleBase.FAKE_BASE_START_POINT, TEST_PLANET_1_ID);
        Collection<BaseAttributes> allBaseAttributes = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().createAllBaseAttributes4FakeBase(ownFakeBase, getUserState(), TEST_PLANET_1_ID);
        Assert.assertEquals(21, allBaseAttributes.size());
        BaseAttributes baseAttributes = getOwnBase(allBaseAttributes, ownFakeBase);
        Assert.assertEquals(ownFakeBase, baseAttributes.getSimpleBase());
        Assert.assertEquals("U1", baseAttributes.getName());
        Assert.assertFalse(baseAttributes.isBot());
        Assert.assertNull(baseAttributes.getSimpleGuild());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void fillGuildForFakeBasesRegGuilds() throws Exception {
        configureMultiplePlanetsAndLevels();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("president");
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        propertyService.createProperty(PropertyServiceEnum.GUILD_CRYSTAL_COST, 0);
        Integer guildId = guildService.createGuild("xxxx").getId();
        createBase(new Index(3000, 1000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        for (int i = 0; i < 10; i++) {
            createOtherBase(new Index(500, 500 * (i + 1)), "user" + i, null);
        }

        for (int i = 0; i < 10; i++) {
            createOtherBase(new Index(1000, 500 * (i + 1)), null, null);
        }

        for (int i = 0; i < 10; i++) {
            createOtherBase(new Index(1500, 500 * (i + 1)), "user1" + i, guildId);
        }

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("president");
        getMovableService().surrenderBase();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("president");
        SimpleBase ownFakeBase = new SimpleBase(SimpleBase.FAKE_BASE_START_POINT, TEST_PLANET_1_ID);
        Collection<BaseAttributes> allBaseAttributes = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService().createAllBaseAttributes4FakeBase(ownFakeBase, getUserState(), TEST_PLANET_1_ID);
        Assert.assertEquals(31, allBaseAttributes.size());
        BaseAttributes baseAttributes = getOwnBase(allBaseAttributes, ownFakeBase);
        Assert.assertEquals(ownFakeBase, baseAttributes.getSimpleBase());
        Assert.assertEquals("president", baseAttributes.getName());
        Assert.assertFalse(baseAttributes.isBot());
        Assert.assertEquals((int) guildId, baseAttributes.getSimpleGuild().getId());
        checkGuild(baseAttributes, allBaseAttributes);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void checkGuild(BaseAttributes myBaseAttributes, Collection<BaseAttributes> allBaseAttributes) {
        int count = 0;
        for (BaseAttributes baseAttributes : allBaseAttributes) {
            if (baseAttributes.getSimpleBase().equals(myBaseAttributes.getSimpleBase())) {
                continue;
            }
            if (baseAttributes.getSimpleGuild() != null) {
                Assert.assertEquals(myBaseAttributes.getSimpleGuild(), baseAttributes.getSimpleGuild());
                count++;
            }
        }
        Assert.assertEquals(10, count);
    }

    private BaseAttributes getOwnBase(Collection<BaseAttributes> allBaseAttributes, SimpleBase ownFakeBase) {
        for (BaseAttributes allBaseAttribute : allBaseAttributes) {
            if (allBaseAttribute.getSimpleBase().equals(ownFakeBase)) {
                return allBaseAttribute;
            }
        }
        throw new IllegalArgumentException("Own base not found: " + ownFakeBase);
    }

    private void createOtherBase(Index startPoint, String userName, Integer guildId) throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        if (userName != null) {
            createAndLoginUser(userName);
        }
        userGuidanceService.promote(userService.getUserState(), TEST_LEVEL_2_REAL_ID);
        createBase(startPoint);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        if (guildId != null) {
            beginHttpSession();
            beginHttpRequestAndOpenSessionInViewFilter();
            loginUser("president");
            guildService.inviteUserToGuild(userName);
            endHttpRequestAndOpenSessionInViewFilter();
            endHttpSession();
            beginHttpSession();
            beginHttpRequestAndOpenSessionInViewFilter();
            loginUser(userName);
            guildService.joinGuild(guildId);
            endHttpRequestAndOpenSessionInViewFilter();
            endHttpSession();
        }
    }

    @Test
    @DirtiesContext
    public void fillBaseStatistics() throws Exception {
        configureMultiplePlanetsAndLevels();
        // Preparation planet 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupMinimalNoAttackBot(TEST_PLANET_1_ID, new Rectangle(0, 0, 500, 500));
        setupMinimalNoAttackBot(TEST_PLANET_1_ID, new Rectangle(0, 0, 500, 500));
        setupMinimalNoAttackBot(TEST_PLANET_1_ID, new Rectangle(0, 0, 500, 500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        createBase(TEST_LEVEL_2_REAL_ID, new Index(1000, 1000));
        createBase(TEST_LEVEL_2_REAL_ID, new Index(2000, 1000));
        createBase(TEST_LEVEL_2_REAL_ID, new Index(3000, 1000));
        createBase(TEST_LEVEL_2_REAL_ID, new Index(4000, 1000));
        // Preparation planet 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupMinimalNoAttackBot(TEST_PLANET_2_ID, new Rectangle(0, 0, 500, 500));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        createBase(TEST_LEVEL_5_REAL, new Index(5000, 1000));
        createBase(TEST_LEVEL_5_REAL, new Index(5000, 2000));

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Verify planet 1
        BaseService baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBaseService();
        StarMapPlanetInfo starMapPlanetInfo = new StarMapPlanetInfo();
        baseService.fillBaseStatistics(starMapPlanetInfo);
        Assert.assertEquals(4, starMapPlanetInfo.getBases());
        Assert.assertEquals(3, starMapPlanetInfo.getBots());
        // Verify planet 2
        baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_2_ID).getBaseService();
        starMapPlanetInfo = new StarMapPlanetInfo();
        baseService.fillBaseStatistics(starMapPlanetInfo);
        Assert.assertEquals(2, starMapPlanetInfo.getBases());
        Assert.assertEquals(1, starMapPlanetInfo.getBots());
        // Verify planet 3
        baseService = planetSystemService.getServerPlanetServices(TEST_PLANET_3_ID).getBaseService();
        starMapPlanetInfo = new StarMapPlanetInfo();
        baseService.fillBaseStatistics(starMapPlanetInfo);
        Assert.assertEquals(0, starMapPlanetInfo.getBases());
        Assert.assertEquals(0, starMapPlanetInfo.getBots());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void createBase(int levelId, Index startPoint) {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        userGuidanceService.promote(getUserState(), levelId);
        createBase(startPoint);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}