package com.btxtech.game.services.media;

import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.CommonClipInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * User: beat
 * Date: 08.10.12
 * Time: 12:52
 */
public class TestClipService extends AbstractServiceTest {
    @Autowired
    private ClipService clipService;
    @Autowired
    private SoundService soundService;

    @Test
    @DirtiesContext
    public void imageSpriteMapCrud() throws Exception {
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getImageSpriteMapCrud().readDbChildren().size());
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        dbImageSpriteMap1.setName("test1");
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getImageSpriteMapCrud().readDbChildren().size());
        dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId());
        Assert.assertEquals("test1", dbImageSpriteMap1.getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().createDbChild();
        dbImageSpriteMap2.setName("test2");
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, clipService.getImageSpriteMapCrud().readDbChildren().size());
        dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId());
        Assert.assertEquals("test1", dbImageSpriteMap1.getName());
        dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap2.getId());
        Assert.assertEquals("test2", dbImageSpriteMap2.getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId());
        clipService.getImageSpriteMapCrud().deleteDbChild(dbImageSpriteMap1);
        dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap2.getId());
        dbImageSpriteMap2.setName("test3");
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getImageSpriteMapCrud().readDbChildren().size());
        dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap2.getId());
        Assert.assertEquals("test3", dbImageSpriteMap2.getName());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap2.getId());
        clipService.getImageSpriteMapCrud().deleteDbChild(dbImageSpriteMap2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getImageSpriteMapCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void clipLibraryCrud() throws Exception {
        // Setup sprite map and sound
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        DbImageSpriteMap dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().createDbChild();
        DbSound dbSound1 = soundService.getSoundLibraryCrud().createDbChild();
        DbSound dbSound2 = soundService.getSoundLibraryCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getClipLibraryCrud().readDbChildren().size());
        DbClip dbClip1 = clipService.getClipLibraryCrud().createDbChild();
        dbClip1.setName("aaa1");
        dbClip1.setDbSound(dbSound1);
        dbClip1.setDbImageSpriteMap(dbImageSpriteMap1);
        clipService.getClipLibraryCrud().updateDbChild(dbClip1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getClipLibraryCrud().readDbChildren().size());
        dbClip1 = clipService.getClipLibraryCrud().readDbChild(dbClip1.getId());
        Assert.assertEquals("aaa1", dbClip1.getName());
        Assert.assertEquals(dbImageSpriteMap1.getId(), dbClip1.getDbImageSpriteMap().getId());
        Assert.assertEquals(dbSound1.getId(), dbClip1.getDbSound().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbClip dbClip2 = clipService.getClipLibraryCrud().createDbChild();
        dbClip2.setName("aaa2");
        dbClip2.setDbSound(null);
        dbClip2.setDbImageSpriteMap(dbImageSpriteMap2);
        clipService.getClipLibraryCrud().updateDbChild(dbClip2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, clipService.getClipLibraryCrud().readDbChildren().size());
        dbClip1 = clipService.getClipLibraryCrud().readDbChild(dbClip1.getId());
        Assert.assertEquals("aaa1", dbClip1.getName());
        Assert.assertEquals(dbImageSpriteMap1.getId(), dbClip1.getDbImageSpriteMap().getId());
        Assert.assertEquals(dbSound1.getId(), dbClip1.getDbSound().getId());
        dbClip2 = clipService.getClipLibraryCrud().readDbChild(dbClip2.getId());
        Assert.assertEquals("aaa2", dbClip2.getName());
        Assert.assertEquals(dbImageSpriteMap2.getId(), dbClip2.getDbImageSpriteMap().getId());
        Assert.assertNull(dbClip2.getDbSound());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbClip1 = clipService.getClipLibraryCrud().readDbChild(dbClip1.getId());
        clipService.getClipLibraryCrud().deleteDbChild(dbClip1);
        dbClip2 = clipService.getClipLibraryCrud().readDbChild(dbClip2.getId());
        dbClip2.setName("aaa3");
        dbClip2.setDbSound(dbSound2);
        dbClip2.setDbImageSpriteMap(dbImageSpriteMap1);
        clipService.getClipLibraryCrud().updateDbChild(dbClip2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getClipLibraryCrud().readDbChildren().size());
        dbClip2 = clipService.getClipLibraryCrud().readDbChild(dbClip2.getId());
        Assert.assertEquals("aaa3", dbClip2.getName());
        Assert.assertEquals(dbImageSpriteMap1.getId(), dbClip2.getDbImageSpriteMap().getId());
        Assert.assertEquals(dbClip2.getDbSound().getId(), dbClip2.getDbSound().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbClip2 = clipService.getClipLibraryCrud().readDbChild(dbClip2.getId());
        clipService.getClipLibraryCrud().deleteDbChild(dbClip2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getClipLibraryCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }


    @Test
    @DirtiesContext
    public void commonClipsCrud() throws Exception {
        // Setup sprite map and sound
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbClip dbClip1 = clipService.getClipLibraryCrud().createDbChild();
        DbClip dbClip2 = clipService.getClipLibraryCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getCommonClipCrud().readDbChildren().size());
        DbCommonClip dbCommonClip1 = clipService.getCommonClipCrud().createDbChild();
        dbCommonClip1.setDbClip(dbClip1);
        clipService.getCommonClipCrud().updateDbChild(dbCommonClip1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getCommonClipCrud().readDbChildren().size());
        dbCommonClip1 = clipService.getCommonClipCrud().readDbChild(dbCommonClip1.getId());
        Assert.assertEquals(dbClip1.getId(), dbCommonClip1.getDbClip().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbCommonClip dbCommonClip2 = clipService.getCommonClipCrud().createDbChild();
        dbCommonClip2.setDbClip(dbClip2);
        clipService.getCommonClipCrud().updateDbChild(dbCommonClip2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, clipService.getCommonClipCrud().readDbChildren().size());
        dbCommonClip1 = clipService.getCommonClipCrud().readDbChild(dbCommonClip1.getId());
        Assert.assertEquals(dbClip1.getId(), dbCommonClip1.getDbClip().getId());
        dbCommonClip2 = clipService.getCommonClipCrud().readDbChild(dbCommonClip2.getId());
        Assert.assertEquals(dbClip2.getId(), dbCommonClip2.getDbClip().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbCommonClip1 = clipService.getCommonClipCrud().readDbChild(dbCommonClip1.getId());
        clipService.getCommonClipCrud().deleteDbChild(dbCommonClip1);
        dbCommonClip2 = clipService.getCommonClipCrud().readDbChild(dbCommonClip2.getId());
        dbCommonClip2.setDbClip(dbClip1);
        clipService.getCommonClipCrud().updateDbChild(dbCommonClip2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getCommonClipCrud().readDbChildren().size());
        dbCommonClip2 = clipService.getCommonClipCrud().readDbChild(dbCommonClip2.getId());
        Assert.assertEquals(dbClip1.getId(), dbCommonClip2.getDbClip().getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbCommonClip2 = clipService.getCommonClipCrud().readDbChild(dbCommonClip2.getId());
        clipService.getCommonClipCrud().deleteDbChild(dbCommonClip2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getCommonClipCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void infoGenerating() throws Exception {
        configureSimplePlanet();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        Assert.assertEquals(0, realGameInfo.getCommonClipInfo().getCommonClips().size());
        Assert.assertEquals(0, realGameInfo.getClipLibrary().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        DbSound dbSound1 = soundService.getSoundLibraryCrud().createDbChild();
        DbClip dbClip1 = clipService.getClipLibraryCrud().createDbChild();
        dbClip1.setDbImageSpriteMap(dbImageSpriteMap1);
        dbClip1.setDbSound(dbSound1);
        clipService.getClipLibraryCrud().updateDbChild(dbClip1);
        DbCommonClip dbCommonClip1 = clipService.getCommonClipCrud().createDbChild();
        dbCommonClip1.setDbClip(dbClip1);
        dbCommonClip1.setType(CommonClipInfo.Type.EXPLOSION);
        clipService.getCommonClipCrud().updateDbChild(dbCommonClip1);
        DbImageSpriteMap dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().createDbChild();
        DbClip dbClip2 = clipService.getClipLibraryCrud().createDbChild();
        dbClip2.setDbImageSpriteMap(dbImageSpriteMap2);
        clipService.getClipLibraryCrud().updateDbChild(dbClip2);
        DbCommonClip dbCommonClip2 = clipService.getCommonClipCrud().createDbChild();
        dbCommonClip2.setDbClip(dbClip2);
        dbCommonClip2.setType(CommonClipInfo.Type.EXPLOSION);
        clipService.getCommonClipCrud().updateDbChild(dbCommonClip2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        realGameInfo = getMovableService().getRealGameInfo(START_UID_1);
        Assert.assertEquals(2, realGameInfo.getClipLibrary().size());
        ClipInfo clipInfo1 = (ClipInfo) realGameInfo.getClipLibrary().toArray()[0];
        Assert.assertEquals((int)dbClip1.getId(), clipInfo1.getClipId());
        Assert.assertEquals((int)dbImageSpriteMap1.getId(), clipInfo1.getSpriteMapId());
        Assert.assertEquals((int)dbSound1.getId(), (int)clipInfo1.getSoundId());
        ClipInfo clipInfo2 = (ClipInfo) realGameInfo.getClipLibrary().toArray()[1];
        Assert.assertEquals((int)dbClip2.getId(), clipInfo2.getClipId());
        Assert.assertEquals((int)dbImageSpriteMap2.getId(), clipInfo2.getSpriteMapId());
        Assert.assertNull(clipInfo2.getSoundId());
        Assert.assertEquals(1, realGameInfo.getCommonClipInfo().getCommonClips().size());
        List<Integer> commonExplosionClips =  realGameInfo.getCommonClipInfo().getCommonClips().get(CommonClipInfo.Type.EXPLOSION);
        Assert.assertEquals(2, commonExplosionClips.size());
        Assert.assertEquals(dbClip1.getId(), commonExplosionClips.get(0));
        Assert.assertEquals(dbClip2.getId(), commonExplosionClips.get(1));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
