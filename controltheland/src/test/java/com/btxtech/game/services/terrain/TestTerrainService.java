package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.BaseTestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
@DirtiesContext
public class TestTerrainService extends BaseTestService {
    @Autowired
    private TerrainService terrainService;

    @Test
    public void testLoadItemType() {
        //terrainService.getNearestPoint(TerrainType.LAND, new Index(4415, 361), 200);
    }


}