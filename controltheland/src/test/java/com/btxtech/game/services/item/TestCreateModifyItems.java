package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.AbstractServiceTest;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImageData;
import com.btxtech.game.services.item.itemType.DbItemTypeSoundData;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
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
        dbResourceItemType.setBounding(new BoundingBox(3, 4, new double[]{0.12}));
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
        Assert.assertEquals("ccc", resourceType.getDescription());
        Assert.assertEquals("Hallo", resourceType.getName());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) itemService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        Assert.assertEquals(22, dbResourceItemType.getAmount());
        Assert.assertEquals(3, dbResourceItemType.createBoundingBox().getWidth());
        Assert.assertEquals(4, dbResourceItemType.createBoundingBox().getHeight());
        Assert.assertEquals("aaa", dbResourceItemType.getContraDescription());
        Assert.assertEquals("bbb", dbResourceItemType.getProDescription());
        Assert.assertEquals("ccc", dbResourceItemType.getDescription());
        Assert.assertEquals(12, dbResourceItemType.getImageWidth());
        Assert.assertEquals(13, dbResourceItemType.getImageHeight());
        Assert.assertEquals("Hallo", dbResourceItemType.getName());
        Assert.assertEquals(TerrainType.LAND, dbResourceItemType.getTerrainType());
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
        dbResourceItemType.setBounding(new BoundingBox(4, 5, new double[]{0.13}));
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
        Assert.assertEquals("ffff", resourceType.getDescription());
        Assert.assertEquals("Ade", resourceType.getName());

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        dbResourceItemType = (DbResourceItemType) itemService.getDbItemTypeCrud().readDbChild(dbResourceItemType.getId());
        Assert.assertEquals(23, dbResourceItemType.getAmount());
        Assert.assertEquals(4, dbResourceItemType.createBoundingBox().getWidth());
        Assert.assertEquals(5, dbResourceItemType.createBoundingBox().getHeight());
        Assert.assertEquals("dddd", dbResourceItemType.getContraDescription());
        Assert.assertEquals("eeee", dbResourceItemType.getProDescription());
        Assert.assertEquals("ffff", dbResourceItemType.getDescription());
        Assert.assertEquals(14, dbResourceItemType.getImageWidth());
        Assert.assertEquals(15, dbResourceItemType.getImageHeight());
        Assert.assertEquals("Ade", dbResourceItemType.getName());
        Assert.assertEquals(TerrainType.WATER, dbResourceItemType.getTerrainType());
        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();
    }

    @Test
    @DirtiesContext
    public void createMuzzleFlashImageSound() throws Exception {
        configureRealGame();

        beginHttpSession();
        beginHttpRequestAndOpenSessionInViewFilter();
        DbBaseItemType dbBaseItemType = (DbBaseItemType) itemService.getDbItemType(TEST_ATTACK_ITEM_ID);
        DbWeaponType dbWeaponType = dbBaseItemType.getDbWeaponType();

        DbItemTypeImageData itemTypeImageData = new DbItemTypeImageData();
        itemTypeImageData.setContentType("xxx/yyy");
        itemTypeImageData.setData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        dbWeaponType.setMuzzleFlashImageData(itemTypeImageData);

        DbItemTypeSoundData dbItemTypeSoundData = new DbItemTypeSoundData();
        dbItemTypeSoundData.setDataOgg(new byte[]{9, 8, 7});
        dbItemTypeSoundData.setDataMp3(new byte[]{11, 12, 13, 14});
        dbWeaponType.setMuzzleFlashSoundData(dbItemTypeSoundData);

        itemService.getDbItemTypeCrud().updateDbChild(dbBaseItemType);

        endHttpRequestAndOpenSessionInViewFilter();
        endHttpSession();

    }
}
