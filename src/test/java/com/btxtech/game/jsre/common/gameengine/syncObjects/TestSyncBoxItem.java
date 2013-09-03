package com.btxtech.game.jsre.common.gameengine.syncObjects;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import junit.framework.Assert;
import org.junit.Test;

/**
 * User: beat
 * Date: 18.05.12
 * Time: 15:20
 */
public class TestSyncBoxItem {
    @Test
    public void testTtl() throws InterruptedException {
        BoxItemType boxItemType = new BoxItemType();
        boxItemType.setTtl(150);

        SyncBoxItem syncBoxItem = new SyncBoxItem(new Id(-1, -1), new Index(1000, 1000), boxItemType, null, null);
        Assert.assertTrue(syncBoxItem.isAlive());
        Assert.assertTrue(syncBoxItem.isInTTL());
        Thread.sleep(160);
        Assert.assertFalse(syncBoxItem.isInTTL());
        Assert.assertTrue(syncBoxItem.isAlive());
        syncBoxItem.kill();
        Assert.assertFalse(syncBoxItem.isInTTL());
        Assert.assertFalse(syncBoxItem.isAlive());
    }
}
