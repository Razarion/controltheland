package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImageData;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import com.btxtech.game.services.sound.DbSound;
import com.btxtech.game.services.sound.SoundService;
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
    private ItemService itemService;
    @Autowired
    private SoundService soundService;

    @Test
    @DirtiesContext
    public void weaponTypeSound() throws Exception {
        configureRealGame();
        // Verify
        BaseItemType baseItemType = (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertNull(baseItemType.getWeaponType().getSoundId());
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSound sound1 = soundService.getSoundLibraryCrud().createDbChild();
        sound1.setName("sound1");
        sound1.setDataMp3(new byte[]{0, 1, 2, 3});
        sound1.setDataOgg(new byte[]{4, 5, 6, 7});
        soundService.getSoundLibraryCrud().updateDbChild(sound1);
        int soundId = sound1.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = dbBaseItemType.getDbWeaponType();
        dbWeaponType.setSound(soundService.getSoundLibraryCrud().readDbChild(soundId));
        itemService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);
        itemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        baseItemType = (BaseItemType) itemService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(soundId, (int) baseItemType.getWeaponType().getSoundId());
    }

    @Test
    @DirtiesContext
    public void weaponTypeImage() throws Exception {
        configureRealGame();

        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = dbBaseItemType.getDbWeaponType();
        DbItemTypeImageData itemTypeImageData = new DbItemTypeImageData();
        itemTypeImageData.setContentType("xxx/yyy");
        itemTypeImageData.setData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        dbWeaponType.setMuzzleFlashImageData(itemTypeImageData);
        itemService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        dbWeaponType = dbBaseItemType.getDbWeaponType();
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, dbWeaponType.getMuzzleFlashImageData().getData());
        Assert.assertEquals("xxx/yyy", dbWeaponType.getMuzzleFlashImageData().getContentType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
