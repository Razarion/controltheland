package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.item.ItemService;
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
public class TestDbToConfig extends AbstractServiceTest {
    @Autowired
    private BotService botService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void convert1() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig1 = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig1.setName("qaywsx");
        dbBotConfig1.setActionDelay(202);
        dbBotConfig1.setRealm(new Rectangle(2000, 2000, 1000, 1000));
        dbBotConfig1.setMaxActiveMs(101L);
        dbBotConfig1.setMinActiveMs(202L);
        dbBotConfig1.setMaxInactiveMs(303L);
        dbBotConfig1.setMinInactiveMs(404L);
        DbBotEnragementStateConfig enragementStateConfig1 = dbBotConfig1.getEnrageStateCrud().createDbChild();
        enragementStateConfig1.setName("Normal");
        enragementStateConfig1.setEnrageUpKills(10);
        DbBotItemConfig dbBotItemConfig1 = enragementStateConfig1.getBotItemCrud().createDbChild();
        dbBotItemConfig1.setCount(1);
        dbBotItemConfig1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        dbBotItemConfig1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        dbBotItemConfig1.setCreateDirectly(true);
        dbBotItemConfig1.setMoveRealmIfIdle(true);
        dbBotItemConfig1.setIdleTtl(1234);
        dbBotItemConfig1.setRePopTime(10L);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig dbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig2.setName("config2");
        dbBotConfig2.setActionDelay(111);
        dbBotConfig2.setRealm(new Rectangle(0, 0, 1000, 1000));
        DbBotEnragementStateConfig enragementStateConfig2 = dbBotConfig2.getEnrageStateCrud().createDbChild();
        enragementStateConfig2.setName("Normal2");
        enragementStateConfig2.setEnrageUpKills(20);
        DbBotItemConfig dbBotItemConfig2 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig2.setCount(10);
        dbBotItemConfig2.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        dbBotItemConfig2.setRegion(new Rectangle(0, 0, 1001, 1001));
        dbBotItemConfig2.setCreateDirectly(false);
        DbBotItemConfig dbBotItemConfig3 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig3.setCount(4);
        dbBotItemConfig3.setBaseItemType(itemService.getDbBaseItemType(TEST_CONTAINER_ITEM_ID));
        dbBotItemConfig3.setRegion(new Rectangle(0, 0, 1002, 1002));
        dbBotItemConfig3.setCreateDirectly(false);
        DbBotItemConfig dbBotItemConfig4 = enragementStateConfig2.getBotItemCrud().createDbChild();
        dbBotItemConfig4.setCount(7);
        dbBotItemConfig4.setBaseItemType(itemService.getDbBaseItemType(TEST_FACTORY_ITEM_ID));
        dbBotItemConfig4.setRegion(new Rectangle(0, 0, 1003, 1003));
        dbBotItemConfig4.setCreateDirectly(false);
        DbBotEnragementStateConfig enragementStateConfig3 = dbBotConfig2.getEnrageStateCrud().createDbChild();
        enragementStateConfig3.setName("Angry1");
        enragementStateConfig3.setEnrageUpKills(10);
        DbBotItemConfig dbBotItemConfig5 = enragementStateConfig3.getBotItemCrud().createDbChild();
        dbBotItemConfig5.setCount(33);
        dbBotItemConfig5.setBaseItemType(itemService.getDbBaseItemType(TEST_HARVESTER_ITEM_ID));
        dbBotItemConfig5.setRegion(new Rectangle(1, 1, 1005, 1005));
        dbBotItemConfig5.setCreateDirectly(true);
        dbBotItemConfig5.setMoveRealmIfIdle(true);
        dbBotItemConfig5.setIdleTtl(1234);
        dbBotItemConfig5.setRePopTime(10L);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 1
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig readDbBotConfig1 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig1.getId());
        BotConfig botConfig1 = readDbBotConfig1.createBotConfig(itemService);
        Assert.assertEquals("qaywsx", botConfig1.getName());
        Assert.assertEquals(202, botConfig1.getActionDelay());
        Assert.assertEquals(new Rectangle(2000, 2000, 1000, 1000), botConfig1.getRealm());
        Assert.assertEquals(101L, (long) botConfig1.getMaxActiveMs());
        Assert.assertEquals(202L, (long) botConfig1.getMinActiveMs());
        Assert.assertEquals(303L, (long) botConfig1.getMaxInactiveMs());
        Assert.assertEquals(404L, (long) botConfig1.getMinInactiveMs());
        Assert.assertEquals(1, botConfig1.getBotEnragementStateConfigs().size());
        BotEnragementStateConfig botEnragementStateConfig1 = botConfig1.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal", botEnragementStateConfig1.getName());
        Assert.assertEquals(10, botEnragementStateConfig1.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig1.getBotItems().size());
        BotItemConfig botItemConfig1 = CommonJava.getFirst(botEnragementStateConfig1.getBotItems());
        Assert.assertEquals(1, botItemConfig1.getCount());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, botItemConfig1.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(2000, 2000, 1000, 1000), botItemConfig1.getRegion());
        Assert.assertTrue(botItemConfig1.isCreateDirectly());
        Assert.assertTrue(botItemConfig1.isMoveRealmIfIdle());
        Assert.assertTrue(botItemConfig1.hasRePopTime());
        Assert.assertEquals(10L, botItemConfig1.getRePopTime());
        Assert.assertEquals(1234, (int) botItemConfig1.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBotConfig readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        BotConfig botConfig2 = readDbBotConfig2.createBotConfig(itemService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals(new Rectangle(0, 0, 1000, 1000), botConfig2.getRealm());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        BotEnragementStateConfig botEnragementStateConfig2 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal2", botEnragementStateConfig2.getName());
        Assert.assertEquals(20, botEnragementStateConfig2.getEnrageUpKills());
        Assert.assertEquals(3, botEnragementStateConfig2.getBotItems().size());
        BotItemConfig botItemConfig2 = getBotItemConfig(TEST_ATTACK_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(10, botItemConfig2.getCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, botItemConfig2.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1001, 1001), botItemConfig2.getRegion());
        Assert.assertFalse(botItemConfig2.isCreateDirectly());
        Assert.assertFalse(botItemConfig2.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig2.hasRePopTime());
        Assert.assertNull(botItemConfig2.getIdleTtl());
        BotItemConfig botItemConfig3 = getBotItemConfig(TEST_CONTAINER_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(4, botItemConfig3.getCount());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, botItemConfig3.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1002, 1002), botItemConfig3.getRegion());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        BotItemConfig botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1003, 1003), botItemConfig4.getRegion());
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
        Assert.assertEquals(new Rectangle(1, 1, 1005, 1005), botItemConfig5.getRegion());
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
        readDbBotConfig1 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig1.getId());
        botService.getDbBotConfigCrudServiceHelper().deleteDbChild(readDbBotConfig1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(itemService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals(new Rectangle(0, 0, 1000, 1000), botConfig2.getRealm());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig2 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal2", botEnragementStateConfig2.getName());
        Assert.assertEquals(20, botEnragementStateConfig2.getEnrageUpKills());
        Assert.assertEquals(3, botEnragementStateConfig2.getBotItems().size());
        botItemConfig2 = getBotItemConfig(TEST_ATTACK_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(10, botItemConfig2.getCount());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, botItemConfig2.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1001, 1001), botItemConfig2.getRegion());
        Assert.assertFalse(botItemConfig2.isCreateDirectly());
        Assert.assertFalse(botItemConfig2.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig2.hasRePopTime());
        Assert.assertNull(botItemConfig2.getIdleTtl());
        botItemConfig3 = getBotItemConfig(TEST_CONTAINER_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(4, botItemConfig3.getCount());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, botItemConfig3.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1002, 1002), botItemConfig3.getRegion());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1003, 1003), botItemConfig4.getRegion());
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
        Assert.assertEquals(new Rectangle(1, 1, 1005, 1005), botItemConfig5.getRegion());
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
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        dbBotItemConfig2 = readDbBotConfig2.getEnrageStateCrud().readDbChildren().get(0).getBotItemCrud().readDbChild(dbBotItemConfig2.getId());
        readDbBotConfig2.getEnrageStateCrud().readDbChildren().get(0).getBotItemCrud().deleteDbChild(dbBotItemConfig2);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(readDbBotConfig2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(itemService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals(new Rectangle(0, 0, 1000, 1000), botConfig2.getRealm());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig2 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Normal2", botEnragementStateConfig2.getName());
        Assert.assertEquals(20, botEnragementStateConfig2.getEnrageUpKills());
        Assert.assertEquals(2, botEnragementStateConfig2.getBotItems().size());
        botItemConfig3 = getBotItemConfig(TEST_CONTAINER_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(4, botItemConfig3.getCount());
        Assert.assertEquals(TEST_CONTAINER_ITEM_ID, botItemConfig3.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1002, 1002), botItemConfig3.getRegion());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1003, 1003), botItemConfig4.getRegion());
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
        Assert.assertEquals(new Rectangle(1, 1, 1005, 1005), botItemConfig5.getRegion());
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
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        Collections.swap(readDbBotConfig2.getEnrageStateCrud().readDbChildren(), 0, 1);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(readDbBotConfig2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(itemService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals(new Rectangle(0, 0, 1000, 1000), botConfig2.getRealm());
        Assert.assertEquals(2, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig3 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Angry1", botEnragementStateConfig3.getName());
        Assert.assertEquals(10, botEnragementStateConfig3.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig3.getBotItems().size());
        botItemConfig5 = getBotItemConfig(TEST_HARVESTER_ITEM_ID, botEnragementStateConfig3.getBotItems());
        Assert.assertEquals(33, botItemConfig5.getCount());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, botItemConfig5.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(1, 1, 1005, 1005), botItemConfig5.getRegion());
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
        Assert.assertEquals(new Rectangle(0, 0, 1002, 1002), botItemConfig3.getRegion());
        Assert.assertFalse(botItemConfig3.isCreateDirectly());
        Assert.assertFalse(botItemConfig3.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig3.hasRePopTime());
        Assert.assertNull(botItemConfig3.getIdleTtl());
        botItemConfig4 = getBotItemConfig(TEST_FACTORY_ITEM_ID, botEnragementStateConfig2.getBotItems());
        Assert.assertEquals(7, botItemConfig4.getCount());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig4.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(0, 0, 1003, 1003), botItemConfig4.getRegion());
        Assert.assertFalse(botItemConfig4.isCreateDirectly());
        Assert.assertFalse(botItemConfig4.isMoveRealmIfIdle());
        Assert.assertFalse(botItemConfig4.hasRePopTime());
        Assert.assertNull(botItemConfig4.getIdleTtl());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Swap enragement
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        readDbBotConfig2.getEnrageStateCrud().deleteDbChild(readDbBotConfig2.getEnrageStateCrud().readDbChildren().get(1));
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(readDbBotConfig2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify 2
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        botConfig2 = readDbBotConfig2.createBotConfig(itemService);
        Assert.assertEquals("config2", botConfig2.getName());
        Assert.assertEquals(111, botConfig2.getActionDelay());
        Assert.assertEquals(new Rectangle(0, 0, 1000, 1000), botConfig2.getRealm());
        Assert.assertEquals(1, botConfig2.getBotEnragementStateConfigs().size());
        botEnragementStateConfig3 = botConfig2.getBotEnragementStateConfigs().get(0);
        Assert.assertEquals("Angry1", botEnragementStateConfig3.getName());
        Assert.assertEquals(10, botEnragementStateConfig3.getEnrageUpKills());
        Assert.assertEquals(1, botEnragementStateConfig3.getBotItems().size());
        botItemConfig5 = getBotItemConfig(TEST_HARVESTER_ITEM_ID, botEnragementStateConfig3.getBotItems());
        Assert.assertEquals(33, botItemConfig5.getCount());
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, botItemConfig5.getBaseItemType().getId());
        Assert.assertEquals(new Rectangle(1, 1, 1005, 1005), botItemConfig5.getRegion());
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
        readDbBotConfig2 = botService.getDbBotConfigCrudServiceHelper().readDbChild(dbBotConfig2.getId());
        botService.getDbBotConfigCrudServiceHelper().deleteDbChild(readDbBotConfig2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(botService.getDbBotConfigCrudServiceHelper().readDbChildren().isEmpty());
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
        BotConfig botConfig = dbBotConfig.createBotConfig(itemService);

        Assert.assertFalse(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(10L);
        dbBotConfig.setMinActiveMs(5L);
        dbBotConfig.setMaxInactiveMs(20L);
        dbBotConfig.setMinInactiveMs(15L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(null);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(1L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(5L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxActiveMs(11L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(null);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(12L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(11L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinActiveMs(5L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(null);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(21L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(20L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMinInactiveMs(17L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(null);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(0L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(16L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertFalse(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(17L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());

        dbBotConfig.setMaxInactiveMs(18L);
        botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertTrue(botConfig.isIntervalBot());
        Assert.assertTrue(botConfig.isIntervalValid());
    }


}
