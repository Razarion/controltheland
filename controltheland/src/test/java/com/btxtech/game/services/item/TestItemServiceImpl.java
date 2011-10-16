package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.item.impl.ItemServiceImpl;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.awt.*;

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
    public void testSaveBoundingBox() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        BoundingBox boundingBox = itemService.getBoundingBox(TEST_ATTACK_ITEM_ID);
        endHttpRequestAndOpenSessionInViewFilter();

        Assert.assertEquals(boundingBox.getWidth(), 80);
        Assert.assertEquals(boundingBox.getHeight(), 80);
        Assert.assertEquals(boundingBox.getImageWidth(), 100);
        Assert.assertEquals(boundingBox.getImageHeight(), 100);
        Assert.assertEquals(boundingBox.getImageCount(), 1);

        beginHttpRequestAndOpenSessionInViewFilter();
        itemService.saveBoundingBox(TEST_ATTACK_ITEM_ID, new BoundingBox(101, 102, 103, 104, 105));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        boundingBox = itemService.getDbItemType(TEST_ATTACK_ITEM_ID).getBoundingBox();
        Assert.assertEquals(boundingBox.getWidth(), 103);
        Assert.assertEquals(boundingBox.getHeight(), 104);
        Assert.assertEquals(boundingBox.getImageWidth(), 101);
        Assert.assertEquals(boundingBox.getImageHeight(), 102);
        Assert.assertEquals(boundingBox.getImageCount(), 105);
    }

    @Test
    @DirtiesContext
    public void isSyncItemOverlapping() throws Exception {
        configureMinimalGame();

        SimpleBase base1 = new SimpleBase(1);
        SimpleBase base2 = new SimpleBase(2);


        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        setPrivateField(ItemServiceImpl.class, itemService, "baseService", baseService);

        EasyMock.replay(baseService);


        ItemType itemType1 = itemService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(100, 100, 80, 80, 1));
        itemService.createSyncObject(itemType1, new Index(4486, 1279), null, base1, 0);

        ItemType itemType2 = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(70, 70, 36, 56, 24));
        SyncItem syncItem2 = itemService.createSyncObject(itemType2, new Index(1396, 2225), null, base2, 0);


        Assert.assertFalse(itemService.isSyncItemOverlapping(syncItem2, new Index(1425, 2331), null, null));
    }

    @Test
    @DirtiesContext
    public void isSyncItemOverlappingAngel() throws Exception {
        configureMinimalGame();

        SimpleBase base1 = new SimpleBase(1);
        SimpleBase base2 = new SimpleBase(2);


        BaseService baseService = EasyMock.createNiceMock(BaseService.class);
        EasyMock.expect(baseService.isAlive(EasyMock.<SimpleBase>anyObject())).andReturn(true).anyTimes();
        setPrivateField(ItemServiceImpl.class, itemService, "baseService", baseService);

        EasyMock.replay(baseService);


        ItemType itemType1 = itemService.getItemType(TEST_HARVESTER_ITEM_ID);
        itemType1.setBoundingBox(new BoundingBox(180, 130, 182, 120, 1));
        itemService.createSyncObject(itemType1, new Index(2820, 2626), null, base1, 0);

        ItemType itemType2 = itemService.getItemType(TEST_ATTACK_ITEM_ID);
        itemType2.setBoundingBox(new BoundingBox(80, 80, 54, 60, 24));
        SyncItem syncItem2 = itemService.createSyncObject(itemType2, new Index(2940, 2609), null, base2, 0);

        Assert.assertFalse(itemService.isSyncItemOverlapping(syncItem2, new Index(2940, 2609), null, null));
        Assert.assertTrue(itemService.isSyncItemOverlapping(syncItem2, new Index(2940, 2609), 0.2053953891897674, null));
    }

}
