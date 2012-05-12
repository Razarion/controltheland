package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.TestServices;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.territory.TerritoryService;
import com.btxtech.game.services.user.UserState;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;

/**
 * User: beat
 * Date: 23.04.12
 * Time: 17:12
 */
public class TestSyncBaseItem extends AbstractServiceTest {
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private ServerServices serverServices;

    @Test
    public void isEnemy1() throws Exception {
        configureRealGame();

        TestServices testServices = new TestServices();
        testServices.setBaseService(baseService);
        ConnectionService connectionServiceMock = EasyMock.createStrictMock(ConnectionService.class);
        EasyMock.expect(connectionServiceMock.getGameEngineMode()).andReturn(GameEngineMode.MASTER).anyTimes();
        testServices.setConnectionService(connectionServiceMock);

        EasyMock.replay(connectionServiceMock);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Base base1 = createBase();
        Base base2 = createBase();
        Base base3 = createBase();
        Base base4 = createBase();
        SimpleBase botBase1 = createBotBase();
        SimpleBase botBase2 = createBotBase();
        SyncBaseItem syncBaseItem1 = (SyncBaseItem) itemService.getItem(getFirstSynItemId(base1.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem syncBaseItem2 = (SyncBaseItem) itemService.getItem(getFirstSynItemId(base2.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem syncBaseItem3 = (SyncBaseItem) itemService.getItem(getFirstSynItemId(base3.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem syncBaseItem4 = (SyncBaseItem) itemService.getItem(getFirstSynItemId(base4.getSimpleBase(), TEST_START_BUILDER_ITEM_ID));
        SyncBaseItem botSyncBaseItem1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(1000, 1000), new Id(100, 0, 0), serverServices, botBase1);
        SyncBaseItem botSyncBaseItem2 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(1100, 1100), new Id(101, 0, 0), serverServices, botBase2);

        Collection<SimpleBase> alliancesBase1 = new HashSet<>();
        alliancesBase1.add(base2.getSimpleBase());
        alliancesBase1.add(base3.getSimpleBase());
        baseService.setAlliances(base1.getSimpleBase(), alliancesBase1);

        Collection<SimpleBase> alliancesBase2 = new HashSet<>();
        alliancesBase2.add(base1.getSimpleBase());
        baseService.setAlliances(base2.getSimpleBase(), alliancesBase2);

        Collection<SimpleBase> alliancesBase3 = new HashSet<>();
        alliancesBase3.add(base1.getSimpleBase());
        baseService.setAlliances(base3.getSimpleBase(), alliancesBase3);

        Assert.assertFalse(syncBaseItem1.isEnemy(syncBaseItem1));
        Assert.assertFalse(syncBaseItem1.isEnemy(base1.getSimpleBase()));
        Assert.assertFalse(syncBaseItem1.isEnemy(syncBaseItem2));
        Assert.assertFalse(syncBaseItem1.isEnemy(base2.getSimpleBase()));
        Assert.assertFalse(syncBaseItem1.isEnemy(syncBaseItem3));
        Assert.assertFalse(syncBaseItem1.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(syncBaseItem1.isEnemy(syncBaseItem4));
        Assert.assertTrue(syncBaseItem1.isEnemy(base4.getSimpleBase()));
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
        Assert.assertTrue(syncBaseItem2.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem2.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem2.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem2.isEnemy(botBase2));

        Assert.assertFalse(syncBaseItem3.isEnemy(syncBaseItem1));
        Assert.assertFalse(syncBaseItem3.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(syncBaseItem3.isEnemy(syncBaseItem2));
        Assert.assertTrue(syncBaseItem3.isEnemy(base2.getSimpleBase()));
        Assert.assertFalse(syncBaseItem3.isEnemy(syncBaseItem3));
        Assert.assertFalse(syncBaseItem3.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(syncBaseItem3.isEnemy(syncBaseItem4));
        Assert.assertTrue(syncBaseItem3.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(syncBaseItem3.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem3.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem3.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem3.isEnemy(botBase2));

        Assert.assertTrue(syncBaseItem4.isEnemy(syncBaseItem1));
        Assert.assertTrue(syncBaseItem4.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(syncBaseItem4.isEnemy(syncBaseItem2));
        Assert.assertTrue(syncBaseItem4.isEnemy(base2.getSimpleBase()));
        Assert.assertTrue(syncBaseItem4.isEnemy(syncBaseItem3));
        Assert.assertTrue(syncBaseItem4.isEnemy(base3.getSimpleBase()));
        Assert.assertFalse(syncBaseItem4.isEnemy(syncBaseItem4));
        Assert.assertFalse(syncBaseItem4.isEnemy(base4.getSimpleBase()));
        Assert.assertTrue(syncBaseItem4.isEnemy(botSyncBaseItem1));
        Assert.assertTrue(syncBaseItem4.isEnemy(botBase1));
        Assert.assertTrue(syncBaseItem4.isEnemy(botSyncBaseItem2));
        Assert.assertTrue(syncBaseItem4.isEnemy(botBase2));

        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem1));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base1.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem2));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base2.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem3));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base3.getSimpleBase()));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(syncBaseItem4));
        Assert.assertTrue(botSyncBaseItem1.isEnemy(base4.getSimpleBase()));
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
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botSyncBaseItem1));
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botBase1));
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botSyncBaseItem2));
        Assert.assertFalse(botSyncBaseItem2.isEnemy(botBase2));

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private Base createBase() throws AlreadyUsedException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        UserState userState2 = new UserState();
        userState2.setDbLevelId(TEST_LEVEL_2_REAL_ID);
        return baseService.createNewBase(userState2, itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID), territoryService.getTerritory(TEST_NOOB_TERRITORY_ID), 100);
    }

    private SimpleBase createBotBase() throws AlreadyUsedException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        BotConfig botConfig = new BotConfig(1, 0, null, null, "bot", null, null, null, null);
        return baseService.createBotBase(botConfig);
    }

}