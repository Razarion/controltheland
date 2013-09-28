package com.btxtech.game.services.terrain;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbImageSpriteMap;
import org.apache.wicket.util.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestTerrainImageService extends AbstractServiceTest {
    @Autowired
    private ClipService clipService;
    @Autowired
    private TerrainImageService terrainImageService;

    @Test
    @DirtiesContext
    public void testCaching() throws Exception {
        // Setup clip
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbImageSpriteMap dbImageSpriteMap1 = clipService.getImageSpriteMapCrud().createDbChild();
        dbImageSpriteMap1.setFrameCount(1);
        dbImageSpriteMap1.setFrameWidth(2);
        dbImageSpriteMap1.setFrameHeight(3);
        dbImageSpriteMap1.setFrameTime(4);
        clipService.getImageSpriteMapCrud().updateDbChild(dbImageSpriteMap1);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Setup clip
        DbSurfaceImage dbClipSurface = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbClipSurface.setImageSpriteMap(clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId()));
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbClipSurface);
        // Setup scattered
        DbSurfaceImage dbScatterSurface = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbScatterSurface.setUncommon(0.3);
        dbScatterSurface.setImageData(new byte[]{1, 2, 3, 4, 5});
        dbScatterSurface.setContentType("gegel");
        DbScatterSurfaceImage dbScatterSurfaceImage = dbScatterSurface.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        dbScatterSurfaceImage = dbScatterSurface.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbScatterSurface);
        // Setup normal image
        DbSurfaceImage dbNormalImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbNormalImage.setImageData(new byte[]{2, 2, 1, 4});
        dbNormalImage.setContentType("gugug");
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbNormalImage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        terrainImageService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Clip
        DbSurfaceImage cachedDbSurfaceImage = terrainImageService.getDbSurfaceImage(dbClipSurface.getId());
        Assert.assertNull(cachedDbSurfaceImage.getCachedContentType());
        Assert.assertNull(cachedDbSurfaceImage.getCachedImageData());
        // Scatter
        cachedDbSurfaceImage = terrainImageService.getDbSurfaceImage(dbScatterSurface.getId());
        Assert.assertEquals("image/jpeg", cachedDbSurfaceImage.getCachedContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(cachedDbSurfaceImage.getCachedImageData()));
        Assert.assertEquals(200, image.getWidth());
        Assert.assertEquals(100, image.getHeight());
        // Normal image
        cachedDbSurfaceImage = terrainImageService.getDbSurfaceImage(dbNormalImage.getId());
        Assert.assertEquals("gugug", cachedDbSurfaceImage.getCachedContentType());
        Assert.assertArrayEquals(new byte[]{2, 2, 1, 4}, cachedDbSurfaceImage.getCachedImageData());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}