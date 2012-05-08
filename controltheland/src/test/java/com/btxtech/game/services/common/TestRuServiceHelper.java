package com.btxtech.game.services.common;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 16.12.2011
 * Time: 13:31:31
 */
public class TestRuServiceHelper extends AbstractServiceTest {
    @Autowired
    private TerrainService terrainService;
    @Autowired
    RuServiceHelper<DbTerrainImageGroup> ruServiceHelper;
    @Autowired
    RuServiceHelper<DbTerrainImage> imageRuServiceHelper;

    @Test
    @DirtiesContext
    public void deleteChildRollback() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = setupMinimalTerrain();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        DbTerrainImage dbTerrainImage = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage.setTiles(1, 1);
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup);
        terrainService.activateTerrain();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();


        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), dbTerrainImage.getId(), TerrainImagePosition.ZIndex.LAYER_1));
        terrainService.saveTerrain(terrainImagePositions, new ArrayList<SurfaceRect>(), dbTerrainSetting.getId());
        terrainService.activateTerrain();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTerrainImageGroup = ruServiceHelper.readDbChild(dbTerrainImageGroup.getId(), DbTerrainImageGroup.class);
        try {
            ruServiceHelper.removeChildAndUpdate(dbTerrainImageGroup,
                    dbTerrainImageGroup.getTerrainImageCrud(),
                    dbTerrainImageGroup.getTerrainImageCrud().readDbChildren(null).iterator().next());
            Assert.fail("exception expected since tile is still used in map");
        } catch (Exception ignore) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        // should still work
        terrainService.activateTerrain();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }

}
