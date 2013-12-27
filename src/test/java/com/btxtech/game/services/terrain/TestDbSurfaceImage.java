package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.common.gameengine.services.terrain.ScatterSurfaceImageInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.TestItemImageHandling;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import junit.framework.Assert;
import org.apache.wicket.util.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * User: beat
 * Date: 25.09.13
 * Time: 11:01
 */
public class TestDbSurfaceImage extends AbstractServiceTest {
    @Autowired
    private TerrainImageService terrainImageService;

    @Test
    @DirtiesContext
    public void testScatterSurfaceImageActivating() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSurfaceImage dbSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage.setUncommon(0.3);
        dbSurfaceImage.setRare(0.1);
        // Common image 1
        DbScatterSurfaceImage dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        // Common image 2
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        // Common image 3
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        // Uncommon image 1
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.UNCOMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        // Uncommon image 2
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.UNCOMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        // Rare image 1
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.RARE);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/test100x100.jpg").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        terrainImageService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().readDbChild(dbSurfaceImage.getId());
        SurfaceImage surfaceImage = dbSurfaceImage.createSurfaceImage();
        Assert.assertTrue(surfaceImage.hasScatterSurfaceImageInfo());
        ScatterSurfaceImageInfo scatterSurfaceImageInfo = surfaceImage.getScatterSurfaceImageInfo();
        Assert.assertNotNull(scatterSurfaceImageInfo);
        Assert.assertEquals(0.3, (double) getPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, "uncommon"), 0.0001);
        Assert.assertEquals(0.1, (double)getPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, "rare"), 0.0001);
        Assert.assertEquals(3, (int) getPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, "commonImageCount"));
        Assert.assertEquals(2, (int) getPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, "uncommonImageCount"));
        Assert.assertEquals(1, (int)getPrivateField(ScatterSurfaceImageInfo.class, scatterSurfaceImageInfo, "rareImageCount"));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void scatterImageSpriteMap() throws IOException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbSurfaceImage dbSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbSurfaceImage.setUncommon(0.3);
        dbSurfaceImage.setRare(0.1);
        DbScatterSurfaceImage dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.UNCOMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0001.png").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.RARE);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0002.png").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0003.png").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.UNCOMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0004.png").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.UNCOMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0005.png").openStream()));
        dbScatterSurfaceImage.setContentType("jpg");
        dbScatterSurfaceImage = dbSurfaceImage.getScatterSurfaceImageCrudHelper().createDbChild();
        dbScatterSurfaceImage.setFrequency(DbScatterSurfaceImage.Frequency.COMMON);
        dbScatterSurfaceImage.setImageData(IOUtils.toByteArray(getClass().getResource("/images/hoover_bagger_0006.png").openStream()));
        dbScatterSurfaceImage.setContentType("png");
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbSurfaceImage);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        terrainImageService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbSurfaceImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().readDbChild(dbSurfaceImage.getId());
        dbSurfaceImage.cacheImage();
        Assert.assertEquals("image/png", dbSurfaceImage.getCachedContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(dbSurfaceImage.getCachedImageData()));
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0003.png", image, 0);
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0006.png", image, 64);
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0001.png", image, 128);
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0004.png", image, 192);
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0005.png", image, 256);
        TestItemImageHandling.assertBufferedImage("/images/hoover_bagger_0002.png", image, 320);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }
}
