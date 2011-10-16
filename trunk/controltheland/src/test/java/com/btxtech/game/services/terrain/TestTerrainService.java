package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.AbstractServiceTest;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.Collection;

/**
 * User: beat
 * Date: Jul 11, 2009
 * Time: 12:00:44 PM
 */
public class TestTerrainService extends AbstractServiceTest {
    @Autowired
    private TerrainService terrainService;

    @Test
    @DirtiesContext
    public void testIsFreeSimple() throws Exception {
        configureComplexGame();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Assert.assertFalse(terrainService.isFree(new Index(49, 49), 100, 100, allowedSurfaces));
        Assert.assertTrue(terrainService.isFree(new Index(50, 50), 100, 100, allowedSurfaces));
        Assert.assertFalse(terrainService.isFree(new Index(1050, 50), 100, 100, allowedSurfaces));

        Assert.assertTrue(terrainService.isFree(new Index(9950, 9950), 100, 100, allowedSurfaces));
        Assert.assertFalse(terrainService.isFree(new Index(9951, 9950), 100, 100, allowedSurfaces));
        Assert.assertFalse(terrainService.isFree(new Index(9950, 9951), 100, 100, allowedSurfaces));
    }

    @Test
    @DirtiesContext
    public void testIsFreeXCount() throws Exception {
        configureComplexGame();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Rectangle terrainImage1 =  new Rectangle(1000, 0, 400, 1000);

        for (int i = 100; i < 1500; i++) {
           Index index = new Index(i, 100);
            Assert.assertEquals(!terrainImage1.adjoinsEclusive(new Rectangle(i - 50, 50, 100, 100)), terrainService.isFree(index, 100, 100, allowedSurfaces));
        }
    }

    @Test
    @DirtiesContext
    public void testIsFreeYCount() throws Exception {
        configureComplexGame();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Rectangle terrainImage2 = new Rectangle(0, 1300, 1000, 400);

        for (int i = 100; i < 1500; i++) {
            Index index = new Index(100, i);
            Assert.assertEquals(!terrainImage2.adjoinsEclusive(new Rectangle(50, i - 50, 100, 100)), terrainService.isFree(index, 100, 100, allowedSurfaces));
        }

    }

}