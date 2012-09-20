package com.btxtech.game.services.common;

import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.terrain.DbTerrainImage;
import com.btxtech.game.services.terrain.DbTerrainImageGroup;
import com.btxtech.game.services.terrain.TerrainImageService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 16.12.2011
 * Time: 13:31:31
 */
public class TestRuServiceHelper extends AbstractServiceTest {
    @Autowired
    private TerrainImageService terrainService;
    @Autowired
    RuServiceHelper<DbTerrainImageGroup> ruServiceHelper;
    @Autowired
    RuServiceHelper<DbTerrainImage> imageRuServiceHelper;

    @Test
    @DirtiesContext
    public void createModifyDelete() throws Exception {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbTerrainImageGroup dbTerrainImageGroup = terrainService.getDbTerrainImageGroupCrudServiceHelper().createDbChild();
        dbTerrainImageGroup.setHtmlBackgroundColorLand("Land");
        DbTerrainImage dbTerrainImage = dbTerrainImageGroup.getTerrainImageCrud().createDbChild();
        dbTerrainImage.setTiles(1, 1);
        dbTerrainImage.setContentType("contetntyp1");
        terrainService.getDbTerrainImageGroupCrudServiceHelper().updateDbChild(dbTerrainImageGroup);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTerrainImageGroup = ruServiceHelper.readDbChild(dbTerrainImageGroup.getId(), DbTerrainImageGroup.class);
        Assert.assertNotNull(dbTerrainImageGroup);
        Assert.assertEquals("Land", dbTerrainImageGroup.getHtmlBackgroundColorLand());
        Assert.assertEquals(1, dbTerrainImageGroup.getTerrainImageCrud().readDbChildren().size());
        dbTerrainImage = CommonJava.getFirst(dbTerrainImageGroup.getTerrainImageCrud().readDbChildren());
        Assert.assertEquals(1, dbTerrainImage.getTileWidth());
        Assert.assertEquals(1, dbTerrainImage.getTileHeight());
        Assert.assertEquals("contetntyp1", dbTerrainImage.getContentType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Modify & save
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTerrainImageGroup = ruServiceHelper.readDbChild(dbTerrainImageGroup.getId(), DbTerrainImageGroup.class);
        dbTerrainImageGroup.setHtmlBackgroundColorLand("Water");
        dbTerrainImage = CommonJava.getFirst(dbTerrainImageGroup.getTerrainImageCrud().readDbChildren());
        dbTerrainImage.setContentType("contetntyp2");
        ruServiceHelper.updateDbEntity(dbTerrainImageGroup);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTerrainImageGroup = ruServiceHelper.readDbChild(dbTerrainImageGroup.getId(), DbTerrainImageGroup.class);
        Assert.assertNotNull(dbTerrainImageGroup);
        Assert.assertEquals("Water", dbTerrainImageGroup.getHtmlBackgroundColorLand());
        Assert.assertEquals(1, dbTerrainImageGroup.getTerrainImageCrud().readDbChildren().size());
        dbTerrainImage = CommonJava.getFirst(dbTerrainImageGroup.getTerrainImageCrud().readDbChildren());
        Assert.assertEquals(1, dbTerrainImage.getTileWidth());
        Assert.assertEquals(1, dbTerrainImage.getTileHeight());
        Assert.assertEquals("contetntyp2", dbTerrainImage.getContentType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Remove child
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTerrainImageGroup = ruServiceHelper.readDbChild(dbTerrainImageGroup.getId(), DbTerrainImageGroup.class);
        dbTerrainImage = CommonJava.getFirst(dbTerrainImageGroup.getTerrainImageCrud().readDbChildren());
        ruServiceHelper.removeChildAndUpdate(dbTerrainImageGroup, dbTerrainImageGroup.getTerrainImageCrud(), dbTerrainImage);
        Assert.assertEquals(1, dbTerrainImage.getTileWidth());
        Assert.assertEquals(1, dbTerrainImage.getTileHeight());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbTerrainImageGroup = ruServiceHelper.readDbChild(dbTerrainImageGroup.getId(), DbTerrainImageGroup.class);
        Assert.assertNotNull(dbTerrainImageGroup);
        Assert.assertEquals("Water", dbTerrainImageGroup.getHtmlBackgroundColorLand());
        Assert.assertEquals(0, dbTerrainImageGroup.getTerrainImageCrud().readDbChildren().size());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

}
