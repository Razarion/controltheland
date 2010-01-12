package com.btxtech.game.services.terrain;

import java.io.RandomAccessFile;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"})
public class TestTerrainService {
    @Autowired
    private TerrainService terrainService;

    //@Test
    public void testDbView() throws Exception {
        terrainService.clearTerrain();
        terrainService.clearTiles();
        Tile tile = terrainService.createTile(loadImage("tank_0001.png"), ItemType.LAND_ITEM);
        Assert.assertNotNull(tile.getId());
        terrainService.createNewTerrain(50, 50, tile);

        terrainService.createTile(loadImage("tank_0010.png"), ItemType.LAND_ITEM);
        terrainService.createTile(loadImage("tank_0015.png"), ItemType.LAND_ITEM);
        terrainService.createTile(loadImage("tank_0020.png"), ItemType.LAND_ITEM);
        terrainService.createTile(loadImage("tank_0025.png"), ItemType.LAND_ITEM);
    }

    @Test
    public void testDbView2() throws Exception {
        terrainService.createNewTerrain(50, 50, terrainService.getTiles().get(1));
    }

    private byte[] loadImage(String img) throws Exception {
        RandomAccessFile raf = new RandomAccessFile("/home/beat/dev/projects/game/map/war/images/vehicles/" + img, "r");
        byte array[] = new byte[(int) raf.length()];
        raf.read(array);
        System.out.println("image loaded: " + raf.length());
        raf.close();
        return array;
    }

}