package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbClip;
import com.btxtech.game.services.media.SoundService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 14.11.12
 * Time: 01:30
 */
public class TestItem extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private ClipService clipService;

    @Test
    @DirtiesContext
    public void explosionClip() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbClip dbClip1 = clipService.getClipLibraryCrud().createDbChild();
        clipService.getClipLibraryCrud().updateDbChild(dbClip1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        configureSimplePlanetNoResources();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemType itemType = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertNull(itemType.getExplosionClipId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Config
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbItemType dbItemType = serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID);
        dbItemType.setExplosionClip(clipService.getClipLibraryCrud().readDbChild(dbClip1.getId()));
        serverItemTypeService.saveDbItemType(dbItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemType = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(dbClip1.getId(), itemType.getExplosionClipId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
