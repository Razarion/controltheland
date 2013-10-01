package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.RegionBuilder;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
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
        // Verify No session
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Assert.assertEquals(1, regionService.getRegionCrud().readDbChildren().size());
        region = regionService.loadRegionFromDb(dbRegion.getId());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        Assert.assertTrue(region.isInsideAbsolute(new Index(100, 100)));
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
    public void testSimple1() {
        Region region = new Region(1, Collections.<Rectangle>emptyList());
        RegionBuilder regionBuilder = new RegionBuilder(region);
        Collection<Index> indexes = new ArrayList<>();
        indexes.add(new Index(0, 0));
        indexes.add(new Index(0, 1));
        indexes.add(new Index(1, 0));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(7, 7));
        regionBuilder.insertTile(indexes);
        region = regionBuilder.toRegion();
        Assert.assertTrue(region.isInsideTile(new Index(0, 0)));
        Assert.assertTrue(region.isInsideTile(new Index(0, 1)));
        Assert.assertTrue(region.isInsideTile(new Index(1, 0)));
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        Assert.assertFalse(region.isInsideTile(new Index(0, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(1, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 0)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 1)));
        Assert.assertTrue(region.isInsideTile(new Index(7, 7)));
        Assert.assertFalse(region.isInsideTile(new Index(8, 8)));
    }

    @Test
    @DirtiesContext
    public void testSimple2() {
        Region region = new Region(1, Collections.<Rectangle>emptyList());
        RegionBuilder regionBuilder = new RegionBuilder(region);
        Collection<Index> indexes = new ArrayList<>();
        indexes.add(new Index(0, 0));
        indexes.add(new Index(0, 1));
        indexes.add(new Index(1, 0));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(4, 4));
        indexes.add(new Index(5, 5));
        indexes.add(new Index(8, 8));
        regionBuilder.insertTile(indexes);
        region = regionBuilder.toRegion();
        Assert.assertTrue(region.isInsideTile(new Index(0, 0)));
        Assert.assertTrue(region.isInsideTile(new Index(0, 1)));
        Assert.assertTrue(region.isInsideTile(new Index(1, 0)));
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        Assert.assertFalse(region.isInsideTile(new Index(0, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(1, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 0)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 1)));
        Assert.assertTrue(region.isInsideTile(new Index(4, 4)));
        Assert.assertTrue(region.isInsideTile(new Index(5, 5)));
        Assert.assertTrue(region.isInsideTile(new Index(8, 8)));
    }

    @Test
    @DirtiesContext
    public void multipleSameInsert() {
        Region region = new Region(1, Collections.<Rectangle>emptyList());
        RegionBuilder regionBuilder = new RegionBuilder(region);
        Collection<Index> indexes = new ArrayList<>();
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        indexes.add(new Index(1, 1));
        regionBuilder.insertTile(indexes);
        region = regionBuilder.toRegion();

        Assert.assertFalse(region.isInsideTile(new Index(0, 0)));
        Assert.assertFalse(region.isInsideTile(new Index(1, 0)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 0)));

        Assert.assertFalse(region.isInsideTile(new Index(0, 1)));
        Assert.assertTrue(region.isInsideTile(new Index(1, 1)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 1)));

        Assert.assertFalse(region.isInsideTile(new Index(0, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(1, 2)));
        Assert.assertFalse(region.isInsideTile(new Index(2, 2)));
    }

}
