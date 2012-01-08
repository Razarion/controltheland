package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;

/**
 * User: beat
 * Date: 17.10.2011
 * Time: 13:37:01
 */
public class TestDbBotConfig extends AbstractServiceTest {
    @Autowired
    private BotService botService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void convert1() throws Exception {
        configureMinimalGame();

        Rectangle realm = new Rectangle(10, 20, 3000, 4000);

        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setName("aabbcc");
        dbBotConfig.setActionDelay(10);
        dbBotConfig.setRealm(realm);

        DbBotItemConfig builder = dbBotConfig.getBotItemCrud().createDbChild();
        builder.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        builder.setCount(1);
        builder.setCreateDirectly(true);
        builder.setRegion(new Rectangle(1, 2, 30, 40));
        DbBotItemConfig factory = dbBotConfig.getBotItemCrud().createDbChild();
        factory.setBaseItemType(getDbBaseItemTypeInSession(TEST_FACTORY_ITEM_ID));
        factory.setCount(3);
        factory.setRegion(new Rectangle(8, 9, 33, 44));
        factory.setMoveRealmIfIdle(true);
        DbBotItemConfig defence = dbBotConfig.getBotItemCrud().createDbChild();
        defence.setBaseItemType(getDbBaseItemTypeInSession(TEST_ATTACK_ITEM_ID));
        defence.setCount(2);
        defence.setIdleTtl(1234);
        botService.getDbBotConfigCrudServiceHelper().updateDbChild(dbBotConfig);

        BotConfig botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertEquals((int) dbBotConfig.getId(), botConfig.hashCode());
        Assert.assertEquals(10, botConfig.getActionDelay());
        Assert.assertEquals(realm, botConfig.getRealm());
        Assert.assertEquals("aabbcc", botConfig.getName());
        Assert.assertEquals(null, botConfig.getMaxActiveMs());
        Assert.assertEquals(null, botConfig.getMinActiveMs());
        Assert.assertEquals(null, botConfig.getMaxInactiveMs());
        Assert.assertEquals(null, botConfig.getMinInactiveMs());

        Assert.assertEquals(3, botConfig.getBotItems().size());
        BotItemConfig botItemConfig = getBotItemConfig4ItemTypeId(TEST_START_BUILDER_ITEM_ID, botConfig.getBotItems());
        Assert.assertEquals(TEST_START_BUILDER_ITEM_ID, botItemConfig.getBaseItemType().getId());
        Assert.assertEquals(1, botItemConfig.getCount());
        Assert.assertTrue(botItemConfig.isCreateDirectly());
        Assert.assertEquals(new Rectangle(1, 2, 30, 40), botItemConfig.getRegion());
        Assert.assertFalse(botItemConfig.isMoveRealmIfIdle());
        Assert.assertNull(botItemConfig.getIdleTtl());

        botItemConfig = getBotItemConfig4ItemTypeId(TEST_FACTORY_ITEM_ID, botConfig.getBotItems());
        Assert.assertEquals(TEST_FACTORY_ITEM_ID, botItemConfig.getBaseItemType().getId());
        Assert.assertEquals(3, botItemConfig.getCount());
        Assert.assertFalse(botItemConfig.isCreateDirectly());
        Assert.assertEquals(new Rectangle(8, 9, 33, 44), botItemConfig.getRegion());
        Assert.assertTrue(botItemConfig.isMoveRealmIfIdle());
        Assert.assertNull(botItemConfig.getIdleTtl());

        botItemConfig = getBotItemConfig4ItemTypeId(TEST_ATTACK_ITEM_ID, botConfig.getBotItems());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, botItemConfig.getBaseItemType().getId());
        Assert.assertEquals(2, botItemConfig.getCount());
        Assert.assertFalse(botItemConfig.isCreateDirectly());
        Assert.assertNull(botItemConfig.getRegion());
        Assert.assertFalse(botItemConfig.isMoveRealmIfIdle());
        Assert.assertEquals(1234, (int)botItemConfig.getIdleTtl());
    }

    private BotItemConfig getBotItemConfig4ItemTypeId(int itemTypeId, Collection<BotItemConfig> botItems) {
        for (BotItemConfig botItem : botItems) {
            if (botItem.getBaseItemType().getId() == itemTypeId) {
                return botItem;
            }
        }
        throw new IllegalArgumentException("Not BotItemConfig for ItemTypeId: " + itemTypeId);
    }

    @Test
    @DirtiesContext
    public void convert2() throws Exception {
        configureMinimalGame();

        Rectangle realm = new Rectangle(40, 50, 600, 70);

        DbBotConfig dbBotConfig = botService.getDbBotConfigCrudServiceHelper().createDbChild();
        dbBotConfig.setName("qaywsx");
        dbBotConfig.setActionDelay(202);
        dbBotConfig.setRealm(realm);
        dbBotConfig.setMaxActiveMs(101L);
        dbBotConfig.setMinActiveMs(202L);
        dbBotConfig.setMaxInactiveMs(303L);
        dbBotConfig.setMinInactiveMs(404L);

        BotConfig botConfig = dbBotConfig.createBotConfig(itemService);
        Assert.assertEquals((int) dbBotConfig.getId(), botConfig.hashCode());
        Assert.assertEquals(202, botConfig.getActionDelay());
        Assert.assertEquals(realm, botConfig.getRealm());
        Assert.assertEquals("qaywsx", botConfig.getName());
        Assert.assertEquals(101L, (long) botConfig.getMaxActiveMs());
        Assert.assertEquals(202L, (long) botConfig.getMinActiveMs());
        Assert.assertEquals(303L, (long) botConfig.getMaxInactiveMs());
        Assert.assertEquals(404L, (long) botConfig.getMinInactiveMs());
    }

}
