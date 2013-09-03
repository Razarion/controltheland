package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.terrain.RegionService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: 15.06.12
 * Time: 00:47
 */
public class TestBotCrud extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private RegionService regionService;

    @Test
    @DirtiesContext
    public void convert1() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Item Types
        setupItemTypes();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().createDbChild();
        DbBotConfig dbBotConfig1 = dbPlanet.getBotCrud().createDbChild();
        dbBotConfig1.setName("qaywsx");
        dbBotConfig1.setActionDelay(202);
        DbRegion dbRegion1 = createDbRegion(new Rectangle(2000, 2000, 1000, 1000));
        dbBotConfig1.setRealm(dbRegion1);
        dbBotConfig1.setMaxActiveMs(101L);
        dbBotConfig1.setMinActiveMs(202L);
        dbBotConfig1.setMaxInactiveMs(303L);
        dbBotConfig1.setMinInactiveMs(404L);
        DbBotEnragementStateConfig enragementStateConfig1 = dbBotConfig1.getEnrageStateCrud().createDbChild();
        enragementStateConfig1.setName("Normal");
        enragementStateConfig1.setEnrageUpKills(10);
        DbBotItemConfig dbBotItemConfig11 = enragementStateConfig1.getBotItemCrud().createDbChild();
        dbBotItemConfig11.setCount(1);
        dbBotItemConfig11.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        DbRegion dbRegion11 = createDbRegion(new Rectangle(2000, 2000, 1000, 1000));
        dbBotItemConfig11.setRegion(dbRegion11);
        dbBotItemConfig11.setCreateDirectly(true);
        dbBotItemConfig11.setMoveRealmIfIdle(true);
        dbBotItemConfig11.setIdleTtl(1234);
        dbBotItemConfig11.setRePopTime(10L);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbRegion dbRegion21 = createDbRegion(new Rectangle(0, 0, 1001, 1001));
        DbRegion dbRegion22 = createDbRegion(new Rectangle(0, 0, 1002, 1002));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        DbBotConfig dbBotConfig2 = dbPlanet.getBotCrud().createDbChild();
        dbBotConfig2.setName("config2");
        dbBotConfig2.setActionDelay(111);
        DbRegion dbRegion2 = createDbRegion(new Rectangle(0, 0, 1000, 1000));
        dbBotConfig2.setRealm(dbRegion2);
        DbBotEnragementStateConfig enragementStateConfig2 = dbBotConfig2.getEnrageStateCrud().createDbChild();
        enragementStateConfig2.setName("Normal2");
        enragementStateConfig2.setEnrageUpKills(20);
        DbBotItemConfig dbBotItemConfig21 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig21.setCount(10);
        dbBotItemConfig21.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfig21.setRegion(regionService.getRegionCrud().readDbChild(dbRegion21.getId()));
        dbBotItemConfig21.setCreateDirectly(false);
        DbBotItemConfig dbBotItemConfig22 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig22.setCount(4);
        dbBotItemConfig22.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        dbBotItemConfig22.setRegion(regionService.getRegionCrud().readDbChild(dbRegion22.getId()));
        dbBotItemConfig22.setCreateDirectly(false);
        DbBotItemConfig dbBotItemConfig23 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig23.setCount(7);
        dbBotItemConfig23.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        DbRegion dbRegion23 = createDbRegion(new Rectangle(0, 0, 1003, 1003));
        dbBotItemConfig23.setRegion(dbRegion23);
        dbBotItemConfig23.setCreateDirectly(false);
        DbBotEnragementStateConfig enragementStateConfig3 = dbBotConfig2.getEnrageStateCrud().createDbChild();
        enragementStateConfig3.setName("Angry1");
        enragementStateConfig3.setEnrageUpKills(10);
        DbBotItemConfig dbBotItemConfig24 = enragementStateConfig3.getBotItemCrud().createDbChild();
        dbBotItemConfig24.setCount(33);
        dbBotItemConfig24.setBaseItemType(serverItemTypeService.getDbBaseItemType(TEST_HARVESTER_ITEM_ID));
        dbBotItemConfig24.setCreateDirectly(true);
        dbBotItemConfig24.setMoveRealmIfIdle(true);
        dbBotItemConfig24.setIdleTtl(1234);
        dbBotItemConfig24.setRePopTime(10L);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        DbBotConfig readDbBotConfig1 = dbPlanet.getBotCrud().readDbChild(dbBotConfig1.getId());
        BotConfig botConfig1 = readDbBotConfig1.createBotConfig(serverItemTypeService);
        Assert.assertEquals("qaywsx", botConfig1.getName());
        Assert.assertEquals(202, botConfig1.getActionDelay());
        Assert.assertEquals((int)dbRegion1.getId(), botConfig1.getRealm().getId());
        Assert.assertEquals(101L, (long) botConfig1.getMaxActiveMs());
        Assert.assertEquals(202L, (long) botConfig1.getMinActiveMs());
        Assert.assertEquals(303L, (long) botConfig1.getMaxInactiveMs());
        Assert.assertEquals(404L, (long) botConfig1.getMinInactiveMs());
        Assert.assertEquals(1, botConfig1.getBotEnragementStateConfigs().size());
        BotEnragementStateConfig botEnragementStateConfig1 = botConfig1.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal", botEnragementStateConfig1.getName());
        Assert.assertEquals(10, botEnragementStateConfig1.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig1.getBotItems().size());
        BotItemConfig botItemConfig11 = CommonJava.getFirst(botEnragementStateConfig1.getBotItems());
        Assert.assertEquals(1, botItemConfig11.getCount());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, botItemConfig11.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion11.getId(), botItemConfig11.getRegion().getId());
        Assert.assertTrue(botItemConfig11.isCreateDirectly());
        Assert.assertTrue(botItemConfig11.isMoveRealmIfIdle());
        Assert.assertTrue(botItemConfig11.hasRePopTime());
        Assert.assertEquals(10L, botItemConfig11.getRePopTime());
        Assert.assertEquals(1234, (int) botItemConfig11.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        DbBotConfig readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        BotConfig botConfig2 = readDbBotConfig2.createBotConfig(serverItemTypeService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals((int)dbRegion2.getId(), botConfig2.getRealm().getId());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        BotEnragementStateConfig botEnragementStateConfig2 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal2", botEnragementStateConfig2.getName());
        Assert.assertEquals(20, botEnragementStateConfig2.getEnrageUpKills());
        Assert.assertEquals(3, botEnragementStateConfig2.getBotItems().size());
        BotItemConfig botItemConfig2 = getBotItemConfig(TEST_ATTACK_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(10, botItemConfig2.getCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, botItemConfig2.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion21.getId(), botItemConfig2.getRegion().getId());
        Assert.assertFalse(botItemConfig2.isCreateDirectly());
        Assert.assertFalse(botItemConfig2.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig2.hasRePopTime());
        Assert.assertNull(botItemConfig2.getIdleTtl());
        BotItemConfig botItemConfig3 = getBotItemConfig(TEST_CONTAINER_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(4, botItemConfig3.getCount());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, botItemConfig3.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion22.getId(), botItemConfig3.getRegion().getId());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        BotItemConfig botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion23.getId(), botItemConfig4.getRegion().getId());
        Assert.assertFalse(botItemConfig4.isCreateDirectly());
        Assert.assertFalse(botItemConfig4.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig4.hasRePopTime());
        Assert.assertNull(botItemConfig4.getIdleTtl());
        BotEnragementStateConfig botEnragementStateConfig3 = botConfig2.getBotEnragementStateConfigs().get(1);
        Assert.assertEquals("Angry1", botEnragementStateConfig3.getName());
        Assert.assertEquals(10, botEnragementStateConfig3.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig3.getBotItems().size());
        BotItemConfig botItemConfig5 = getBotItemConfig(TEST_HARVESTER_ITEM_ID, botEnragementStateConfig3.getBotItems());
        Assert.assertEquals(33, botItemConfig5.getCount());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, botItemConfig5.getBaseItemType().getId());
        Assert.assertNull(botItemConfig5.getRegion());
        Assert.assertTrue(botItemConfig5.isCreateDirectly());
        Assert.assertTrue(botItemConfig5.isMoveRealmIfIdle());
        Assert.assertTrue(botItemConfig5.hasRePopTime());
        Assert.assertEquals(10L, botItemConfig5.getRePopTime());
        Assert.assertEquals(1234, (long) botItemConfig5.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Remove first bot
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig1 = dbPlanet.getBotCrud().readDbChild(dbBotConfig1.getId());
        dbPlanet.getBotCrud().deleteDbChild(readDbBotConfig1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(serverItemTypeService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals((int)dbRegion2.getId(), botConfig2.getRealm().getId());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig2 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal2", botEnragementStateConfig2.getName());
        Assert.assertEquals(20, botEnragementStateConfig2.getEnrageUpKills());
        Assert.assertEquals(3, botEnragementStateConfig2.getBotItems().size());
        botItemConfig2 = getBotItemConfig(TEST_ATTACK_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(10, botItemConfig2.getCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, botItemConfig2.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion21.getId(), botItemConfig2.getRegion().getId());
        Assert.assertFalse(botItemConfig2.isCreateDirectly());
        Assert.assertFalse(botItemConfig2.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig2.hasRePopTime());
        Assert.assertNull(botItemConfig2.getIdleTtl());
        botItemConfig3 = getBotItemConfig(TEST_CONTAINER_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(4, botItemConfig3.getCount());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, botItemConfig3.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion22.getId(), botItemConfig3.getRegion().getId());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion23.getId(), botItemConfig4.getRegion().getId());
        Assert.assertFalse(botItemConfig4.isCreateDirectly());
        Assert.assertFalse(botItemConfig4.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig4.hasRePopTime());
        Assert.assertNull(botItemConfig4.getIdleTtl());
        botEnragementStateConfig3 = botConfig2.getBotEnragementStateConfigs().get(1);
        Assert.assertEquals("Angry1", botEnragementStateConfig3.getName());
        Assert.assertEquals(10, botEnragementStateConfig3.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig3.getBotItems().size());
        botItemConfig5 = getBotItemConfig(TEST_HARVESTER_ITEM_ID, botEnragementStateConfig3.getBotItems());
        Assert.assertEquals(33, botItemConfig5.getCount());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, botItemConfig5.getBaseItemType().getId());
        Assert.assertNull(botItemConfig5.getRegion());
        Assert.assertTrue(botItemConfig5.isCreateDirectly());
        Assert.assertTrue(botItemConfig5.isMoveRealmIfIdle());
        Assert.assertTrue(botItemConfig5.hasRePopTime());
        Assert.assertEquals(10L, botItemConfig5.getRePopTime());
        Assert.assertEquals(1234, (long) botItemConfig5.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Remove item config
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        dbBotItemConfig21 = readDbBotConfig2.getEnrageStateCrud().readDbChildren().get(0).getBotItemCrud().readDbChild(dbBotItemConfig21.getId());
        readDbBotConfig2.getEnrageStateCrud().readDbChildren().get(0).getBotItemCrud().deleteDbChild(dbBotItemConfig21);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(serverItemTypeService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals((int)dbRegion2.getId(), botConfig2.getRealm().getId());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig2 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal2", botEnragementStateConfig2.getName());
        Assert.assertEquals(20, botEnragementStateConfig2.getEnrageUpKills());
        Assert.assertEquals(2, botEnragementStateConfig2.getBotItems().size());
        botItemConfig3 = getBotItemConfig(TEST_CONTAINER_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(4, botItemConfig3.getCount());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, botItemConfig3.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion22.getId(), botItemConfig3.getRegion().getId());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion23.getId(), botItemConfig4.getRegion().getId());
        Assert.assertFalse(botItemConfig4.isCreateDirectly());
        Assert.assertFalse(botItemConfig4.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig4.hasRePopTime());
        Assert.assertNull(botItemConfig4.getIdleTtl());
        botEnragementStateConfig3 = botConfig2.getBotEnragementStateConfigs().get(1);
        Assert.assertEquals("Angry1", botEnragementStateConfig3.getName());
        Assert.assertEquals(10, botEnragementStateConfig3.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig3.getBotItems().size());
        botItemConfig5 = getBotItemConfig(TEST_HARVESTER_ITEM_ID, botEnragementStateConfig3.getBotItems());
        Assert.assertEquals(33, botItemConfig5.getCount());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, botItemConfig5.getBaseItemType().getId());
        Assert.assertNull(botItemConfig5.getRegion());
        Assert.assertTrue(botItemConfig5.isCreateDirectly());
        Assert.assertTrue(botItemConfig5.isMoveRealmIfIdle());
        Assert.assertTrue(botItemConfig5.hasRePopTime());
        Assert.assertEquals(10L, botItemConfig5.getRePopTime());
        Assert.assertEquals(1234, (long) botItemConfig5.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Swap enragement
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        Collections.swap(readDbBotConfig2.getEnrageStateCrud().readDbChildren(), 0, 1);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(serverItemTypeService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals((int)dbRegion2.getId(), botConfig2.getRealm().getId());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig3 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Angry1", botEnragementStateConfig3.getName());
        Assert.assertEquals(10, botEnragementStateConfig3.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig3.getBotItems().size());
        botItemConfig5 = getBotItemConfig(TEST_HARVESTER_ITEM_ID, botEnragementStateConfig3.getBotItems());
        Assert.assertEquals(33, botItemConfig5.getCount());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, botItemConfig5.getBaseItemType().getId());
        Assert.assertNull(botItemConfig5.getRegion());
        Assert.assertTrue(botItemConfig5.isCreateDirectly());
        Assert.assertTrue(botItemConfig5.isMoveRealmIfIdle());
        Assert.assertTrue(botItemConfig5.hasRePopTime());
        Assert.assertEquals(10L, botItemConfig5.getRePopTime());
        Assert.assertEquals(1234, (long) botItemConfig5.getIdleTtl());
        botEnragementStateConfig2 = botConfig2.getBotEnragementStateConfigs().get(1);
        Assert.assertEquals("Normal2", botEnragementStateConfig2.getName());
        Assert.assertEquals(20, botEnragementStateConfig2.getEnrageUpKills());
        Assert.assertEquals(2, botEnragementStateConfig2.getBotItems().size());
        botItemConfig3 = getBotItemConfig(TEST_CONTAINER_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(4, botItemConfig3.getCount());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, botItemConfig3.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion22.getId(), botItemConfig3.getRegion().getId());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals((int)dbRegion23.getId(), botItemConfig4.getRegion().getId());
        Assert.assertFalse(botItemConfig4.isCreateDirectly());
        Assert.assertFalse(botItemConfig4.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig4.hasRePopTime());
        Assert.assertNull(botItemConfig4.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Swap enragement
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        readDbBotConfig2.getEnrageStateCrud().deleteDbChild(readDbBotConfig2.getEnrageStateCrud().readDbChildren().get(1));
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(serverItemTypeService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals((int)dbRegion2.getId(), botConfig2.getRealm().getId());
        Assert.assertEquals(1, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig3 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Angry1", botEnragementStateConfig3.getName());
        Assert.assertEquals(10, botEnragementStateConfig3.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig3.getBotItems().size());
        botItemConfig5 = getBotItemConfig(TEST_HARVESTER_ITEM_ID, botEnragementStateConfig3.getBotItems());
        Assert.assertEquals(33, botItemConfig5.getCount());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, botItemConfig5.getBaseItemType().getId());
        Assert.assertNull(botItemConfig5.getRegion());
        Assert.assertTrue(botItemConfig5.isCreateDirectly());
        Assert.assertTrue(botItemConfig5.isMoveRealmIfIdle());
        Assert.assertTrue(botItemConfig5.hasRePopTime());
        Assert.assertEquals(10L, botItemConfig5.getRePopTime());
        Assert.assertEquals(1234, (long) botItemConfig5.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Remove last bot
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        readDbBotConfig2 = dbPlanet.getBotCrud().readDbChild(dbBotConfig2.getId());
        dbPlanet.getBotCrud().deleteDbChild(readDbBotConfig2);
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(dbPlanet.getId());
        Assert.assertTrue(dbPlanet.getBotCrud().readDbChildren().isEmpty());
        Assert.assertTrue(HibernateUtil.loadAll(getSessionFactory(), DbBotConfig.class).isEmpty());
        Assert.assertTrue(HibernateUtil.loadAll(getSessionFactory(), DbBotEnragementStateConfig.class).isEmpty());
        Assert.assertTrue(HibernateUtil.loadAll(getSessionFactory(), DbBotItemConfig.class).isEmpty());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private BotItemConfig getBotItemConfig(int itemTypeId, Collection<BotItemConfig> botItemConfigs) {
        for (BotItemConfig botItemConfig : botItemConfigs) {
            if (botItemConfig.getBaseItemType().getId() == itemTypeId) {
                return botItemConfig;
            }
        }
        throw new IllegalArgumentException("Unable to find BotItemConfig with id: " + itemTypeId);
    }

    @Test
    @DirtiesContext
    public void intervalConfig() throws Exception {
        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.setRealm(createDbRegion(new Rectangle(0, 0, 1000, 1000)));
        BotConfig botConfig = dbBotConfig.createBotConfig(serverItemTypeService);

        Assert.assertFalse(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(10L);
        dbBotConfig.setMinActiveMs(5L);
        dbBotConfig.setMaxInactiveMs(20L);
        dbBotConfig.setMinInactiveMs(15L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(null);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(1L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(5L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(11L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(null);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(12L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(11L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(5L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(null);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(21L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(20L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(17L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(null);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(16L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(17L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(18L);
        botConfig = dbBotConfig.createBotConfig(serverItemTypeService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());
    }


}
