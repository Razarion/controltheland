package com.btxtech.game.services.collision;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.AwtMapFrame;
import com.btxtech.game.services.terrain.TerrainService;
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
    @Autowired
    private TerrainService terrainService;


    //@Test
    public void testGetPath() {
        Index start = new Index(740, 465);
        Index end = new Index(894, 543);
        collisionService.getPath(start, end);
    }

    @Test
    public void testFindPath() throws Exception {
        AwtMapFrame mapFrame = new AwtMapFrame(collisionService, terrainService);
        Thread.sleep(Integer.MAX_VALUE);
    }

}
