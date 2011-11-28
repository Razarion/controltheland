package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
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
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void createModifyResource() throws NoSuchItemTypeException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbItemType> crudRootServiceHelper = itemService.getDbItemTypeCrud();
        DbResourceItemType dbResourceItemType = (DbResourceItemType) crudRootServiceHelper.createDbChild(DbResourceItemType.class);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) itemService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        dbResourceItemType.setAmount(22);
        dbResourceItemType.setContraDescription("aaa");
        dbResourceItemType.setProDescription("bbb");
        dbResourceItemType.setDescription("ccc");
        dbResourceItemType.setName("Hallo");
        dbResourceItemType.setTerrainType(TerrainType.LAND);
        DbItemTypeImage dbItemTypeImage = dbResourceItemType.getItemTypeImageCrud().createDbChild();
        dbItemTypeImage.setContentType("image/jpg");
        dbItemTypeImage.setNumber(7);
        dbItemTypeImage.setData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
        dbResourceItemType.setBounding(new BoundingBox(1, 2, 3, 4, new double[]{0.12}));
        dbResourceItemType.setImageWidth(12);
        dbResourceItemType.setImageHeight(13);
        itemService.getDbItemTypeCrud().updateDbChild(dbResourceItemType);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        itemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        ResourceType resourceType = (ResourceType) itemService.getItemType(dbResourceItemType.getId());
        Assert.assertEquals(TerrainType.LAND, resourceType.getTerrainType());
        Assert.assertEquals(22, resourceType.getAmount());
        Assert.assertEquals(3, resourceType.getBoundingBox().getWidth());
        Assert.assertEquals(4, resourceType.getBoundingBox().getHeight());
        Assert.assertEquals(1, resourceType.getBoundingBox().getAngels().length);
        Assert.assertEquals(0.12, resourceType.getBoundingBox().getAngels()[0], 0.01);
        Assert.assertEquals(12, resourceType.getBoundingBox().getImageWidth());
        Assert.assertEquals(13, resourceType.getBoundingBox().getImageHeight());
        Assert.assertEquals("ccc", resourceType.getDescription());
        Assert.assertEquals("Hallo", resourceType.getName());

        dbItemTypeImage = itemService.getItemTypeImage(dbResourceItemType.getId(), 7);
        Assert.assertEquals("image/jpg", dbItemTypeImage.getContentType());
        Assert.assertEquals(7, dbItemTypeImage.getNumber());
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}, dbItemTypeImage.getData());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) itemService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        Assert.assertEquals(22, dbResourceItemType.getAmount());
        Assert.assertEquals(3, dbResourceItemType.getBoundingBox().getWidth());
        Assert.assertEquals(4, dbResourceItemType.getBoundingBox().getHeight());
        Assert.assertEquals("aaa", dbResourceItemType.getContraDescription());
        Assert.assertEquals("bbb", dbResourceItemType.getProDescription());
        Assert.assertEquals("ccc", dbResourceItemType.getDescription());
        Assert.assertEquals(12, dbResourceItemType.getImageWidth());
        Assert.assertEquals(13, dbResourceItemType.getImageHeight());
        Assert.assertEquals("Hallo", dbResourceItemType.getName());
        Assert.assertEquals(TerrainType.LAND, dbResourceItemType.getTerrainType());
        Assert.assertEquals(1, dbResourceItemType.getItemTypeImageCrud().readDbChildren().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) itemService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        dbResourceItemType.setAmount(23);
        dbResourceItemType.setContraDescription("dddd");
        dbResourceItemType.setProDescription("eeee");
        dbResourceItemType.setDescription("ffff");
        dbResourceItemType.setName("Ade");
        dbResourceItemType.setTerrainType(TerrainType.WATER);
        dbResourceItemType.getItemTypeImageCrud().deleteAllChildren();
        dbItemTypeImage = dbResourceItemType.getItemTypeImageCrud().createDbChild();
        dbItemTypeImage.setContentType("image/jpgxxx");
        dbItemTypeImage.setNumber(7);
        dbItemTypeImage.setData(new byte[]{8, 9, 0});
        dbResourceItemType.setBounding(new BoundingBox(2, 3, 4, 5, new double[]{0.13}));
        dbResourceItemType.setImageWidth(14);
        dbResourceItemType.setImageHeight(15);
        itemService.getDbItemTypeCrud().updateDbChild(dbResourceItemType);
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        itemService.activate();
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

        resourceType = (ResourceType) itemService.getItemType(dbResourceItemType.getId());
        Assert.assertEquals(TerrainType.WATER, resourceType.getTerrainType());
        Assert.assertEquals(23, resourceType.getAmount());
        Assert.assertEquals(4, resourceType.getBoundingBox().getWidth());
        Assert.assertEquals(5, resourceType.getBoundingBox().getHeight());
        Assert.assertEquals(1, resourceType.getBoundingBox().getAngels().length);
        Assert.assertEquals(0.13, resourceType.getBoundingBox().getAngels()[0], 0.1);
        Assert.assertEquals(14, resourceType.getBoundingBox().getImageWidth());
        Assert.assertEquals(15, resourceType.getBoundingBox().getImageHeight());
        Assert.assertEquals("ffff", resourceType.getDescription());
        Assert.assertEquals("Ade", resourceType.getName());

        dbItemTypeImage = itemService.getItemTypeImage(dbResourceItemType.getId(), 7);
        Assert.assertEquals("image/jpgxxx", dbItemTypeImage.getContentType());
        Assert.assertEquals(7, dbItemTypeImage.getNumber());
        Assert.assertArrayEquals(new byte[]{8, 9, 0}, dbItemTypeImage.getData());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) itemService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        Assert.assertEquals(23, dbResourceItemType.getAmount());
        Assert.assertEquals(4, dbResourceItemType.getBoundingBox().getWidth());
        Assert.assertEquals(5, dbResourceItemType.getBoundingBox().getHeight());
        Assert.assertEquals("dddd", dbResourceItemType.getContraDescription());
        Assert.assertEquals("eeee", dbResourceItemType.getProDescription());
        Assert.assertEquals("ffff", dbResourceItemType.getDescription());
        Assert.assertEquals(14, dbResourceItemType.getImageWidth());
        Assert.assertEquals(15, dbResourceItemType.getImageHeight());
        Assert.assertEquals("Ade", dbResourceItemType.getName());
        Assert.assertEquals(TerrainType.WATER, dbResourceItemType.getTerrainType());
        Assert.assertEquals(1, dbResourceItemType.getItemTypeImageCrud().readDbChildren().size());

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createModifyBaseItemType() throws NoSuchItemTypeException {
        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        CrudRootServiceHelper<DbItemType> crudRootServiceHelper = itemService.getDbItemTypeCrud();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) crudRootServiceHelper.createDbChild(DbBaseItemType.class);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().readDbChild(dbBaseItemType.getId());
        DbItemTypeImage dbItemTypeImage = dbBaseItemType.getItemTypeImageCrud().createDbChild();
        dbItemTypeImage.setData(new byte[]{1, 2});
        dbItemTypeImage.setNumber(1);
        dbItemTypeImage.setContentType("1");
        dbItemTypeImage = dbBaseItemType.getItemTypeImageCrud().createDbChild();
        dbItemTypeImage.setData(new byte[]{2, 3});
        dbItemTypeImage.setNumber(2);
        dbItemTypeImage.setContentType("2");
        dbItemTypeImage = dbBaseItemType.getItemTypeImageCrud().createDbChild();
        dbItemTypeImage.setData(new byte[]{3, 4});
        dbItemTypeImage.setNumber(3);
        dbItemTypeImage.setContentType("3");
        itemService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().readDbChild(dbBaseItemType.getId());
        BaseItemType baseItemType = (BaseItemType) dbBaseItemType.createItemType();
        Assert.assertEquals(3, baseItemType.getBoundingBox().getAngels().length);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().readDbChild(dbBaseItemType.getId());
        BoundingBox boundingBox = new BoundingBox(1, 2, 3, 4, new double[]{1});
        try {
            dbBaseItemType.setBounding(boundingBox);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException ignore) {
            // Expected
        }
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().readDbChild(dbBaseItemType.getId());
        boundingBox = new BoundingBox(1, 2, 3, 4, new double[]{0.1, 0.2, 0.3});
        dbBaseItemType.setBounding(boundingBox);
        itemService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);
        endHttpRequestAndOpenSessionInViewFilter();

        beginHttpRequestAndOpenSessionInViewFilter();
        dbBaseItemType = (DbBaseItemType) itemService.getDbItemTypeCrud().readDbChild(dbBaseItemType.getId());
        boundingBox = dbBaseItemType.getBoundingBox();
        Assert.assertEquals(3, boundingBox.getAngels().length);
        Assert.assertEquals(0.1, boundingBox.getAngels()[0], 0.001);
        Assert.assertEquals(0.2, boundingBox.getAngels()[1], 0.001);
        Assert.assertEquals(0.3, boundingBox.getAngels()[2], 0.001);
        endHttpRequestAndOpenSessionInViewFilter();

        endHttpSession();

    }

}
