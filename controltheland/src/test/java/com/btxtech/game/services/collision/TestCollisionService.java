package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: beat
 * Date: Jul 19, 2009
 * Time: 5:46:45 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"})
public class TestCollisionService {
    @Autowired
    private CollisionService collisionService;

    @Test
    public void testPathfinder1() {
        Index start = new Index(439, 412);
        Index end = new Index(19497, 1597);
        collisionService.setupPathToDestination(start,end, TerrainType.LAND);
    }

    @Test
    public void testPathfinder2() {
        Index start = new Index(7481, 20);
        Index end = new Index(19380, 19946);
        collisionService.setupPathToDestination(start,end, TerrainType.LAND);
    }

}
