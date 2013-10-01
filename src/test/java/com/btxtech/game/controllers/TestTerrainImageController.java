package com.btxtech.game.controllers;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbImageSpriteMap;
import com.btxtech.game.services.terrain.DbScatterSurfaceImage;
import com.btxtech.game.services.terrain.DbSurfaceImage;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.TerrainImageService;
import org.apache.wicket.util.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

/**
 * User: beat
 * Date: 21.03.2012
 * Time: 14:11:49
 */
public class TestTerrainImageController extends AbstractServiceTest {
    @Autowired
    private ClipService clipService;
    @Autowired
    private TerrainImageService terrainImageService;
    @Autowired
    private TerrainImageController terrainImageController;

    @Test
    @DirtiesContext
    public void test() throws Exception {
        configureSimplePlanetNoResources();
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
        // Setup clip surface
        DbSurfaceImage dbClipSurface = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbClipSurface.setImageSpriteMap(clipService.getImageSpriteMapCrud().readDbChild(dbImageSpriteMap1.getId()));
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbClipSurface);
        // Setup scattered surface
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
        // Setup normal image surface
        DbSurfaceImage dbNormalImage = terrainImageService.getDbSurfaceImageCrudServiceHelper().createDbChild();
        dbNormalImage.setImageData(new byte[]{2, 2, 1, 4});
        dbNormalImage.setContentType("gugug");
        terrainImageService.getDbSurfaceImageCrudServiceHelper().updateDbChild(dbNormalImage);
        // Setup terrain image
        DbTerrainImageGroup dbTerrainImageGroup1 = terrainImageService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        DbTerrainImage dbTerrainImage11 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        dbTerrainImage11.setContentType("ccc1");
        dbTerrainImage11.setImageData(new byte[]{1, 2, 3, 4});
        terrainImageService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup1);
        DbTerrainImageGroup dbTerrainImageGroup2 = terrainImageService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        DbTerrainImage dbTerrainImage21 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        dbTerrainImage21.setContentType("ccc2");
        dbTerrainImage21.setImageData(new byte[]{3, 4, 5});
        DbTerrainImage dbTerrainImage22 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        dbTerrainImage22.setContentType("ccc3");
        dbTerrainImage22.setImageData(new byte[]{19, 3, 4, 5});
        terrainImageService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup2);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Activate
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        terrainImageService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test wrong argument
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify terrain image
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Invalid id
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("tp", "fg");
        mockHttpServletRequest.setParameter("id", "999999999999");
        mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());
        // Test images 1
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("tp", "fg");
        mockHttpServletRequest.setParameter("id", Integer.toString(dbTerrainImage11.getId()));
        mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("ccc1", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, mockHttpServletResponse.getContentAsByteArray());
        // Test images 2
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("tp", "fg");
        mockHttpServletRequest.setParameter("id", Integer.toString(dbTerrainImage21.getId()));
        mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("ccc2", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{3, 4, 5}, mockHttpServletResponse.getContentAsByteArray());
        // Test images 3
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("tp", "fg");
        mockHttpServletRequest.setParameter("id", Integer.toString(dbTerrainImage22.getId()));
        mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("ccc3", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{19, 3, 4, 5}, mockHttpServletResponse.getContentAsByteArray());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        // Verify Surface
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Test surface clip -> must return an error
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("tp", "sf");
        mockHttpServletRequest.setParameter("id", Integer.toString(dbClipSurface.getId()));
        mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());
        // Test surface scattered
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("tp", "sf");
        mockHttpServletRequest.setParameter("id", Integer.toString(dbScatterSurface.getId()));
        mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("image/jpeg", mockHttpServletResponse.getContentType());
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(mockHttpServletResponse.getContentAsByteArray()));
        Assert.assertEquals(200, image.getWidth());
        Assert.assertEquals(100, image.getHeight());
        // Test surface normal
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("tp", "sf");
        mockHttpServletRequest.setParameter("id", Integer.toString(dbNormalImage.getId()));
        mockHttpServletResponse = new MockHttpServletResponse();
        terrainImageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertNull(mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("gugug", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{2, 2, 1, 4}, mockHttpServletResponse.getContentAsByteArray());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUrlGenerating() throws Exception {
        Assert.assertEquals("/spring/terrain?tp=sf&id=9", ImageHandler.getSurfaceImagesUrl(9));
        Assert.assertEquals("/spring/terrain?tp=fg&id=4", ImageHandler.getTerrainImageUrl(4));
    }
}
