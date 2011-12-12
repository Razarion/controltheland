package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 11.12.2011
 * Time: 23:51:18
 */
public class TestWeaponType extends AbstractServiceTest {
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void muzzleFlashPosition() throws Exception {
        configureMinimalGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();

        DbBaseItemType dbBaseItemType = itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = dbBaseItemType.getDbWeaponType();
        WeaponType weaponType = dbWeaponType.createWeaponType(3);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        weaponType.setMuzzleFlashPosition(0, 0, new Index(9, 10));
        weaponType.setMuzzleFlashPosition(0, 1, new Index(11, 12));
        weaponType.setMuzzleFlashPosition(0, 2, new Index(13, 14));
        itemService.saveWeaponType(dbBaseItemType.getId(), weaponType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(9, 10), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(11, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(13, 14), weaponType.getMuzzleFlashPosition(0, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemService.saveWeaponType(dbBaseItemType.getId(), weaponType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(9, 10), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(11, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(13, 14), weaponType.getMuzzleFlashPosition(0, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        weaponType.changeMuzzleFlashCount(3);
        weaponType.setMuzzleFlashPosition(1, 0, new Index(10, 11));
        weaponType.setMuzzleFlashPosition(1, 1, new Index(10, 12));
        weaponType.setMuzzleFlashPosition(1, 2, new Index(10, 13));
        weaponType.setMuzzleFlashPosition(2, 0, new Index(20, 11));
        weaponType.setMuzzleFlashPosition(2, 1, new Index(20, 12));
        weaponType.setMuzzleFlashPosition(2, 2, new Index(20, 13));
        itemService.saveWeaponType(dbBaseItemType.getId(), weaponType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(3, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(9, 10), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(11, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(13, 14), weaponType.getMuzzleFlashPosition(0, 2));
        Assert.assertEquals(new Index(10, 11), weaponType.getMuzzleFlashPosition(1, 0));
        Assert.assertEquals(new Index(10, 12), weaponType.getMuzzleFlashPosition(1, 1));
        Assert.assertEquals(new Index(10, 13), weaponType.getMuzzleFlashPosition(1, 2));
        Assert.assertEquals(new Index(20, 11), weaponType.getMuzzleFlashPosition(2, 0));
        Assert.assertEquals(new Index(20, 12), weaponType.getMuzzleFlashPosition(2, 1));
        Assert.assertEquals(new Index(20, 13), weaponType.getMuzzleFlashPosition(2, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        weaponType.changeMuzzleFlashCount(1);
        weaponType.setMuzzleFlashPosition(0, 0, new Index(10, 11));
        weaponType.setMuzzleFlashPosition(0, 1, new Index(10, 12));
        weaponType.setMuzzleFlashPosition(0, 2, new Index(10, 13));
        itemService.saveWeaponType(dbBaseItemType.getId(), weaponType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = itemService.getDbBaseItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        weaponType = dbWeaponType.createWeaponType(3);
        Assert.assertEquals(1, weaponType.getMuzzleFlashCount());
        Assert.assertEquals(new Index(10, 11), weaponType.getMuzzleFlashPosition(0, 0));
        Assert.assertEquals(new Index(10, 12), weaponType.getMuzzleFlashPosition(0, 1));
        Assert.assertEquals(new Index(10, 13), weaponType.getMuzzleFlashPosition(0, 2));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
