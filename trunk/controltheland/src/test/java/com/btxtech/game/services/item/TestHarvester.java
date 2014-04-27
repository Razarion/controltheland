package com.btxtech.game.services.item;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.HarvesterType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.media.DbClip;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 27.05.2011
 * Time: 14:17:18
 */
public class TestHarvester extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void testCrud() throws Exception {
        configureSimplePlanet();
        DbClip dbClip1 = createEmptyDbClip();
        DbClip dbClip2 = createEmptyDbClip();
        // Setup item
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().createDbChild(DbBaseItemType.class);
        setupImages(dbBaseItemType, 24);
        dbBaseItemType.setTerrainType(TerrainType.LAND_COAST);
        dbBaseItemType.setBounding(new BoundingBox(80, ANGELS_24));
        dbBaseItemType.setHealth(10);
        dbBaseItemType.setBuildup(10);
        dbBaseItemType.setPrice(1);
        dbBaseItemType.setXpOnKilling(1);
        dbBaseItemType.setConsumingHouseSpace(1);
        // Harvester
        DbHarvesterType dbHarvesterType = new DbHarvesterType();
        dbHarvesterType.setRange(100);
        dbHarvesterType.setProgress(1);
        dbHarvesterType.setHarvestClip(readDbClipInSession(dbClip1.getId()));
        dbHarvesterType.setHarvestClipPositions(INDEX_24);
        dbBaseItemType.setDbHarvesterType(dbHarvesterType);

        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        int builderId = dbBaseItemType.getId();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        BaseItemType baseItemType = (BaseItemType) serverItemTypeService.getItemType(builderId);
        HarvesterType harvesterType = baseItemType.getHarvesterType();
        Assert.assertEquals(100, harvesterType.getRange());
        Assert.assertEquals(1, harvesterType.getProgress(), 0.0001);
        Assert.assertEquals((int) dbClip1.getId(), harvesterType.getHarvesterClip().getClipId());
        Assert.assertArrayEquals(INDEX_24, harvesterType.getHarvesterClip().getPositions());
        // Change
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(dbBaseItemType.getId());
        // DbBuilderType
        dbHarvesterType = dbBaseItemType.getDbHarvesterType();
        dbHarvesterType.setRange(105);
        dbHarvesterType.setProgress(15);
        dbHarvesterType.setHarvestClip(readDbClipInSession(dbClip2.getId()));
        dbHarvesterType.setHarvestClipPositions(INDEX_05);
        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        baseItemType = (BaseItemType) serverItemTypeService.getItemType(builderId);
        harvesterType = baseItemType.getHarvesterType();
        Assert.assertEquals(105, harvesterType.getRange());
        Assert.assertEquals(15, harvesterType.getProgress(), 0.0001);
        Assert.assertEquals((int) dbClip2.getId(), harvesterType.getHarvesterClip().getClipId());
        Assert.assertArrayEquals(INDEX_05, harvesterType.getHarvesterClip().getPositions());
        // Change no clip
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(dbBaseItemType.getId());
        // DbBuilderType
        dbHarvesterType = dbBaseItemType.getDbHarvesterType();
        dbHarvesterType.setHarvestClipPositions(new Index[0]);
        dbHarvesterType.setHarvestClip(null);
        serverItemTypeService.saveDbItemType(dbBaseItemType);
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        baseItemType = (BaseItemType) serverItemTypeService.getItemType(builderId);
        harvesterType = baseItemType.getHarvesterType();
        Assert.assertEquals(105, harvesterType.getRange());
        Assert.assertEquals(15, harvesterType.getProgress(), 0.0001);
        Assert.assertNull(harvesterType.getHarvesterClip());
    }
}
