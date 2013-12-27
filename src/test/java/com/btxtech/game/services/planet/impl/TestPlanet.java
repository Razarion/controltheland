package com.btxtech.game.services.planet.impl;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.TestGlobalServices;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbRegion;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 17:18
 */
public class TestPlanet {
    @Test
    public void basicTest() throws Exception {
        DbPlanet dbPlanet = new DbPlanet();
        AbstractServiceTest.setPrivateField(DbPlanet.class, dbPlanet, "id", 1);
        AbstractServiceTest.setPrivateField(DbPlanet.class, dbPlanet, "dbBoxRegions", new ArrayList<>());
        AbstractServiceTest.setPrivateField(DbPlanet.class, dbPlanet, "dbBotConfigs", new ArrayList<>());
        AbstractServiceTest.setPrivateField(DbPlanet.class, dbPlanet, "dbRegionResources", new ArrayList<>());
        AbstractServiceTest.setPrivateField(DbPlanet.class, dbPlanet, "dbPlanetItemTypeLimitations", new ArrayList<>());
        DbTerrainSetting dbTerrainSetting = new DbTerrainSetting();
        dbPlanet.setDbTerrainSetting(dbTerrainSetting);
        dbTerrainSetting.setTileXCount(80);
        dbTerrainSetting.setTileYCount(120);
        AbstractServiceTest.setPrivateField(DbTerrainSetting.class, dbTerrainSetting, "dbSurfaceRects", new HashSet<>());
        AbstractServiceTest.setPrivateField(DbTerrainSetting.class, dbTerrainSetting, "dbTerrainImagePositions", new HashSet<>());
        DbRegion dbRegion = new DbRegion();
        dbRegion.init(null);
        AbstractServiceTest.setPrivateField(DbRegion.class, dbRegion, "id", 1);
        dbPlanet.setStartRegion(dbRegion);

        PlanetImpl planet = new PlanetImpl();
        planet.init(new TestGlobalServices());
        planet.activate(dbPlanet);

        Assert.assertEquals(9600, dbPlanet.getSize());
    }
}
