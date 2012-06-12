package com.btxtech.game.services.terrain;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImageBackground;
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
        configureComplexGameOneRealLevel();

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
        configureComplexGameOneRealLevel();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Rectangle terrainImage1 = new Rectangle(1000, 0, 400, 1000);

        for (int i = 100; i < 1500; i++) {
            Index index = new Index(i, 100);
            Assert.assertEquals(!terrainImage1.adjoinsEclusive(new Rectangle(i - 50, 50, 100, 100)), terrainService.isFree(index, 100, 100, allowedSurfaces));
        }
    }

    @Test
    @DirtiesContext
    public void testIsFreeYCount() throws Exception {
        configureComplexGameOneRealLevel();

        Collection<SurfaceType> allowedSurfaces = Arrays.asList(SurfaceType.LAND);

        Rectangle terrainImage2 = new Rectangle(0, 1300, 1000, 400);

        for (int i = 100; i < 1500; i++) {
            Index index = new Index(100, i);
            Assert.assertEquals(!terrainImage2.adjoinsEclusive(new Rectangle(50, i - 50, 100, 100)), terrainService.isFree(index, 100, 100, allowedSurfaces));
        }

    }

    @Test
    @DirtiesContext
    public void testTerrainImageBackground() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainSetting dbTerrainSetting = terrainService.getDbTerrainSettingCrudServiceHelper().createDbChild();
        dbTerrainSetting.setName("Test Real Game");
        dbTerrainSetting.setRealGame(true);
        dbTerrainSetting.setTileWidth(100);
        dbTerrainSetting.setTileHeight(100);
        dbTerrainSetting.setTileXCount(10);
        dbTerrainSetting.setTileYCount(10);

        DbTerrainImageGroup dbTerrainImageGroup1 = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup1.setHtmlBackgroundColorNone("#000000");
        dbTerrainImageGroup1.setHtmlBackgroundColorWater("#000001");
        dbTerrainImageGroup1.setHtmlBackgroundColorLand("#000002");
        dbTerrainImageGroup1.setHtmlBackgroundColorWaterCoast("#000003");
        dbTerrainImageGroup1.setHtmlBackgroundColorLandCoast("#000004");
        DbTerrainImage dbTerrainImage11 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage12 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage13 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage14 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage15 = dbTerrainImageGroup1.getTerrainImageCrud().createDbChild();
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup1);

        DbTerrainImageGroup dbTerrainImageGroup2 = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup2.setHtmlBackgroundColorNone("#000010");
        dbTerrainImageGroup2.setHtmlBackgroundColorWater("#000011");
        dbTerrainImageGroup2.setHtmlBackgroundColorLand("#000012");
        dbTerrainImageGroup2.setHtmlBackgroundColorWaterCoast("#000013");
        dbTerrainImageGroup2.setHtmlBackgroundColorLandCoast("#000014");
        DbTerrainImage dbTerrainImage21 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage22 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage23 = dbTerrainImageGroup2.getTerrainImageCrud().createDbChild();
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup2);

        DbTerrainImageGroup dbTerrainImageGroup3 = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup3.setHtmlBackgroundColorNone("#000020");
        dbTerrainImageGroup3.setHtmlBackgroundColorWater("#000021");
        dbTerrainImageGroup3.setHtmlBackgroundColorLand("#000022");
        dbTerrainImageGroup3.setHtmlBackgroundColorWaterCoast("#000023");
        DbTerrainImage dbTerrainImage31 = dbTerrainImageGroup3.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage32 = dbTerrainImageGroup3.getTerrainImageCrud().createDbChild();
        DbTerrainImage dbTerrainImage33 = dbTerrainImageGroup3.getTerrainImageCrud().createDbChild();
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup3);

        terrainService.activateTerrain();

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        AbstractTerrainServiceImpl abstractTerrainService = (AbstractTerrainServiceImpl) deAopProxy(terrainService);
        TerrainImageBackground backgrounds = abstractTerrainService.getTerrainImageBackground();

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage11.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage12.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage13.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage14.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000000", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000001", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000002", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000003", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000004", backgrounds.get(dbTerrainImage15.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000014", backgrounds.get(dbTerrainImage21.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000014", backgrounds.get(dbTerrainImage22.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000010", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000011", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000012", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000013", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#000014", backgrounds.get(dbTerrainImage23.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000023", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage31.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000023", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage32.getId(), SurfaceType.LAND_COAST));

        Assert.assertEquals("#000020", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.NONE));
        Assert.assertEquals("#000021", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.WATER));
        Assert.assertEquals("#000022", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.LAND));
        Assert.assertEquals("#000023", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.WATER_COAST));
        Assert.assertEquals("#FFFFFF", backgrounds.get(dbTerrainImage33.getId(), SurfaceType.LAND_COAST));
    }

}