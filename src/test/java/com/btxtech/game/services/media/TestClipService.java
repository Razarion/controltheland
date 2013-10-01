package com.btxtech.game.services.media;

import com.btxtech.game.controllers.ImageSpriteMapController;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.info.ClipInfo;
import com.btxtech.game.jsre.client.common.info.PreloadedImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.ImageSpriteMapInfo;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.item.TestItemImageHandling;
import org.apache.wicket.util.io.IOUtils;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ImageSpriteMapController imageSpriteMapController;

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
    public void saveSpriteMap() throws Exception {
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        ImageSpriteMapInfo imageSpriteMapInfo = dbImageSpriteMap1.createImageSpriteMapInfo();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId()).getImageSpriteMapFrameCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        imageSpriteMapInfo.setFrameCount(3);
        imageSpriteMapInfo.setFrameWidth(11);
        imageSpriteMapInfo.setFrameHeight(10);
        imageSpriteMapInfo.setFrameTime(15);
        clipService.saveImageSpriteMap(imageSpriteMapInfo, new String[]{TestItemImageHandling.INLINE_IMAGE_1, TestItemImageHandling.INLINE_IMAGE_2, TestItemImageHandling.INLINE_IMAGE_3});
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap readImageSpriteMap1 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId());
        Assert.assertEquals(3, readImageSpriteMap1.getFrameCount());
        Assert.assertEquals(11, readImageSpriteMap1.getFrameWidth());
        Assert.assertEquals(10, readImageSpriteMap1.getFrameHeight());
        Assert.assertEquals(15, readImageSpriteMap1.getFrameTime());
        List<DbImageSpriteMapFrame> spriteMapFrames = readImageSpriteMap1.getImageSpriteMapFrameCrud().readDbChildren();
        Assert.assertEquals(3, spriteMapFrames.size());
        Assert.assertArrayEquals(TestItemImageHandling.IMAGE_DATA_1, spriteMapFrames.get(0).getData());
        Assert.assertArrayEquals(TestItemImageHandling.IMAGE_DATA_2, spriteMapFrames.get(1).getData());
        Assert.assertArrayEquals(TestItemImageHandling.IMAGE_DATA_3, spriteMapFrames.get(2).getData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        imageSpriteMapInfo.setFrameCount(2);
        imageSpriteMapInfo.setFrameWidth(12);
        imageSpriteMapInfo.setFrameHeight(13);
        imageSpriteMapInfo.setFrameTime(14);
        clipService.saveImageSpriteMap(imageSpriteMapInfo, new String[]{TestItemImageHandling.INLINE_IMAGE_3, TestItemImageHandling.INLINE_IMAGE_2});
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        readImageSpriteMap1 = clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId());
        Assert.assertEquals(2, readImageSpriteMap1.getFrameCount());
        Assert.assertEquals(12, readImageSpriteMap1.getFrameWidth());
        Assert.assertEquals(13, readImageSpriteMap1.getFrameHeight());
        Assert.assertEquals(14, readImageSpriteMap1.getFrameTime());
        spriteMapFrames = readImageSpriteMap1.getImageSpriteMapFrameCrud().readDbChildren();
        Assert.assertEquals(2, spriteMapFrames.size());
        Assert.assertArrayEquals(TestItemImageHandling.IMAGE_DATA_3, spriteMapFrames.get(0).getData());
        Assert.assertArrayEquals(TestItemImageHandling.IMAGE_DATA_2, spriteMapFrames.get(1).getData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        clipService.getImageSpriteMapCrud().deleteDbChild(clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId()));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbImageSpriteMap.class).size());
        Assert.assertEquals(0, HibernateUtil.loadAll(sessionFactory, DbImageSpriteMapFrame.class).size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void spriteMapGeneratingAndController() throws Exception {
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        dbImageSpriteMap1.setFrameCount(2);
        dbImageSpriteMap1.setFrameWidth(64);
        dbImageSpriteMap1.setFrameHeight(64);
        dbImageSpriteMap1.setFrameTime(10);
        DbImageSpriteMapFrame dbImageSpriteMapFrame = dbImageSpriteMap1.getImageSpriteMapFrameCrud().createDbChild();
        dbImageSpriteMapFrame.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0001.png").openStream()));
        dbImageSpriteMapFrame = dbImageSpriteMap1.getImageSpriteMapFrameCrud().createDbChild();
        dbImageSpriteMapFrame.setData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0002.png").openStream()));
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap1);
        clipService.activateImageSpriteMapCache();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        mockHttpServletRequest.setParameter(Constants.IMAGE_SPRITE_MAP_ID, Integer.toString(dbImageSpriteMap1.getId()));
        imageSpriteMapController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals("image/png", mockHttpServletResponse.getContentType());
        BufferedImage spriteImage = ImageIO.read(new ByteArrayInputStream(mockHttpServletResponse.getContentAsByteArray()));
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0001.png", spriteImage, 0);
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0002.png", spriteImage, 64);
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
    public void preloadedImageSpriteMapCrud() throws Exception {
        // Setup sprite map and sound
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        DbImageSpriteMap dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getPreloadedSpriteMapCrud().readDbChildren().size());
        PreloadedImageSpriteMap preloadedImageSpriteMap1 = clipService.getPreloadedSpriteMapCrud().createDbChild();
        preloadedImageSpriteMap1.setDbImageSpriteMap(dbImageSpriteMap1);
        preloadedImageSpriteMap1.setType(PreloadedImageSpriteMapInfo.Type.DETONATION);
        clipService.getPreloadedSpriteMapCrud().updateDbChild(preloadedImageSpriteMap1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getPreloadedSpriteMapCrud().readDbChildren().size());
        preloadedImageSpriteMap1 = clipService.getPreloadedSpriteMapCrud().readDbChild(preloadedImageSpriteMap1.getId());
        Assert.assertEquals(dbImageSpriteMap1.getId(), preloadedImageSpriteMap1.getDbImageSpriteMap().getId());
        Assert.assertEquals(PreloadedImageSpriteMapInfo.Type.DETONATION, preloadedImageSpriteMap1.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        PreloadedImageSpriteMap preloadedImageSpriteMap2 = clipService.getPreloadedSpriteMapCrud().createDbChild();
        preloadedImageSpriteMap2.setDbImageSpriteMap(dbImageSpriteMap2);
        preloadedImageSpriteMap2.setType(PreloadedImageSpriteMapInfo.Type.EXPLOSION);
        clipService.getPreloadedSpriteMapCrud().updateDbChild(preloadedImageSpriteMap2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(2, clipService.getPreloadedSpriteMapCrud().readDbChildren().size());
        preloadedImageSpriteMap1 = clipService.getPreloadedSpriteMapCrud().readDbChild(preloadedImageSpriteMap1.getId());
        Assert.assertEquals(dbImageSpriteMap1.getId(), preloadedImageSpriteMap1.getDbImageSpriteMap().getId());
        Assert.assertEquals(PreloadedImageSpriteMapInfo.Type.DETONATION, preloadedImageSpriteMap1.getType());
        preloadedImageSpriteMap2 = clipService.getPreloadedSpriteMapCrud().readDbChild(preloadedImageSpriteMap2.getId());
        Assert.assertEquals(dbImageSpriteMap2.getId(), preloadedImageSpriteMap2.getDbImageSpriteMap().getId());
        Assert.assertEquals(PreloadedImageSpriteMapInfo.Type.EXPLOSION, preloadedImageSpriteMap2.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        preloadedImageSpriteMap1 = clipService.getPreloadedSpriteMapCrud().readDbChild(preloadedImageSpriteMap1.getId());
        clipService.getPreloadedSpriteMapCrud().deleteDbChild(preloadedImageSpriteMap1);
        preloadedImageSpriteMap2 = clipService.getPreloadedSpriteMapCrud().readDbChild(preloadedImageSpriteMap2.getId());
        preloadedImageSpriteMap2.setDbImageSpriteMap(dbImageSpriteMap1);
        preloadedImageSpriteMap2.setType(PreloadedImageSpriteMapInfo.Type.MUZZLE_FLASH);
        clipService.getPreloadedSpriteMapCrud().updateDbChild(preloadedImageSpriteMap2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, clipService.getPreloadedSpriteMapCrud().readDbChildren().size());
        preloadedImageSpriteMap2 = clipService.getPreloadedSpriteMapCrud().readDbChild(preloadedImageSpriteMap2.getId());
        Assert.assertEquals(dbImageSpriteMap1.getId(), preloadedImageSpriteMap2.getDbImageSpriteMap().getId());
        Assert.assertEquals(PreloadedImageSpriteMapInfo.Type.MUZZLE_FLASH, preloadedImageSpriteMap2.getType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        preloadedImageSpriteMap2 = clipService.getPreloadedSpriteMapCrud().readDbChild(preloadedImageSpriteMap2.getId());
        clipService.getPreloadedSpriteMapCrud().deleteDbChild(preloadedImageSpriteMap2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(0, clipService.getPreloadedSpriteMapCrud().readDbChildren().size());
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
        RealGameInfo realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        Assert.assertEquals(0, realGameInfo.getPreloadedImageSpriteMapInfo().getPreloadedImageSpriteMapInfo().size());
        Assert.assertEquals(0, realGameInfo.getClipLibrary().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        dbImageSpriteMap1.setFrameCount(1);
        dbImageSpriteMap1.setFrameWidth(2);
        dbImageSpriteMap1.setFrameHeight(3);
        dbImageSpriteMap1.setFrameTime(4);
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap1);
        DbSound dbSound1 = soundService.getSoundLibraryCrud().createDbChild();
        DbClip dbClip1 = clipService.getClipLibraryCrud().createDbChild();
        dbClip1.setDbImageSpriteMap(dbImageSpriteMap1);
        dbClip1.setDbSound(dbSound1);
        clipService.getClipLibraryCrud().updateDbChild(dbClip1);
        PreloadedImageSpriteMap preloadedImageSpriteMap1 = clipService.getPreloadedSpriteMapCrud().createDbChild();
        preloadedImageSpriteMap1.setDbImageSpriteMap(dbImageSpriteMap1);
        preloadedImageSpriteMap1.setType(PreloadedImageSpriteMapInfo.Type.MUZZLE_FLASH);
        clipService.getPreloadedSpriteMapCrud().updateDbChild(preloadedImageSpriteMap1);
        DbImageSpriteMap dbImageSpriteMap2 = clipService.getImageSpriteMapCrud().createDbChild();
        dbImageSpriteMap2.setFrameCount(11);
        dbImageSpriteMap2.setFrameWidth(12);
        dbImageSpriteMap2.setFrameHeight(13);
        dbImageSpriteMap2.setFrameTime(14);
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap2);
        DbClip dbClip2 = clipService.getClipLibraryCrud().createDbChild();
        dbClip2.setDbImageSpriteMap(dbImageSpriteMap2);
        clipService.getClipLibraryCrud().updateDbChild(dbClip2);
        PreloadedImageSpriteMap preloadedImageSpriteMap2 = clipService.getPreloadedSpriteMapCrud().createDbChild();
        preloadedImageSpriteMap2.setDbImageSpriteMap(dbImageSpriteMap2);
        preloadedImageSpriteMap2.setType(PreloadedImageSpriteMapInfo.Type.EXPLOSION);
        clipService.getPreloadedSpriteMapCrud().updateDbChild(preloadedImageSpriteMap2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        realGameInfo = getMovableService().getRealGameInfo(START_UID_1, null);
        // Image sprite map
        Assert.assertEquals(2, realGameInfo.getImageSpriteMapLibrary().size());
        List<ImageSpriteMapInfo> imageSpriteMap = new ArrayList<>(realGameInfo.getImageSpriteMapLibrary());
        Assert.assertEquals(1, imageSpriteMap.get(0).getFrameCount());
        Assert.assertEquals(2, imageSpriteMap.get(0).getFrameWidth());
        Assert.assertEquals(3, imageSpriteMap.get(0).getFrameHeight());
        Assert.assertEquals(4, imageSpriteMap.get(0).getFrameTime());
        Assert.assertEquals(11, imageSpriteMap.get(1).getFrameCount());
        Assert.assertEquals(12, imageSpriteMap.get(1).getFrameWidth());
        Assert.assertEquals(13, imageSpriteMap.get(1).getFrameHeight());
        Assert.assertEquals(14, imageSpriteMap.get(1).getFrameTime());
        // Clip
        Assert.assertEquals(2, realGameInfo.getClipLibrary().size());
        ClipInfo clipInfo1 = (ClipInfo) realGameInfo.getClipLibrary().toArray()[0];
        Assert.assertEquals((int) dbImageSpriteMap1.getId(), clipInfo1.getClipId());
        Assert.assertEquals((int) dbImageSpriteMap1.getId(), clipInfo1.getSpriteMapId());
        Assert.assertEquals((int) dbSound1.getId(), (int) clipInfo1.getSoundId());
        ClipInfo clipInfo2 = (ClipInfo) realGameInfo.getClipLibrary().toArray()[1];
        Assert.assertEquals((int) dbImageSpriteMap2.getId(), clipInfo2.getClipId());
        Assert.assertEquals((int) dbImageSpriteMap2.getId(), clipInfo2.getSpriteMapId());
        Assert.assertNull(clipInfo2.getSoundId());
        // Preloaded sprite maps
        Assert.assertEquals(2, realGameInfo.getPreloadedImageSpriteMapInfo().getPreloadedImageSpriteMapInfo().size());
        Assert.assertEquals(dbImageSpriteMap1.getId(), realGameInfo.getPreloadedImageSpriteMapInfo().getPreloadedImageSpriteMapInfo().get(PreloadedImageSpriteMapInfo.Type.MUZZLE_FLASH));
        Assert.assertEquals(dbImageSpriteMap2.getId(), realGameInfo.getPreloadedImageSpriteMapInfo().getPreloadedImageSpriteMapInfo().get(PreloadedImageSpriteMapInfo.Type.EXPLOSION));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
