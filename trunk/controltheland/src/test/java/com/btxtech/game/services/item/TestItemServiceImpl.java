package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.impl.ItemServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 20:29:24
 */
public class TestItemServiceImpl extends AbstractServiceTest {
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void isSyncItemOverlapping() throws Exception {
        configureRealGame();

        SimpleBase base1 = new SimpleBase(1);
        SimpleBase base2 = new SimpleBase(2);


        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        setPrivateField(ItemServiceImpl.class, itemService, "baseService", baseService);

        ActionService actionService = EasyMock.createNiceMock(ActionService.class);
        setPrivateField(ItemServiceImpl.class, itemService, "actionService", actionService);

        EasyMock.replay(baseService, actionService);


        ItemType itemType1 = itemService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(80, 80, ANGELS_24));
        itemService.createSyncObject(itemType1, new Index(4486, 1279), null, base1, 0);

        ItemType itemType2 = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(36, 56, ANGELS_24));
        SyncItem syncItem2 = itemService.createSyncObject(itemType2, new Index(1396, 2225), null, base2, 0);


        Assert.assertFalse(itemService.isSyncItemOverlapping(syncItem2, new Index(1425, 2331), null, null));
    }

    @Test
    @DirtiesContext
    public void isSyncItemOverlappingAngel() throws Exception {
        configureRealGame();

        SimpleBase base1 = new SimpleBase(1);
        SimpleBase base2 = new SimpleBase(2);


        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        setPrivateField(ItemServiceImpl.class, itemService, "baseService", baseService);

        ActionService actionService = EasyMock.createNiceMock(ActionService.class);
        setPrivateField(ItemServiceImpl.class, itemService, "actionService", actionService);

        EasyMock.replay(baseService, actionService);


        ItemType itemType1 = itemService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(182, 120, ANGELS_24));
        itemService.createSyncObject(itemType1, new Index(2820, 2626), null, base1, 0);

        ItemType itemType2 = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(54, 60, ANGELS_24));
        SyncItem syncItem2 = itemService.createSyncObject(itemType2, new Index(2940, 2609), null, base2, 0);

        Assert.assertFalse(itemService.isSyncItemOverlapping(syncItem2, new Index(2940, 2609), null, null));
        Assert.assertTrue(itemService.isSyncItemOverlapping(syncItem2, new Index(2940, 2609), 0.2053953891897674, null));
    }
}
