package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.Need;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ItemService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 08.08.2011
 * Time: 15:35:50
 */
public class TestNeed extends AbstractServiceTest {
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void oneDirectItem() throws Exception {
        configureRealGame();

        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        dbBotItemConfigs.add(config1);

        List<BotItemConfig> botItemConfigs = TestBotItemContainer.convert(dbBotItemConfigs, itemService);
        BotItemConfig botItemConfig = botItemConfigs.get(0);

        Need need = new Need(botItemConfigs);
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(3000, 3000), new Id(1, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, botItemConfig, null);
        need.onItemAdded(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());

        syncBaseItem.getSyncItemArea().setPosition(new Index(10000, 10000));
        need.onItemRemoved(botSyncBaseItem);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig));
    }

    @Test
    @DirtiesContext
    public void twoDirectOneNormal() throws Exception {
        configureRealGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();

        DbBotItemConfig direct1 = new DbBotItemConfig();
        direct1.setCount(1);
        direct1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        direct1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        direct1.setCreateDirectly(true);
        dbBotItemConfigs.add(direct1);

        DbBotItemConfig direct2 = new DbBotItemConfig();
        direct2.setCount(2);
        direct2.setBaseItemType(getDbBaseItemTypeInSession(TEST_HARVESTER_ITEM_ID));
        direct2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        direct2.setCreateDirectly(true);
        dbBotItemConfigs.add(direct2);

        DbBotItemConfig normal1 = new DbBotItemConfig();
        normal1.setCount(1);
        normal1.setBaseItemType(getDbBaseItemTypeInSession(TEST_ATTACK_ITEM_ID));
        normal1.setCreateDirectly(false);
        dbBotItemConfigs.add(normal1);

        List<BotItemConfig> botItemConfigs = TestBotItemContainer.convert(dbBotItemConfigs, itemService);
        BotItemConfig botItemConfig1 = botItemConfigs.get(0);
        BotItemConfig botItemConfig2 = botItemConfigs.get(1);
        BotItemConfig botItemConfig3 = botItemConfigs.get(2);

        Need need = new Need(botItemConfigs);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(3, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig1));
        Assert.assertEquals(2,  (int)need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig3));

        SyncBaseItem syncBaseItem1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(2900, 2900), new Id(1, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem1 = new BotSyncBaseItem(syncBaseItem1, botItemConfig1, null);
        need.onItemAdded(botSyncBaseItem1);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(2, need.getEffectiveItemNeed().size());
        Assert.assertEquals(2,  (int)need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig3));

        SyncBaseItem syncBaseItem2 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(2500, 2500), new Id(2, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem2 = new BotSyncBaseItem(syncBaseItem2, botItemConfig2, null);
        need.onItemAdded(botSyncBaseItem2);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(2, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig3));

        SyncBaseItem syncBaseItem3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(3100, 3100), new Id(3, 1, 0));
        BotSyncBaseItem botSyncBaseItem3 = new BotSyncBaseItem(syncBaseItem3, botItemConfig3, null);
        need.onItemAdded(botSyncBaseItem3);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig2));

        SyncBaseItem syncBaseItem4 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(2700, 2700), new Id(4, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem4 = new BotSyncBaseItem(syncBaseItem4, botItemConfig2, null);
        need.onItemAdded(botSyncBaseItem4);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());


        need.onItemRemoved(botSyncBaseItem3);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig3));

        need.onItemRemoved(botSyncBaseItem4);
        Assert.assertEquals(2, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig3));
    }

    @Test
    @DirtiesContext
    public void noRebuild() throws Exception {
        configureRealGame();

        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        config1.setNoRebuild(true);

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        dbBotItemConfigs.add(config1);

        List<BotItemConfig> botItemConfigs = TestBotItemContainer.convert(dbBotItemConfigs, itemService);
        BotItemConfig botItemConfig = botItemConfigs.get(0);

        Need need = new Need(botItemConfigs);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(3000, 3000), new Id(1, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, botItemConfig, null);
        need.onItemAdded(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());

        syncBaseItem.getSyncItemArea().setPosition(new Index(10000, 10000));
        need.onItemRemoved(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());
    }

    @Test
    @DirtiesContext
    public void rePop() throws Exception {
        configureRealGame();

        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(getDbBaseItemTypeInSession(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);
        config1.setRePopTime(100L);

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        dbBotItemConfigs.add(config1);

        List<BotItemConfig> botItemConfigs = TestBotItemContainer.convert(dbBotItemConfigs, itemService);
        BotItemConfig botItemConfig = botItemConfigs.get(0);

        Need need = new Need(botItemConfigs);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(3000, 3000), new Id(1, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, botItemConfig, null);
        need.onItemAdded(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());

        syncBaseItem.getSyncItemArea().setPosition(new Index(10000, 10000));
        need.onItemRemoved(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());

        Thread.sleep(110);

        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1,  (int)need.getEffectiveItemNeed().get(botItemConfig));
    }

}
