package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.history.DbHistoryElement;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.BackupService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 28.03.2011
 * Time: 17:10:51
 */
public class TestBotService extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private BackupService backupService;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void testInRealm() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(1, 1, 3000, 3000));
        setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(4000, 4000, 3000, 3000));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        BotService botService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService();

        Assert.assertTrue(botService.isInRealm(new Index(2, 2)));
        Assert.assertTrue(botService.isInRealm(new Index(2999, 2999)));
        Assert.assertTrue(botService.isInRealm(new Index(4001, 4001)));
        Assert.assertTrue(botService.isInRealm(new Index(6999, 6999)));

        Assert.assertFalse(botService.isInRealm(new Index(3002, 3002)));
        Assert.assertFalse(botService.isInRealm(new Index(3998, 3998)));
        Assert.assertFalse(botService.isInRealm(new Index(7002, 7002)));
        Assert.assertFalse(botService.isInRealm(new Index(8000, 8000)));

    }

    @Test
    @DirtiesContext
    public void testSystemActivate() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(1, 1, 5000, 5000)).createBotConfig(serverItemTypeService);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        BotService botService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService();

        HibernateUtil.openSession4InternalCall(getSessionFactory());
        try {

            botService.activate(planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID));
        } finally {
            HibernateUtil.closeSession4InternalCall(getSessionFactory());
        }
        // Wait for bot to complete
        // TODO failed on 28.06.2012
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        assertWholeItemCount(TEST_PLANET_1_ID, 4);

        HibernateUtil.openSession4InternalCall(getSessionFactory());
        try {
            botService.activate(planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID));
        } finally {
            HibernateUtil.closeSession4InternalCall(getSessionFactory());
        }
        // Wait for bot to complete
        // TODO failed on: 18.06.2012, 07.07.2012
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
    }

    @Test
    @DirtiesContext
    public void testSystemActivateNoWait() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BotConfig botConfig = setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(1, 1, 5000, 5000)).createBotConfig(serverItemTypeService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        BotService botService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService();

        HibernateUtil.openSession4InternalCall(getSessionFactory());
        try {
            for (int i = 0; i < 1000; i++) {
                botService.activate(planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID));
            }
        } finally {
            HibernateUtil.closeSession4InternalCall(getSessionFactory());
        }

        // Wait for bot to complete
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        // TODO failed on: 25.10.2012, 25.10.2012, 07.12.2012, 14.12.2012
        assertBaseCount(TEST_PLANET_1_ID, 1);
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
    }

    @Test
    @DirtiesContext
    public void testDelete() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig = setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(1, 1, 5000, 5000));
        BotConfig botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);
        assertWholeItemCount(TEST_PLANET_1_ID, 4);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        dbPlanet.getBotCrud().deleteDbChild(dbPlanet.getBotCrud().readDbChild(dbBotConfig.getId()));
        BotService botService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService();
        botService.activate(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        assertWholeItemCount(TEST_PLANET_1_ID, 0);

        // Make sure backup still works
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        backupService.backup();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testRageUp() throws Exception {
        configureSimplePlanetNoResources();

        ServerItemService serverItemService = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        createAndLoginUser("U1");
        sendBuildCommand(getFirstSynItemId(TEST_START_BUILDER_ITEM_ID), new Index(2000, 2000), TEST_FACTORY_ITEM_ID);
        waitForActionServiceDone();
        sendFactoryCommand(getFirstSynItemId(TEST_FACTORY_ITEM_ID), TEST_ATTACK_ITEM_ID);
        waitForActionServiceDone();
        Id intruder = getFirstSynItemId(TEST_ATTACK_ITEM_ID);
        sendMoveCommand(getFirstSynItemId(TEST_ATTACK_ITEM_ID), new Index(5000, 5000));
        waitForActionServiceDone();
        SyncBaseItem intruderItem = (SyncBaseItem) serverItemService.getItem(intruder);
        intruderItem.setHealth(100000);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        DbBotConfig dbBotConfig = dbPlanet.getBotCrud().createDbChild();
        dbBotConfig.setName("config2");
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(createDbRegion(new Rectangle(0, 0, 1000, 1000)));
        DbBotEnragementStateConfig enragementStateConfig1 = dbBotConfig.getEnrageStateCrud().createDbChild();
        enragementStateConfig1.setName("NormalTest");
        enragementStateConfig1.setEnrageUpKills(3);
        DbBotItemConfig dbBotItemConfig1 = enragementStateConfig1.getBotItemCrud().createDbChild();
        dbBotItemConfig1.setCount(2);
        dbBotItemConfig1.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfig1.setRegion(createDbRegion(new Rectangle(500, 500, 500, 500)));
        dbBotItemConfig1.setCreateDirectly(true);
        DbBotEnragementStateConfig enragementStateConfig2 = dbBotConfig.getEnrageStateCrud().createDbChild();
        enragementStateConfig2.setName("AngryTest");
        enragementStateConfig2.setEnrageUpKills(10);
        DbBotItemConfig dbBotItemConfig2 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig2.setCount(2);
        dbBotItemConfig2.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID_2));
        dbBotItemConfig2.setRegion(createDbRegion(new Rectangle(500, 500, 500, 500)));
        dbBotItemConfig2.setCreateDirectly(true);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        planetSystemService.getPlanet(TEST_PLANET_1_ID).deactivate();
        planetSystemService.getPlanet(TEST_PLANET_1_ID).activate(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Thread.sleep(500); // Wait for bot to build up

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        loginUser("U1", "xxx");
        sendMoveCommand(intruder, new Index(500, 500));
        waitForHistoryType(DbHistoryElement.Type.BOT_ENRAGE_UP);
        sendMoveCommand(intruder, new Index(5000, 5000));
        waitForHistoryType(DbHistoryElement.Type.BOT_ENRAGE_NORMAL);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testReactivate() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig = setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(1, 1, 5000, 5000));
        BotConfig botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        List<SyncItem> syncItems1 = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItemsCopy();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().reactivate(planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig);

        for (SyncItem syncItem : syncItems1) {
            Assert.assertFalse(syncItem.isAlive());
        }
        List<SyncItem> syncItems2 = planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getItemService().getItemsCopy();
        for (SyncItem syncItem : syncItems2) {
            Assert.assertTrue(syncItem.isAlive());
        }
    }

    @Test
    @DirtiesContext
    public void testKillBot() throws Exception {
        configureSimplePlanetNoResources();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig1 = setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(1, 1, 5000, 5000));
        BotConfig botConfig1 = dbBotConfig1.createBotConfig(serverItemTypeService);
        DbBotConfig dbBotConfig2 = setupMinimalBot(TEST_PLANET_1_ID, new Rectangle(5000, 5000, 5000, 5000));
        BotConfig botConfig2 = dbBotConfig2.createBotConfig(serverItemTypeService);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Wait for bot to complete
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig1);
        waitForBotToBuildup(TEST_PLANET_1_ID, botConfig2);
        assertWholeItemCount(TEST_PLANET_1_ID, 8);
        Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().getBotRunner(botConfig1));
        Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().getBotRunner(botConfig2));
        // Kill bot
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().killBot(dbBotConfig1.getId());
        Thread.sleep(1000);
        // Verify
        Assert.assertNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().getBotRunner(botConfig1));
        Assert.assertNotNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().getBotRunner(botConfig2));
        assertWholeItemCount(TEST_PLANET_1_ID, 4);
        // Kill bot
        planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().killBot(dbBotConfig2.getId());
        Thread.sleep(1000);
        // Verify
        Assert.assertNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().getBotRunner(botConfig1));
        Assert.assertNull(planetSystemService.getServerPlanetServices(TEST_PLANET_1_ID).getBotService().getBotRunner(botConfig2));
        assertWholeItemCount(TEST_PLANET_1_ID, 0);
    }

}
