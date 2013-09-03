package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.media.SoundService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 16.08.12
 * Time: 01:30
 */
public class TestItemSound extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private SoundService soundService;

    @Test
    @DirtiesContext
    public void weaponTypeSound() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSound sound1 = soundService.getSoundLibraryCrud().createDbChild();
        sound1.setName("sound1");
        sound1.setDataMp3(new byte[]{0, 1, 2, 3});
        sound1.setDataOgg(new byte[]{4, 5, 6, 7});
        soundService.getSoundLibraryCrud().updateDbChild(sound1);
        int soundId1 = sound1.getId();
        DbSound sound2 = soundService.getSoundLibraryCrud().createDbChild();
        sound2.setName("sound2");
        sound2.setDataMp3(new byte[]{1, 2, 3, 43});
        sound2.setDataOgg(new byte[]{5, 6, 7, 8});
        soundService.getSoundLibraryCrud().updateDbChild(sound2);
        int soundId2 = sound2.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        configureSimplePlanetNoResources();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        ItemType itemType = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertNull(itemType.getBuildupSound());
        Assert.assertNull(itemType.getSelectionSound());
        Assert.assertNull(itemType.getBuildupSound());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Config
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbItemType dbItemType = serverItemTypeService.getDbItemType(TEST_ATTACK_ITEM_ID);
        dbItemType.setSelectionSound(soundService.getSoundLibraryCrud().readDbChild(soundId1));
        dbItemType.setBuildupSound(soundService.getSoundLibraryCrud().readDbChild(soundId2));
        dbItemType.setCommandSound(soundService.getSoundLibraryCrud().readDbChild(soundId1));
        serverItemTypeService.saveDbItemType(dbItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemType = serverItemTypeService.getItemType(TEST_ATTACK_ITEM_ID);
        Assert.assertEquals(soundId1, (int)itemType.getSelectionSound());
        Assert.assertEquals(soundId2, (int)itemType.getBuildupSound());
        Assert.assertEquals(soundId1, (int)itemType.getCommandSound());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
