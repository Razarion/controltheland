package com.btxtech.game.jsre.common.gameengine.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType.*;

/**
 * User: beat
 * Date: 07.10.2011
 * Time: 20:07:23
 */
public class TestAbstractTerrainServiceImpl {

    @Test
    public void createSurfaceTypeField1() {
        AbstractTerrainServiceImpl abstractTerrainService = new AbstractTerrainServiceImpl() {
            @Override
            protected CommonTerrainImageService getCommonTerrainImageService() {
                return null;
            }
        };
        abstractTerrainService.setTerrainSettings(new TerrainSettings(100, 200));
        abstractTerrainService.createTerrainTileField(Collections.<TerrainImagePosition>emptyList(), Collections.<SurfaceRect>emptyList());
        TerrainTile[][] terrainTile = abstractTerrainService.getTerrainTileField();
        Assert.assertEquals(100, terrainTile.length);
        for (int x = 0; x < 100; x++) {
            Assert.assertEquals(200, terrainTile[x].length);
            for (int y = 0; y < 200; y++) {
                Assert.assertNull(terrainTile[x][y]);
            }
        }
    }

    @Test
    public void createSurfaceTypeField2() {
        final CommonTerrainImageServiceImpl commonTerrainImageService = new CommonTerrainImageServiceImpl() {
        };
        AbstractTerrainServiceImpl abstractTerrainService = new AbstractTerrainServiceImpl() {
            @Override
            protected CommonTerrainImageService getCommonTerrainImageService() {
                return commonTerrainImageService;
            }
        };
        abstractTerrainService.setTerrainSettings(new TerrainSettings(20, 20));

        SurfaceType[][] tileSurfaceTypes = new SurfaceType[2][2];
        tileSurfaceTypes[0][0] = SurfaceType.LAND;
        tileSurfaceTypes[0][1] = SurfaceType.WATER;
        tileSurfaceTypes[1][0] = SurfaceType.COAST;
        tileSurfaceTypes[1][1] = SurfaceType.LAND;
        commonTerrainImageService.putTerrainImage(new TerrainImage(0, null, 2, 2, tileSurfaceTypes));

        commonTerrainImageService.putSurfaceImage(new SurfaceImage(SurfaceType.LAND, 0, null, null, ""));
        commonTerrainImageService.putSurfaceImage(new SurfaceImage(SurfaceType.WATER, 1, null, null, ""));

        List<TerrainImagePosition> terrainImagePositions = new ArrayList<>();
        terrainImagePositions.add(new TerrainImagePosition(new Index(0, 0), 0, TerrainImagePosition.ZIndex.LAYER_1));
        terrainImagePositions.add(new TerrainImagePosition(new Index(1, 1), 0, TerrainImagePosition.ZIndex.LAYER_2));
        terrainImagePositions.add(new TerrainImagePosition(new Index(5, 0), 0, TerrainImagePosition.ZIndex.LAYER_1));
        terrainImagePositions.add(new TerrainImagePosition(new Index(19, 10), 0, TerrainImagePosition.ZIndex.LAYER_1)); // Overbooked

        List<SurfaceRect> surfaceRects = new ArrayList<>();
        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 10, 10), 0));
        surfaceRects.add(new SurfaceRect(new Rectangle(10, 12, 10, 10), 0));
        surfaceRects.add(new SurfaceRect(new Rectangle(15, 15, 7, 7), 1)); // Overbooked

        abstractTerrainService.createTerrainTileField(terrainImagePositions, surfaceRects);
        TerrainTile[][] terrainTile = abstractTerrainService.getTerrainTileField();

        assertLine(terrainTile, 0, LAND, COAST, LAND, LAND, LAND, LAND, COAST, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 1, WATER, LAND, COAST, LAND, LAND, WATER, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 2, LAND, WATER, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 3, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 4, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 5, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 6, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 7, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 8, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 9, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, null, null, null, null, null, null, null, null, null, null);
        assertLine(terrainTile, 10, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, LAND);
        assertLine(terrainTile, 11, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, WATER);
        assertLine(terrainTile, 12, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        assertLine(terrainTile, 13, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        assertLine(terrainTile, 14, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND, LAND);
        assertLine(terrainTile, 15, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, WATER, WATER, WATER, WATER, WATER);
        assertLine(terrainTile, 16, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, WATER, WATER, WATER, WATER, WATER);
        assertLine(terrainTile, 17, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, WATER, WATER, WATER, WATER, WATER);
        assertLine(terrainTile, 18, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, WATER, WATER, WATER, WATER, WATER);
        assertLine(terrainTile, 19, null, null, null, null, null, null, null, null, null, null, LAND, LAND, LAND, LAND, LAND, WATER, WATER, WATER, WATER, WATER);
    }

    private void assertLine(TerrainTile[][] terrainTile, int y, SurfaceType... surfaceTypes) {
        Assert.assertEquals(20, terrainTile.length);
        for (int x = 0; x < surfaceTypes.length; x++) {
            Assert.assertEquals(20, terrainTile[x].length);
            SurfaceType surfaceTypesExpected = surfaceTypes[x];
            if (surfaceTypesExpected == null) {
                Assert.assertNull("Null expected at " + x + ":" + y, terrainTile[x][y]);
            } else {
                Assert.assertNotNull("Not null expected at " + x + ":" + y, terrainTile[x][y]);
                Assert.assertEquals("Wrong surface type at " + x + ":" + y, surfaceTypesExpected, terrainTile[x][y].getSurfaceType());
            }
        }
    }

}
