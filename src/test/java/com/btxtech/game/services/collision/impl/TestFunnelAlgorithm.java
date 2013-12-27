package com.btxtech.game.services.collision.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.FunnelAlgorithm;
import com.btxtech.game.jsre.common.gameengine.services.terrain.CommonTerrainImageService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: beat
 * Date: 08.06.12
 * Time: 11:19
 */
public class TestFunnelAlgorithm {
    private TerrainSettings terrainSettings = new TerrainSettings(400, 400);
    private AbstractTerrainServiceImpl terrainService;

    @Before
    public void setup() {
        terrainService = new AbstractTerrainServiceImpl() {
            @Override
            protected CommonTerrainImageService getCommonTerrainImageService() {
                return null;
            }
        };
        terrainService.setTerrainSettings(terrainSettings);
    }

    @Test
    public void testNoTiles() {
        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(new Index(1000, 1000), new Index(1200, 1000));
        funnelAlgorithm.setTilePath(Collections.<Index>emptyList(), terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(2, path.size());
        Assert.assertEquals(new Index(1000, 1000), path.get(0));
        Assert.assertEquals(new Index(1200, 1000), path.get(1));
    }

    @Test
    public void test1Tile1() {
        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(new Index(1050, 950), new Index(1150, 1050));
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(10, 10));
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(3, path.size());
        Assert.assertEquals(new Index(1050, 950), path.get(0));
        Assert.assertEquals(new Index(1099, 1000), path.get(1));
        Assert.assertEquals(new Index(1150, 1050), path.get(2));
    }

    @Test
    public void test1Tile2() {
        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(new Index(950, 1050), new Index(1150, 1050));
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(10, 10));
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(2, path.size());
        Assert.assertEquals(new Index(950, 1050), path.get(0));
        Assert.assertEquals(new Index(1150, 1050), path.get(1));
    }

    @Test
    public void test1Tile3() {
        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(new Index(975, 1000), new Index(1075, 1125));
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(10, 10));
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(2, path.size());
        Assert.assertEquals(new Index(975, 1000), path.get(0));
        Assert.assertEquals(new Index(1075, 1125), path.get(1));
    }
    @Test
    public void test1Tile4() {
        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(new Index(1050, 1150), new Index(1150, 1050));
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(10, 10));
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(3, path.size());
        Assert.assertEquals(new Index(1050, 1150), path.get(0));
        Assert.assertEquals(new Index(1099, 1099), path.get(1));
        Assert.assertEquals(new Index(1150, 1050), path.get(2));
    }


    @Test
    public void testStraitVertical() {
        Index start = new Index(2565, 450);
        Index destination = new Index(2549, 1109);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(25, 5));
        tilePath.add(new Index(25, 6));
        tilePath.add(new Index(25, 7));
        tilePath.add(new Index(25, 8));
        tilePath.add(new Index(25, 9));
        tilePath.add(new Index(25, 10));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(2, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(destination, path.get(1));
    }

    @Test
    public void testStraitHorizontal() {
        Index start = new Index(2734, 1260);
        Index destination = new Index(3369, 1245);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(28, 12));
        tilePath.add(new Index(29, 12));
        tilePath.add(new Index(30, 12));
        tilePath.add(new Index(31, 12));
        tilePath.add(new Index(32, 12));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(2, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(destination, path.get(1));
    }


    @Test
    public void test3Tiles1() {
        Index start = new Index(4028, 841);
        Index destination = new Index(4255, 1036);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(40, 9));
        tilePath.add(new Index(40, 10));
        tilePath.add(new Index(41, 10));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(3, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(new Index(4099, 1000), path.get(1));
        Assert.assertEquals(destination, path.get(2));
    }

    @Test
    public void test3Tiles2() {
        Index start = new Index(4447, 833);
        Index destination = new Index(4250, 1023);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(44, 9));
        tilePath.add(new Index(44, 10));
        tilePath.add(new Index(43, 10));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(3, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(new Index(4400, 1000), path.get(1));
        Assert.assertEquals(destination, path.get(2));
    }

    @Test
    public void test3Tiles3() {
        Index start = new Index(4450, 831);
        Index destination = new Index(4260, 654);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(44, 7));
        tilePath.add(new Index(44, 6));
        tilePath.add(new Index(43, 6));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(3, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(new Index(4400, 699), path.get(1));
        Assert.assertEquals(destination, path.get(2));
    }

    @Test
    public void test3Tiles4() {
        Index start = new Index(4291, 635);
        Index destination = new Index(4065, 828);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(41, 6));
        tilePath.add(new Index(40, 6));
        tilePath.add(new Index(40, 7));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(3, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(new Index(4099, 699), path.get(1));
        Assert.assertEquals(destination, path.get(2));
    }

    @Test
    public void test1() {
        Index start = new Index(4466, 838);
        Index destination = new Index(3973, 815);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(44, 7));
        tilePath.add(new Index(44, 6));
        tilePath.add(new Index(43, 6));
        tilePath.add(new Index(42, 6));
        tilePath.add(new Index(41, 6));
        tilePath.add(new Index(40, 6));
        tilePath.add(new Index(40, 7));
        tilePath.add(new Index(40, 8));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(5, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(new Index(4400, 699), path.get(1));
        Assert.assertEquals(new Index(4099, 699), path.get(2));
        Assert.assertEquals(new Index(4000, 800), path.get(3));
        Assert.assertEquals(destination, path.get(4));
    }

    @Test
    public void test2() {
        Index start = new Index(3945, 799);
        Index destination = new Index(4455, 838);
        List<Index> tilePath = new ArrayList<Index>();
        tilePath.add(new Index(40, 7));
        tilePath.add(new Index(40, 6));
        tilePath.add(new Index(41, 6));
        tilePath.add(new Index(42, 6));
        tilePath.add(new Index(43, 6));
        tilePath.add(new Index(44, 6));
        tilePath.add(new Index(44, 7));

        FunnelAlgorithm funnelAlgorithm = new FunnelAlgorithm(start, destination);
        funnelAlgorithm.setTilePath(tilePath, terrainService);
        List<Index> path = funnelAlgorithm.stringPull();
        Assert.assertEquals(4, path.size());
        Assert.assertEquals(start, path.get(0));
        Assert.assertEquals(new Index(4099, 699), path.get(1));
        Assert.assertEquals(new Index(4400, 699), path.get(2));
        Assert.assertEquals(destination, path.get(3));
    }

}
