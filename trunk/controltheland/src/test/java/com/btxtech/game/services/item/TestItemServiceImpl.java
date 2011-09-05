package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.services.AbstractServiceTest;
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
}
