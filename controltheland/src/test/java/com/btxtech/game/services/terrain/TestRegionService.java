package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.RegionBuilder;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;

/**
 * User: beat
 * Date: 12.09.12
 * Time: 00:21
 */
public class TestRegionService extends AbstractServiceTest {
    @Autowired
    private RegionService regionService;

    @Test
    @DirtiesContext
    public void regionCrud() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(regionService.getRegionCrud().readDbChildren().isEmpty());
        DbRegion dbRegion = regionService.getRegionCrud().createDbChild();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, regionService.getRegionCrud().readDbChildren().size());
        Region region = regionService.loadRegionFromDb(dbRegion.getId());
        Assert.assertFalse(region.isInsideTile(new Index(1, 1)));
        Assert.assertFalse(region.isInsideAbsolute(new Index(100, 100)));
        RegionBuilder regionBuilder = new RegionBuilder(region);
        regionBuilder.insertTile(Collections.singletonList(new Index(1, 1)));
        regionService.saveRegionToDb(regionBuilder.toRegion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, regionService.getRegionCrud().readDbChildren().size());
        region = regionService.loadRegionFromDb(dbRegion.getId());
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        Assert.assertTrue(region.isInsideAbsolute(new Index(100, 100)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, regionService.getRegionCrud().readDbChildren().size());
        region = regionService.loadRegionFromDb(dbRegion.getId());
        regionBuilder = new RegionBuilder(region);
        regionBuilder.insertTile(Collections.singletonList(new Index(2, 2)));
        regionService.saveRegionToDb(regionBuilder.toRegion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, regionService.getRegionCrud().readDbChildren().size());
        region = regionService.loadRegionFromDb(dbRegion.getId());
        Assert.assertTrue(region.isInsideTile(new Index(2, 2)));
        Assert.assertTrue(region.isInsideAbsolute(new Index(200, 200)));
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        Assert.assertTrue(region.isInsideAbsolute(new Index(100, 100)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
                // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, regionService.getRegionCrud().readDbChildren().size());
        region = regionService.loadRegionFromDb(dbRegion.getId());
        regionBuilder = new RegionBuilder(region);
        regionBuilder.clear();
        regionBuilder.insertTile(Collections.singletonList(new Index(3, 3)));
        regionService.saveRegionToDb(regionBuilder.toRegion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, regionService.getRegionCrud().readDbChildren().size());
        region = regionService.loadRegionFromDb(dbRegion.getId());
        Assert.assertTrue(region.isInsideTile(new Index(3, 3)));
        Assert.assertTrue(region.isInsideAbsolute(new Index(300, 300)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 2)));
        Assert.assertFalse(region.isInsideAbsolute(new Index(200, 200)));
        Assert.assertFalse(region.isInsideTile(new Index(1, 1)));
        Assert.assertFalse(region.isInsideAbsolute(new Index(100, 100)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void cache() {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertTrue(regionService.getRegionCrud().readDbChildren().isEmpty());
        DbRegion dbRegion = regionService.getRegionCrud().createDbChild();
        RegionBuilder regionBuilder = new RegionBuilder(dbRegion.createRegion());
        regionBuilder.insertTile(Collections.singletonList(new Index(1, 1)));
        regionService.saveRegionToDb(regionBuilder.toRegion());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbRegion = regionService.getRegionCrud().readDbChild(dbRegion.getId());
        Region region = regionService.getRegionFromCache(dbRegion);
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbRegion = regionService.getRegionCrud().readDbChild(dbRegion.getId());
        region = regionService.getRegionFromCache(dbRegion);
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
