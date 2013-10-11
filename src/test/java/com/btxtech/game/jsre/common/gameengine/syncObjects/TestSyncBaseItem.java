package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.PositionInBotException;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.SimpleGuild;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.user.UserState;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 23.04.12
 * Time: 17:12
 */
public class TestSyncBaseItem extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private GlobalServices globalServices;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void isEnemy1() throws Exception {
        configureSimplePlanetNoResources();

        ServerPlanetServices planetServices = planetSystemService.getPlanet(TEST_PLANET_1_ID).getPlanetServices();

        // TestPlanetServices testServices = new TestPlanetServices();
        // BaseService baseService = new BaseServiceImpl(null);
        // ServerItemService serverItemService = new ServerItemServiceImpl();

        // testServices.setBaseService(baseService);
        //ServerConnectionService connectionServiceMock = EasyMock.createStrictMock(ServerConnectionService.class);
        //EasyMock.expect(connectionServiceMock.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        //globalServices.setServerConnectionService(connectionServiceMock);

        //EasyMock.replay(connectionServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Base base1 = createBase(planetServices, new Index(1000, 1000));
        Base base2 = createBase(planetServices, new Index(2000, 2000));
        Base base3 = createBase(planetServices, new Index(3000, 3000));
        Base base4 = createBase(planetServices, new Index(4000, 4000));
        Base base5 = createBase(planetServices, new Index(5000, 5000));
        SimpleBase botBase1 = createBotBase(planetServices);
        SimpleBase botBase2 = createBotBase(planetServices);
        createConnection();
        SyncBaseItem syncBaseItem1 = (SyncBaseItem) planetServices.getItemService().getItem(getFirstSynItemId(base1.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem syncBaseItem2 = (SyncBaseItem) planetServices.getItemService().getItem(getFirstSynItemId(base2.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem syncBaseItem3 = (SyncBaseItem) planetServices.getItemService().getItem(getFirstSynItemId(base3.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem syncBaseItem4 = (SyncBaseItem) planetServices.getItemService().getItem(getFirstSynItemId(base4.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem syncBaseItem5 = (SyncBaseItem) planetServices.getItemService().getItem(getFirstSynItemId(base5.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem botSyncBaseItem1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(1000, 1000), new Id(100, 0), globalServices, planetServices, botBase1);
        SyncBaseItem botSyncBaseItem2 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(1100, 1100), new Id(101, 0), globalServices, planetServices, botBase2);

        // Setup guild
        int guild12 = 1;
        planetServices.getBaseService().setGuild(base1.getSimpleBase(), new SimpleGuild(guild12, null));
        planetServices.getBaseService().setGuild(base2.getSimpleBase(), new SimpleGuild(guild12, null));
        int guild34 = 2;
        planetServices.getBaseService().setGuild(base3.getSimpleBase(), new SimpleGuild(guild34, null));
        planetServices.getBaseService().setGuild(base4.getSimpleBase(), new SimpleGuild(guild34, null));

        Assert.assertFalse(syncBaseItem1.isEnemy(syncBaseItem1));
        Assert.assertFalse(syncBaseItem1.isEnemy(base1.getSimpleBase()));
        Assert.assertFalse(syncBaseItem1.isEnemy(syncBaseItem2));
        Assert.assertFalse(syncBaseItem1.isEnemy(base2.getSimpleBase()));
        Assert.assertTrue(syncBaseItem1.isEnemy(syncBaseItem3));
        Assert.assertTrue(syncBaseItem1.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(syncBaseItem1.isEnemy(syncBaseItem4));
        Assert.assertTrue(syncBaseItem1.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(syncBaseItem1.isEnemy(syncBaseItem5));
        Assert.assertTrue(syncBaseItem1.isEnemy(base5.getSimpleBase()));
        Assert.assertTrue(syncBaseItem1.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem1.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem1.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem1.isEnemy(botBase2));

        Assert.assertFalse(syncBaseItem2.isEnemy(syncBaseItem1));
        Assert.assertFalse(syncBaseItem2.isEnemy(base1.getSimpleBase()));
        Assert.assertFalse(syncBaseItem2.isEnemy(syncBaseItem2));
        Assert.assertFalse(syncBaseItem2.isEnemy(base2.getSimpleBase()));
        Assert.assertTrue(syncBaseItem2.isEnemy(syncBaseItem3));
        Assert.assertTrue(syncBaseItem2.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(syncBaseItem2.isEnemy(syncBaseItem4));
        Assert.assertTrue(syncBaseItem2.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(syncBaseItem2.isEnemy(syncBaseItem5));
        Assert.assertTrue(syncBaseItem2.isEnemy(base5.getSimpleBase()));
        Assert.assertTrue(syncBaseItem2.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem2.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem2.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem2.isEnemy(botBase2));

        Assert.assertTrue(syncBaseItem3.isEnemy(syncBaseItem1));
        Assert.assertTrue(syncBaseItem3.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(syncBaseItem3.isEnemy(syncBaseItem2));
        Assert.assertTrue(syncBaseItem3.isEnemy(base2.getSimpleBase()));
        Assert.assertFalse(syncBaseItem3.isEnemy(syncBaseItem3));
        Assert.assertFalse(syncBaseItem3.isEnemy(base3.getSimpleBase()));
        Assert.assertFalse(syncBaseItem3.isEnemy(syncBaseItem4));
        Assert.assertFalse(syncBaseItem3.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(syncBaseItem3.isEnemy(syncBaseItem5));
        Assert.assertTrue(syncBaseItem3.isEnemy(base5.getSimpleBase()));
        Assert.assertTrue(syncBaseItem3.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem3.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem3.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem3.isEnemy(botBase2));

        Assert.assertTrue(syncBaseItem4.isEnemy(syncBaseItem1));
        Assert.assertTrue(syncBaseItem4.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(syncBaseItem4.isEnemy(syncBaseItem2));
        Assert.assertTrue(syncBaseItem4.isEnemy(base2.getSimpleBase()));
        Assert.assertFalse(syncBaseItem4.isEnemy(syncBaseItem3));
        Assert.assertFalse(syncBaseItem4.isEnemy(base3.getSimpleBase()));
        Assert.assertFalse(syncBaseItem4.isEnemy(syncBaseItem4));
        Assert.assertFalse(syncBaseItem4.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(syncBaseItem4.isEnemy(syncBaseItem5));
        Assert.assertTrue(syncBaseItem4.isEnemy(base5.getSimpleBase()));
        Assert.assertTrue(syncBaseItem4.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem4.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem4.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem4.isEnemy(botBase2));

        Assert.assertTrue(syncBaseItem5.isEnemy(syncBaseItem1));
        Assert.assertTrue(syncBaseItem5.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(syncBaseItem5.isEnemy(syncBaseItem2));
        Assert.assertTrue(syncBaseItem5.isEnemy(base2.getSimpleBase()));
        Assert.assertTrue(syncBaseItem5.isEnemy(syncBaseItem3));
        Assert.assertTrue(syncBaseItem5.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(syncBaseItem5.isEnemy(syncBaseItem4));
        Assert.assertTrue(syncBaseItem5.isEnemy(base4.getSimpleBase()));
        Assert.assertFalse(syncBaseItem5.isEnemy(syncBaseItem5));
        Assert.assertFalse(syncBaseItem5.isEnemy(base5.getSimpleBase()));
        Assert.assertTrue(syncBaseItem5.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem5.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem5.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem5.isEnemy(botBase2));

        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem1));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem2));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base2.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem3));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem4));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem5));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base5.getSimpleBase()));
        Assert.assertFalse(botSyncBaseItem1.isEnemy(botSyncBaseItem1));
        Assert.assertFalse(botSyncBaseItem1.isEnemy(botBase1));
        Assert.assertFalse(botSyncBaseItem1.isEnemy(botSyncBaseItem2));
        Assert.assertFalse(botSyncBaseItem1.isEnemy(botBase2));

        Assert.assertTrue(botSyncBaseItem2.isEnemy(syncBaseItem1));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(syncBaseItem2));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(base2.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(syncBaseItem3));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(syncBaseItem4));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(syncBaseItem5));
        Assert.assertTrue(botSyncBaseItem2.isEnemy(base5.getSimpleBase()));
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botSyncBaseItem1));
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botBase1));
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botSyncBaseItem2));
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botBase2));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private Base createBase(ServerPlanetServices baseService, Index startPosition) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException, PositionInBotException {
        UserState userState2 = new UserState();
        userState2.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        return baseService.getBaseService().createNewBase(userState2, serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID), 100, startPosition, 100);
    }

    private SimpleBase createBotBase(ServerPlanetServices baseService) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        BotConfig botConfig = new BotConfig(1, false, 0, null, createSimpleRegion(1), "bot", null, null, null, null);
        return baseService.getBaseService().createBotBase(botConfig);
    }

}
