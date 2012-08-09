package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * User: beat
 * Date: 02.08.12
 * Time: 22:27
 */
public class TestItemTypeSpriteMap {
    @Test
    public void testRuntime() {
        BoundingBox boundingBox = new BoundingBox(80, 120, new double[]{0.0, 0.1, 0.2, 0.3, 0.4});
        ItemTypeSpriteMap itemTypeSpriteMap = new ItemTypeSpriteMap(boundingBox, 100, 200, 4, 3, 25, 4, 30, 5, 6, 40);

        SyncItemArea syncItemArea = EasyMock.createNiceMock(SyncItemArea.class);
        EasyMock.expect(syncItemArea.getAngel()).andReturn(0.0).anyTimes();
        SyncBaseItem syncBaseItemMock = EasyMock.createNiceMock(SyncBaseItem.class);
        EasyMock.expect(syncBaseItemMock.isReady()).andReturn(true).anyTimes();
        EasyMock.expect(syncBaseItemMock.isHealthy()).andReturn(true).anyTimes();
        EasyMock.expect(syncBaseItemMock.getSyncItemArea()).andReturn(syncItemArea).anyTimes();
        EasyMock.replay(syncItemArea, syncBaseItemMock);

        Assert.assertEquals(new Index(1200, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 0));
        Assert.assertEquals(new Index(1200, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 29));
        Assert.assertEquals(new Index(1300, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 30));
        Assert.assertEquals(new Index(1300, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 31));
        Assert.assertEquals(new Index(1400, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 60));
        Assert.assertEquals(new Index(1500, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 90));
        Assert.assertEquals(new Index(1200, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 120));
        Assert.assertEquals(new Index(1300, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 155));
        Assert.assertEquals(new Index(1400, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 185));
        Assert.assertEquals(new Index(1500, 0), itemTypeSpriteMap.getItemTypeImageOffset(syncBaseItemMock, 215));
    }
}
