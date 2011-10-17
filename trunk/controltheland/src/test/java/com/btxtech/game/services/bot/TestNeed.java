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
        configureMinimalGame();

        DbBotItemConfig config1 = new DbBotItemConfig();
        config1.setCount(1);
        config1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        config1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        config1.setCreateDirectly(true);

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();
        dbBotItemConfigs.add(config1);

        List<BotItemConfig> botItemConfigs = TestBotItemContainer.convert(dbBotItemConfigs, itemService);

        Need need = new Need(botItemConfigs);
        Assert.assertEquals(1, need.getNeedCount());
        Assert.assertEquals(1, need.getItemNeed().size());
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(0)));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(3000, 3000), new Id(1, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, null);
        need.onItemAdded(botSyncBaseItem);

        Assert.assertEquals(0, need.getNeedCount());
        Assert.assertEquals(0, need.getItemNeed().size());

        syncBaseItem.getSyncItemArea().setPosition(new Index(10000, 10000));
        need.onItemRemoved(botSyncBaseItem);
        Assert.assertEquals(1, need.getNeedCount());
        Assert.assertEquals(1, need.getItemNeed().size());
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(0)));
    }

    @Test
    @DirtiesContext
    public void threeDirectOneNormal() throws Exception {
        configureMinimalGame();

        Collection<DbBotItemConfig> dbBotItemConfigs = new ArrayList<DbBotItemConfig>();

        DbBotItemConfig direct1 = new DbBotItemConfig();
        direct1.setCount(1);
        direct1.setBaseItemType(itemService.getDbBaseItemType(TEST_START_BUILDER_ITEM_ID));
        direct1.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        direct1.setCreateDirectly(true);
        dbBotItemConfigs.add(direct1);

        DbBotItemConfig direct2 = new DbBotItemConfig();
        direct2.setCount(2);
        direct2.setBaseItemType(itemService.getDbBaseItemType(TEST_HARVESTER_ITEM_ID));
        direct2.setRegion(new Rectangle(2000, 2000, 1000, 1000));
        direct2.setCreateDirectly(true);
        dbBotItemConfigs.add(direct2);

        DbBotItemConfig normal1 = new DbBotItemConfig();
        normal1.setCount(1);
        normal1.setBaseItemType(itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID));
        normal1.setCreateDirectly(false);
        dbBotItemConfigs.add(normal1);

        Collection<BotItemConfig> botItemConfigs = TestBotItemContainer.convert(dbBotItemConfigs, itemService);

        Need need = new Need(botItemConfigs);
        Assert.assertEquals(4, need.getNeedCount());
        Assert.assertEquals(3, need.getItemNeed().size());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, need.getItemNeed().get(2).getBaseItemType().getId());

        SyncBaseItem syncBaseItem1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(2900, 2900), new Id(1, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem1 = new BotSyncBaseItem(syncBaseItem1, null);
        need.onItemAdded(botSyncBaseItem1);
        Assert.assertEquals(3, need.getNeedCount());
        Assert.assertEquals(2, need.getItemNeed().size());
        Assert.assertEquals(2, need.getNeedCount(need.getItemNeed().get(0)));
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(1)));
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, need.getItemNeed().get(0).getBaseItemType().getId());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, need.getItemNeed().get(1).getBaseItemType().getId());

        SyncBaseItem syncBaseItem2 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(2500, 2500), new Id(2, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem2 = new BotSyncBaseItem(syncBaseItem2, null);
        need.onItemAdded(botSyncBaseItem2);
        Assert.assertEquals(2, need.getNeedCount());
        Assert.assertEquals(2, need.getItemNeed().size());
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(0)));
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(1)));
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, need.getItemNeed().get(0).getBaseItemType().getId());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, need.getItemNeed().get(1).getBaseItemType().getId());

        SyncBaseItem syncBaseItem3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(3100, 3100), new Id(3, 1, 0));
        BotSyncBaseItem botSyncBaseItem3 = new BotSyncBaseItem(syncBaseItem3, null);
        need.onItemAdded(botSyncBaseItem3);
        Assert.assertEquals(1, need.getNeedCount());
        Assert.assertEquals(1, need.getItemNeed().size());
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(0)));
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, need.getItemNeed().get(0).getBaseItemType().getId());

        SyncBaseItem syncBaseItem4 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(2700, 2700), new Id(4, Id.NO_ID, 0));
        BotSyncBaseItem botSyncBaseItem4 = new BotSyncBaseItem(syncBaseItem4, null);
        need.onItemAdded(botSyncBaseItem4);
        Assert.assertEquals(0, need.getNeedCount());
        Assert.assertEquals(0, need.getItemNeed().size());

        need.onItemRemoved(botSyncBaseItem3);
        Assert.assertEquals(1, need.getNeedCount());
        Assert.assertEquals(1, need.getItemNeed().size());
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(0)));
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, need.getItemNeed().get(0).getBaseItemType().getId());

        need.onItemRemoved(botSyncBaseItem4);
        Assert.assertEquals(2, need.getNeedCount());
        Assert.assertEquals(2, need.getItemNeed().size());
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(0)));
        Assert.assertEquals(1, need.getNeedCount(need.getItemNeed().get(1)));
        Assert.assertEquals(TEST_HARVESTER_ITEM_ID, need.getItemNeed().get(0).getBaseItemType().getId());
        Assert.assertEquals(TEST_ATTACK_ITEM_ID, need.getItemNeed().get(1).getBaseItemType().getId());

    }

}
