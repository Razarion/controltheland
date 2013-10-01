package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbClip;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 16.08.12
 * Time: 01:30
 */
public class TestWeaponType extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private ClipService clipService;

    @Test
    @DirtiesContext
    public void weaponMuzzleFlashClip() throws Exception {
        configureSimplePlanetNoResources();
        // Verify
        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertNull(baseItemType.getWeaponType().getMuzzleFlashClipId());
        // Create clip
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbClip dbClip = clipService.getClipLibraryCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = dbBaseItemType.getDbWeaponType();
        dbWeaponType.setMuzzleFlashClip(clipService.getClipLibraryCrud().readDbChild(dbClip.getId()));
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        baseItemType = (BaseItemType) serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(dbClip.getId(), baseItemType.getWeaponType().getMuzzleFlashClipId());
    }
}
