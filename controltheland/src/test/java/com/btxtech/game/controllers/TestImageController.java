package com.btxtech.game.controllers;

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 21.03.2012
 * Time: 14:11:49
 */
public class TestImageController extends AbstractServiceTest {
    @Autowired
    private ImageController imageController;
    @Autowired
    private PlanetSystemService planetSystemService;

    @Test
    @DirtiesContext
    public void test1() throws Exception {
        configureSimplePlanetNoResources();
        // Setup
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbPlanet dbPlanet = planetSystemService.getDbPlanetCrud().readDbChild(TEST_PLANET_1_ID);
        dbPlanet.setStarMapImageContentType("qwert");
        dbPlanet.setStarMapImageData(new byte[]{2, 3, 4, 5, 6});
        planetSystemService.getDbPlanetCrud().updateDbChild(dbPlanet);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Test
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // Test wrong argument
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        imageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertEquals(400, mockHttpServletResponse.getStatus());
        // Test correct argument
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("type", "star");
        mockHttpServletRequest.setParameter("id", Integer.toString(TEST_PLANET_1_ID));
        mockHttpServletResponse = new MockHttpServletResponse();
        imageController.handleRequest(mockHttpServletRequest, mockHttpServletResponse);
        Assert.assertNull(mockHttpServletResponse.getErrorMessage());
        Assert.assertEquals(200, mockHttpServletResponse.getStatus());
        Assert.assertEquals("qwert", mockHttpServletResponse.getContentType());
        Assert.assertArrayEquals(new byte[]{2, 3, 4, 5, 6}, mockHttpServletResponse.getContentAsByteArray());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testUrlGenerating() throws Exception {
        Assert.assertEquals("/spring/image?type=star&id=11", ImageHandler.getStarMapPlanetImageUrl(11));
    }
}
