package com.btxtech.game.services.planet.impl;

import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.TestGlobalServices;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbTerrainSetting;
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
        dbTerrainSetting.setTileXCount(100);
        dbTerrainSetting.setTileYCount(100);
        AbstractServiceTest.setPrivateField(DbTerrainSetting.class, dbTerrainSetting, "dbSurfaceRects", new HashSet<>());
        AbstractServiceTest.setPrivateField(DbTerrainSetting.class, dbTerrainSetting, "dbTerrainImagePositions", new HashSet<>());

        PlanetImpl planet = new PlanetImpl();
        planet.init(new TestGlobalServices());
        planet.activate(dbPlanet);
    }
}
