package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.CommonSoundInfo;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 16.08.12
 * Time: 00:44
 */
public class TestSoundService extends AbstractServiceTest {
    @Autowired
    private SoundService soundService;
    private int SOUND1_ID;
    private int SOUND2_ID;

    @Test
    @DirtiesContext
    public void testDbSoundDb() throws Exception {
        configureSimplePlanetNoResources();
        // Configure
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSound sound1 = soundService.getSoundLibraryCrud().createDbChild();
        sound1.setName("sound1");
        sound1.setDataMp3(new byte[]{0, 1, 2, 3});
        sound1.setDataOgg(new byte[]{4, 5, 6, 7});
        soundService.getSoundLibraryCrud().updateDbChild(sound1);
        DbSound sound2 = soundService.getSoundLibraryCrud().createDbChild();
        sound2.setName("sound2");
        sound2.setDataMp3(new byte[]{1, 2, 3, 43});
        sound2.setDataOgg(new byte[]{5, 6, 7, 8});
        soundService.getSoundLibraryCrud().updateDbChild(sound2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, soundService.getSoundLibraryCrud().readDbChildren().size());
        sound1 = soundService.getSoundLibraryCrud().readDbChild(sound1.getId());
        Assert.assertEquals("sound1", sound1.getName());
        Assert.assertArrayEquals(new byte[]{0, 1, 2, 3}, sound1.getDataMp3());
        Assert.assertArrayEquals(new byte[]{4, 5, 6, 7}, sound1.getDataOgg());
        sound2 = soundService.getSoundLibraryCrud().readDbChild(sound2.getId());
        Assert.assertEquals("sound2", sound2.getName());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 43}, sound2.getDataMp3());
        Assert.assertArrayEquals(new byte[]{5, 6, 7, 8}, sound2.getDataOgg());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, soundService.getSoundLibraryCrud().readDbChildren().size());
        sound1 = soundService.getSoundLibraryCrud().readDbChild(sound1.getId());
        sound1.setName("sound3");
        sound1.setDataMp3(new byte[]{9, 8, 7, 6});
        sound1.setDataOgg(new byte[]{3, 9, 2, 6});
        soundService.getSoundLibraryCrud().updateDbChild(sound1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, soundService.getSoundLibraryCrud().readDbChildren().size());
        sound1 = soundService.getSoundLibraryCrud().readDbChild(sound1.getId());
        Assert.assertEquals("sound3", sound1.getName());
        Assert.assertArrayEquals(new byte[]{9, 8, 7, 6}, sound1.getDataMp3());
        Assert.assertArrayEquals(new byte[]{3, 9, 2, 6}, sound1.getDataOgg());
        sound2 = soundService.getSoundLibraryCrud().readDbChild(sound2.getId());
        Assert.assertEquals("sound2", sound2.getName());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 43}, sound2.getDataMp3());
        Assert.assertArrayEquals(new byte[]{5, 6, 7, 8}, sound2.getDataOgg());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Delete
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sound1 = soundService.getSoundLibraryCrud().readDbChild(sound1.getId());
        soundService.getSoundLibraryCrud().deleteDbChild(sound1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, soundService.getSoundLibraryCrud().readDbChildren().size());
        sound2 = soundService.getSoundLibraryCrud().readDbChild(sound2.getId());
        Assert.assertEquals("sound2", sound2.getName());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 43}, sound2.getDataMp3());
        Assert.assertArrayEquals(new byte[]{5, 6, 7, 8}, sound2.getDataOgg());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Delete
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        sound2 = soundService.getSoundLibraryCrud().readDbChild(sound2.getId());
        soundService.getSoundLibraryCrud().deleteDbChild(sound2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, soundService.getSoundLibraryCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    private void setupSound() throws Exception {
        // Configure
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSound sound1 = soundService.getSoundLibraryCrud().createDbChild();
        sound1.setName("sound1");
        sound1.setDataMp3(new byte[]{0, 1, 2, 3});
        sound1.setDataOgg(new byte[]{4, 5, 6, 7});
        soundService.getSoundLibraryCrud().updateDbChild(sound1);
        SOUND1_ID = sound1.getId();
        DbSound sound2 = soundService.getSoundLibraryCrud().createDbChild();
        sound2.setName("sound2");
        sound2.setDataMp3(new byte[]{1, 2, 3, 43});
        sound2.setDataOgg(new byte[]{5, 6, 7, 8});
        soundService.getSoundLibraryCrud().updateDbChild(sound2);
        SOUND2_ID = sound2.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testDbCommonSoundDb() throws Exception {
        configureSimplePlanetNoResources();
        setupSound();
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbCommonSound commonSound1 = soundService.getCommonSoundCrud().createDbChild();
        commonSound1.setType(DbCommonSound.Type.BACKGROUND_MUSIC);
        commonSound1.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND1_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound1);
        DbCommonSound commonSound2 = soundService.getCommonSoundCrud().createDbChild();
        commonSound2.setType(DbCommonSound.Type.BUILDING_LOST);
        commonSound2.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND2_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, soundService.getCommonSoundCrud().readDbChildren().size());
        commonSound1 = soundService.getCommonSoundCrud().readDbChild(commonSound1.getId());
        Assert.assertEquals(DbCommonSound.Type.BACKGROUND_MUSIC, commonSound1.getType());
        Assert.assertEquals(SOUND1_ID, (int) commonSound1.getDbSound().getId());
        commonSound2 = soundService.getCommonSoundCrud().readDbChild(commonSound2.getId());
        Assert.assertEquals(DbCommonSound.Type.BUILDING_LOST, commonSound2.getType());
        Assert.assertEquals(SOUND2_ID, (int) commonSound2.getDbSound().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, soundService.getCommonSoundCrud().readDbChildren().size());
        commonSound1 = soundService.getCommonSoundCrud().readDbChild(commonSound1.getId());
        commonSound1.setType(DbCommonSound.Type.BUILDING_KILLED);
        commonSound1.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND2_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, soundService.getCommonSoundCrud().readDbChildren().size());
        commonSound1 = soundService.getCommonSoundCrud().readDbChild(commonSound1.getId());
        Assert.assertEquals(DbCommonSound.Type.BUILDING_KILLED, commonSound1.getType());
        Assert.assertEquals(SOUND2_ID, (int) commonSound1.getDbSound().getId());
        commonSound2 = soundService.getCommonSoundCrud().readDbChild(commonSound2.getId());
        Assert.assertEquals(DbCommonSound.Type.BUILDING_LOST, commonSound2.getType());
        Assert.assertEquals(SOUND2_ID, (int) commonSound2.getDbSound().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, soundService.getCommonSoundCrud().readDbChildren().size());
        commonSound1 = soundService.getCommonSoundCrud().readDbChild(commonSound1.getId());
        soundService.getCommonSoundCrud().deleteDbChild(commonSound1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, soundService.getCommonSoundCrud().readDbChildren().size());
        commonSound2 = soundService.getCommonSoundCrud().readDbChild(commonSound2.getId());
        Assert.assertEquals(DbCommonSound.Type.BUILDING_LOST, commonSound2.getType());
        Assert.assertEquals(SOUND2_ID, (int) commonSound2.getDbSound().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        commonSound2 = soundService.getCommonSoundCrud().readDbChild(commonSound2.getId());
        soundService.getCommonSoundCrud().deleteDbChild(commonSound2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, soundService.getCommonSoundCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createCommonSoundInfo() throws Exception {
        configureSimplePlanetNoResources();
        setupSound();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CommonSoundInfo commonSoundInfo = soundService.getCommonSoundInfo();
        Assert.assertNull(commonSoundInfo.getBackgroundMusicSoundId());
        Assert.assertNull(commonSoundInfo.getBuildingLostSoundId());
        Assert.assertNull(commonSoundInfo.getBuildingKilledSoundId());
        Assert.assertNull(commonSoundInfo.getUnitLostSoundId());
        Assert.assertNull(commonSoundInfo.getUnitKilledSoundId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbCommonSound commonSound1 = soundService.getCommonSoundCrud().createDbChild();
        commonSound1.setType(DbCommonSound.Type.BACKGROUND_MUSIC);
        commonSound1.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND1_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound1);
        DbCommonSound commonSound2 = soundService.getCommonSoundCrud().createDbChild();
        commonSound2.setType(DbCommonSound.Type.BUILDING_LOST);
        commonSound2.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND2_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        commonSoundInfo = soundService.getCommonSoundInfo();
        Assert.assertEquals(SOUND1_ID, (int) commonSoundInfo.getBackgroundMusicSoundId());
        Assert.assertEquals(SOUND2_ID, (int) commonSoundInfo.getBuildingLostSoundId());
        Assert.assertNull(commonSoundInfo.getBuildingKilledSoundId());
        Assert.assertNull(commonSoundInfo.getUnitLostSoundId());
        Assert.assertNull(commonSoundInfo.getUnitKilledSoundId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Create
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbCommonSound commonSound = soundService.getCommonSoundCrud().createDbChild();
        commonSound.setType(DbCommonSound.Type.UNIT_LOST);
        commonSound.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND1_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound);
        commonSound = soundService.getCommonSoundCrud().createDbChild();
        commonSound.setType(DbCommonSound.Type.BUILDING_LOST);
        commonSound.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND2_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound);
        commonSound = soundService.getCommonSoundCrud().createDbChild();
        commonSound.setType(DbCommonSound.Type.UNIT_KILLED);
        commonSound.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND1_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound);
        commonSound = soundService.getCommonSoundCrud().createDbChild();
        commonSound.setType(DbCommonSound.Type.BUILDING_KILLED);
        commonSound.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND2_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound);
        commonSound = soundService.getCommonSoundCrud().createDbChild();
        commonSound.setType(DbCommonSound.Type.BACKGROUND_MUSIC);
        commonSound.setDbSound(soundService.getSoundLibraryCrud().readDbChild(SOUND2_ID));
        soundService.getCommonSoundCrud().updateDbChild(commonSound);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        commonSoundInfo = soundService.getCommonSoundInfo();
        Assert.assertEquals(SOUND1_ID, (int) commonSoundInfo.getUnitLostSoundId());
        Assert.assertEquals(SOUND2_ID, (int) commonSoundInfo.getBuildingLostSoundId());
        Assert.assertEquals(SOUND1_ID, (int) commonSoundInfo.getUnitKilledSoundId());
        Assert.assertEquals(SOUND2_ID, (int) commonSoundInfo.getBuildingKilledSoundId());
        Assert.assertEquals(SOUND2_ID, (int) commonSoundInfo.getBackgroundMusicSoundId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
