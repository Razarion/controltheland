package com.btxtech.game.services.territory;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Territory;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestTerritoryService extends AbstractServiceTest {
    @Autowired
    private TerritoryService territoryService;

    @Test
    @DirtiesContext
    public void testSaveSameTerritoryTiles() throws Exception {
        configureComplexGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Collection<Rectangle> rectangles = new ArrayList<Rectangle>(territory.getTerritoryTileRegions());
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, rectangles);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        rectangles.removeAll(territory.getTerritoryTileRegions());
        Assert.assertEquals(0, rectangles.size());
        Assert.assertEquals(5, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveEmptyTerritoryTiles() throws Exception {
        configureComplexGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, Collections.<Rectangle>emptyList());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(0, territory.getTerritoryTileRegions().size());
        Assert.assertEquals(0, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveDifferentTerritoryTiles() throws Exception {
        configureComplexGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<Rectangle> rectangles = Arrays.asList(new Rectangle(0, 0, 5, 5), new Rectangle(5, 5, 5, 5), new Rectangle(0, 5, 5, 5), new Rectangle(5, 0, 5, 5));
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, rectangles);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(4, territory.getTerritoryTileRegions().size());
        Assert.assertEquals(4, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveEqualTerritoryTiles() throws Exception {
        configureComplexGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<Rectangle> rectangles = Arrays.asList(new Rectangle(0, 0, 5, 5), new Rectangle(5, 5, 5, 5), new Rectangle(0, 5, 5, 5), new Rectangle(5, 0, 5, 5));
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, rectangles);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(4, territory.getTerritoryTileRegions().size());
        Assert.assertEquals(4, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        rectangles = Arrays.asList(new Rectangle(0, 0, 5, 5), new Rectangle(5, 5, 5, 5));
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, rectangles);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(2, territory.getTerritoryTileRegions().size());
        rectangles = territory.getTerritoryTileRegions();
        Assert.assertTrue(rectangles.remove(new Rectangle(0, 0, 5, 5)));
        Assert.assertTrue(rectangles.remove(new Rectangle(5, 5, 5, 5)));
        Assert.assertEquals(0, rectangles.size());
        Assert.assertEquals(2, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveEqual2TerritoryTiles() throws Exception {
        configureComplexGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<Rectangle> rectangles = Arrays.asList(new Rectangle(0, 0, 5, 5), new Rectangle(0, 0, 5, 5));
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, rectangles);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(2, territory.getTerritoryTileRegions().size());
        Assert.assertEquals(2, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void testSaveMultiTerritoryTiles() throws Exception {
        configureComplexGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<Rectangle> rectangles = Arrays.asList(new Rectangle(0, 0, 5, 5), new Rectangle(5, 5, 5, 5));
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, rectangles);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Territory territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(2, territory.getTerritoryTileRegions().size());
        Assert.assertEquals(2, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, Collections.<Rectangle>emptyList());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(0, territory.getTerritoryTileRegions().size());
        Assert.assertEquals(0, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        rectangles = Arrays.asList(new Rectangle(5, 0, 5, 5), new Rectangle(0, 5, 5, 5));
        territoryService.saveTerritory(COMPLEX_TERRITORY_ID, rectangles);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        territory = territoryService.getTerritory(COMPLEX_TERRITORY_ID);
        Assert.assertEquals(2, territory.getTerritoryTileRegions().size());
        Assert.assertEquals(2, territoryService.getDbTerritoryCrudServiceHelper().readDbChild(COMPLEX_TERRITORY_ID).getDbTerritoryRegions().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

}