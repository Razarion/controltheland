package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

/**
 * User: beat
 * Date: 18.11.2011
 * Time: 20:09:09
 */
public class TestCreateModifyItems extends AbstractServiceTest {
    @Autowired
    private ServerItemTypeService serverItemTypeService;

    @Test
    @DirtiesContext
    public void createModifyResource() throws NoSuchItemTypeException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbItemType> crudRootServiceHelper = serverItemTypeService.getDbItemTypeCrud();
        DbResourceItemType dbResourceItemType = (DbResourceItemType) crudRootServiceHelper.createDbChild(DbResourceItemType.class);
        endHttpRequestAndOpenSessionInViewFilter();
        // Create
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        dbResourceItemType.setAmount(22);
        dbResourceItemType.getDbI18nDescription().putString("ccc");
        dbResourceItemType.setName("Hallo");
        dbResourceItemType.getDbI18nName().putString("NameI81n");
        dbResourceItemType.setTerrainType(TerrainType.LAND);
        dbResourceItemType.setBounding(new BoundingBox(3, new double[]{0.12}));
        dbResourceItemType.setImageWidth(12);
        dbResourceItemType.setImageHeight(13);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbResourceItemType);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        Assert.assertEquals(22, dbResourceItemType.getAmount());
        Assert.assertEquals(3, dbResourceItemType.createBoundingBox().getRadius());
        Assert.assertEquals("ccc", dbResourceItemType.getDbI18nDescription().getString());
        Assert.assertEquals(12, dbResourceItemType.getImageWidth());
        Assert.assertEquals(13, dbResourceItemType.getImageHeight());
        Assert.assertEquals("Hallo", dbResourceItemType.getName());
        Assert.assertEquals("NameI81n", dbResourceItemType.getDbI18nName().getString());
        Assert.assertEquals(TerrainType.LAND, dbResourceItemType.getTerrainType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify cache
        ResourceType resourceType = (ResourceType) serverItemTypeService.getItemType(dbResourceItemType.getId());
        Assert.assertEquals(TerrainType.LAND, resourceType.getTerrainType());
        Assert.assertEquals(22, resourceType.getAmount());
        Assert.assertEquals(3, resourceType.getBoundingBox().getRadius());
        Assert.assertEquals(1, resourceType.getBoundingBox().getAngels().length);
        Assert.assertEquals(0.12, resourceType.getBoundingBox().getAngels()[0], 0.01);
        Assert.assertEquals("ccc", resourceType.getDescription().getString());
        Assert.assertEquals("NameI81n", resourceType.getI18Name().getString());
        Assert.assertEquals("Hallo", resourceType.getName());
        // Modify
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        dbResourceItemType.setAmount(23);
        dbResourceItemType.getDbI18nDescription().putString("ffff");
        dbResourceItemType.setName("Ade");
        dbResourceItemType.getDbI18nName().putString("NameI81n2");
        dbResourceItemType.setTerrainType(TerrainType.WATER);
        dbResourceItemType.setBounding(new BoundingBox(4, new double[]{0.13}));
        dbResourceItemType.setImageWidth(14);
        dbResourceItemType.setImageHeight(15);
        serverItemTypeService.getDbItemTypeCrud().updateDbChild(dbResourceItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        serverItemTypeService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify DB
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) serverItemTypeService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        Assert.assertEquals(23, dbResourceItemType.getAmount());
        Assert.assertEquals(4, dbResourceItemType.createBoundingBox().getRadius());
        Assert.assertEquals("ffff", dbResourceItemType.getDbI18nDescription().getString());
        Assert.assertEquals(14, dbResourceItemType.getImageWidth());
        Assert.assertEquals(15, dbResourceItemType.getImageHeight());
        Assert.assertEquals("Ade", dbResourceItemType.getName());
        Assert.assertEquals("NameI81n2", dbResourceItemType.getDbI18nName().getString());
        Assert.assertEquals(TerrainType.WATER, dbResourceItemType.getTerrainType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
        // Verify cache
        resourceType = (ResourceType) serverItemTypeService.getItemType(dbResourceItemType.getId());
        Assert.assertEquals(TerrainType.WATER, resourceType.getTerrainType());
        Assert.assertEquals(23, resourceType.getAmount());
        Assert.assertEquals(4, resourceType.getBoundingBox().getRadius());
        Assert.assertEquals(1, resourceType.getBoundingBox().getAngels().length);
        Assert.assertEquals(0.13, resourceType.getBoundingBox().getAngels()[0], 0.1);
        Assert.assertEquals("ffff", resourceType.getDescription().getString());
        Assert.assertEquals("Ade", resourceType.getName());
        Assert.assertEquals("NameI81n2", resourceType.getI18Name().getString());
    }
}
