/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbResourceItemType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 21:21:37
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:war/WEB-INF/applicationContext.xml"})
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    //@Test
    public void testSaveItemType() {
        ArrayList<DbItemType> itemTypes = new ArrayList<DbItemType>();
        // CV
        DbBaseItemType cv = new DbBaseItemType();
        cv.setName("Construction Vehicle");
        cv.setDescription("Your construction vehicle");
        cv.setHeight(64);
        cv.setWidth(64);
        cv.setPrice(1);
        cv.setHealth(1000);
        DbMovableType dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(100);
        dbMovableType.setTerrainType(SurfaceType.LAND);
        cv.setMovableType(dbMovableType);
        itemTypes.add(cv);
        // Factroy
        DbBaseItemType factroy = new DbBaseItemType();
        factroy.setPrice(1);
        factroy.setName("Factroy");
        factroy.setDescription("Your Factory");
        factroy.setHeight(80);
        factroy.setWidth(80);
        factroy.setHealth(1000);
        itemTypes.add(factroy);
        // Tank
        DbBaseItemType tank = new DbBaseItemType();
        tank.setPrice(1);
        tank.setName("Tank");
        tank.setDescription("Your Tank");
        tank.setHeight(64);
        tank.setWidth(64);
        tank.setHealth(1000);
        dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(200);
        dbMovableType.setTerrainType(SurfaceType.LAND);
        tank.setMovableType(dbMovableType);
        DbWeaponType weaponType = new DbWeaponType();
        weaponType.setDamage(100);
        weaponType.setRange(100);
        tank.setWeaponType(weaponType);
        itemTypes.add(tank);
        // Harvester
        DbBaseItemType harvester = new DbBaseItemType();
        harvester.setPrice(1);
        harvester.setName("Harvester");
        harvester.setDescription("Your Harvester");
        harvester.setHeight(64);
        harvester.setWidth(64);
        harvester.setHealth(1000);
        dbMovableType = new DbMovableType();
        dbMovableType.setSpeed(200);
        dbMovableType.setTerrainType(SurfaceType.LAND);
        harvester.setMovableType(dbMovableType);
        DbHarvesterType harvesterType = new DbHarvesterType();
        harvesterType.setProgress(150);
        harvesterType.setProgress(180);
        harvester.setHarvesterType(harvesterType);
        itemTypes.add(harvester);
        // Relations
        TreeSet<DbBaseItemType> ableToBuild = new TreeSet<DbBaseItemType>();
        ableToBuild.add(factroy);
        DbBuilderType builderType = new DbBuilderType();
        builderType.setAbleToBuild(ableToBuild);
        builderType.setProgress(100);
        builderType.setRange(100);
        cv.setBuilderType(builderType);

        ableToBuild = new TreeSet<DbBaseItemType>();
        ableToBuild.add(cv);
        ableToBuild.add(tank);
        ableToBuild.add(harvester);
        DbFactoryType factoryType = new DbFactoryType();
        factoryType.setProgress(100);
        factoryType.setAbleToBuild(ableToBuild);
        factroy.setFactoryType(factoryType);

        // Money
        DbResourceItemType resource = new DbResourceItemType();
        resource.setAmount(5000);
        resource.setName("Money");
        resource.setDescription("Money to collect");
        resource.setHeight(64);
        resource.setWidth(64);
        itemTypes.add(resource);
        //

        itemService.saveDbItemTypes(itemTypes);
    }

    //@Test
    public void testLoadItemType() {
        itemService.getDbItemTypes();
    }

    //@Test
    public void testManipulate() {
        Collection<DbItemType> dbItemTypes = itemService.getDbItemTypes();
        for (DbItemType dbItemType : dbItemTypes) {
            if (dbItemType instanceof DbBaseItemType) {
                if (((DbBaseItemType) dbItemType).getFactoryType() != null) {
                    ((DbBaseItemType) dbItemType).getFactoryType().setProgress(1000);
                    itemService.saveDbItemTypes(dbItemTypes);
                    return;
                }
            }
        }
    }

    @Test
    public void testManipulate2() throws InterruptedException {
       /* Collection<DbItemType> ignore = itemService.getDbItemTypes();
        Collection<DbItemType> dbItemTypes = itemService.getDbItemTypes();
        for (final DbItemType dbBaseItemType : dbItemTypes) {
            if (dbBaseItemType.getId() == 4) {
                Set<DbItemTypeImage> itemTypeImages = fillImages(dbBaseItemType);
                itemService.removeItemTypeImages(dbBaseItemType);
                dbBaseItemType.setItemTypeImages(itemTypeImages);
                itemService.saveDbItemType(dbBaseItemType);
                return;
            }
        }*/

        Collection<DbItemType> dbItemTypes = itemService.getDbItemTypes();
        HashMap<Integer, DbItemType> itemType = new HashMap<Integer, DbItemType>();
        for (DbItemType dbItemType : dbItemTypes) {
           itemType.put(dbItemType.getId(), dbItemType);
        }

        DbBaseItemType dbBaseItemType1 = (DbBaseItemType) itemType.get(4); // Construction vehicle
       // DbBaseItemType dbBaseItemType2 = (DbBaseItemType) itemService.getDbItemType(4);
        Set<DbBaseItemType> ableoToBuild = new HashSet<DbBaseItemType>();
        //ableoToBuild.add((DbBaseItemType) itemService.getDbItemType(1));
        //ableoToBuild.add((DbBaseItemType) itemService.getDbItemType(2));
        ableoToBuild.add((DbBaseItemType) itemType.get(3));
        dbBaseItemType1.getBuilderType().setAbleToBuild(ableoToBuild);
        //itemService.removeItemTypeImages(dbBaseItemType1);
        //dbBaseItemType1.setItemTypeImages(fillImages(dbBaseItemType1));
       // itemService.saveDbItemType(dbBaseItemType1);
    }

    private Set<DbItemTypeImage> fillImages(DbItemType dbItemType) {
        Set<DbItemTypeImage> itemTypeImages = new HashSet<DbItemTypeImage>();
        for (int i = 1; i < 25; i++) {
            DbItemTypeImage itemTypeImage = new DbItemTypeImage();
            itemTypeImage.setContentType("image/png");
            itemTypeImage.setData(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
            itemTypeImage.setItemType(dbItemType);
            itemTypeImage.setNumber(i);
            itemTypeImages.add(itemTypeImage);
        }
        return itemTypeImages;
    }


}
