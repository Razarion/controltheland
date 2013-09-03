package com.btxtech.game.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotSyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.Need;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 08.08.2011
 * Time: 15:35:50
 */
public class TestNeed extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void oneDirectItem() throws Exception {
        configureSimplePlanetNoResources();

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        BotItemConfig botItemConfig = new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null);
        botItemConfigs.add(botItemConfig);

        Need need = new Need(botItemConfigs);
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(3000, 3000), new Id(1, Id.NO_ID));
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, botItemConfig, null);
        need.onItemAdded(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());

        syncBaseItem.getSyncItemArea().setPosition(new Index(10000, 10000));
        need.onItemRemoved(botSyncBaseItem);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig));
    }

    @Test
    @DirtiesContext
    public void twoDirectOneNormal() throws Exception {
        configureSimplePlanetNoResources();

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        BotItemConfig botItemConfig1 = new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null);
        botItemConfigs.add(botItemConfig1);
        BotItemConfig botItemConfig2 = new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 2, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, null);
        botItemConfigs.add(botItemConfig2);
        BotItemConfig botItemConfig3 = new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID), 1, false, null, false, null, false, null);
        botItemConfigs.add(botItemConfig3);

        Need need = new Need(botItemConfigs);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(3, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig1));
        Assert.assertEquals(2, (int) need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig3));

        SyncBaseItem syncBaseItem1 = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(2900, 2900), new Id(1, Id.NO_ID));
        BotSyncBaseItem botSyncBaseItem1 = new BotSyncBaseItem(syncBaseItem1, botItemConfig1, null);
        need.onItemAdded(botSyncBaseItem1);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(2, need.getEffectiveItemNeed().size());
        Assert.assertEquals(2, (int) need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig3));

        SyncBaseItem syncBaseItem2 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(2500, 2500), new Id(2, Id.NO_ID));
        BotSyncBaseItem botSyncBaseItem2 = new BotSyncBaseItem(syncBaseItem2, botItemConfig2, null);
        need.onItemAdded(botSyncBaseItem2);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(2, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig3));

        SyncBaseItem syncBaseItem3 = createSyncBaseItem(TEST_ATTACK_ITEM_ID, new Index(3100, 3100), new Id(3, 1));
        BotSyncBaseItem botSyncBaseItem3 = new BotSyncBaseItem(syncBaseItem3, botItemConfig3, null);
        need.onItemAdded(botSyncBaseItem3);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig2));

        SyncBaseItem syncBaseItem4 = createSyncBaseItem(TEST_HARVESTER_ITEM_ID, new Index(2700, 2700), new Id(4, Id.NO_ID));
        BotSyncBaseItem botSyncBaseItem4 = new BotSyncBaseItem(syncBaseItem4, botItemConfig2, null);
        need.onItemAdded(botSyncBaseItem4);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());


        need.onItemRemoved(botSyncBaseItem3);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig3));

        need.onItemRemoved(botSyncBaseItem4);
        Assert.assertEquals(2, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig2));
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig3));
    }

    @Test
    @DirtiesContext
    public void noRebuild() throws Exception {
        configureSimplePlanetNoResources();

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        BotItemConfig botItemConfig = new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, true, null);
        botItemConfigs.add(botItemConfig);

        Need need = new Need(botItemConfigs);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(3000, 3000), new Id(1, Id.NO_ID));
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
        configureSimplePlanetNoResources();

        Collection<BotItemConfig> botItemConfigs = new ArrayList<>();
        BotItemConfig botItemConfig = new BotItemConfig((BaseItemType) serverItemTypeService.getItemType(TEST_START_BUILDER_ITEM_ID), 1, true, createRegion(new Rectangle(2000, 2000, 1000, 1000), 1), false, null, false, 100L);
        botItemConfigs.add(botItemConfig);

        Need need = new Need(botItemConfigs);
        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig));

        SyncBaseItem syncBaseItem = createSyncBaseItem(TEST_START_BUILDER_ITEM_ID, new Index(3000, 3000), new Id(1, Id.NO_ID));
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, botItemConfig, null);
        need.onItemAdded(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());

        syncBaseItem.getSyncItemArea().setPosition(new Index(10000, 10000));
        need.onItemRemoved(botSyncBaseItem);
        Assert.assertTrue(need.getEffectiveItemNeed().isEmpty());

        Thread.sleep(110);

        Assert.assertFalse(need.getEffectiveItemNeed().isEmpty());
        Assert.assertEquals(1, need.getEffectiveItemNeed().size());
        Assert.assertEquals(1, (int) need.getEffectiveItemNeed().get(botItemConfig));
    }

}
