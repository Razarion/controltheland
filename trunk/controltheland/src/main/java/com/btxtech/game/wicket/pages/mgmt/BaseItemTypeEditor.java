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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBuilderType;
import com.btxtech.game.services.item.itemType.DbConsumerType;
import com.btxtech.game.services.item.itemType.DbFactoryType;
import com.btxtech.game.services.item.itemType.DbGeneratorType;
import com.btxtech.game.services.item.itemType.DbHarvesterType;
import com.btxtech.game.services.item.itemType.DbHouseType;
import com.btxtech.game.services.item.itemType.DbItemContainerType;
import com.btxtech.game.services.item.itemType.DbItemTypeData;
import com.btxtech.game.services.item.itemType.DbLauncherType;
import com.btxtech.game.services.item.itemType.DbMovableType;
import com.btxtech.game.services.item.itemType.DbProjectileItemType;
import com.btxtech.game.services.item.itemType.DbSpecialType;
import com.btxtech.game.services.item.itemType.DbTurnableType;
import com.btxtech.game.services.item.itemType.DbWeaponType;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.ImageIcon;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: Sep 4, 2009
 * Time: 10:35:35 PM
 */
public class BaseItemTypeEditor extends WebPage {
    @SpringBean
    private ItemService itemService;
    private DbBaseItemType dbBaseItemType;
    private boolean turnable;
    private int imageCount;
    private boolean movable;
    private int speed;
    private boolean weapon;
    private int damage;
    private int weaponRange;
    private double weaponReloadTime;
    private FileUpload weaponSound;
    private FileUpload weaponMuzzleImage;
    private int weaponMuzzlePointX_0;
    private int weaponMuzzlePointY_0;
    private int weaponMuzzlePointX_90;
    private int weaponMuzzlePointY_90;
    private boolean weaponMuzzleStretch;
    private boolean factory;
    private double factoryProgress;
    private String factoryAbleToBuild;
    private boolean harvester;
    private int harvesterRange;
    private double harvesterProgress;
    private boolean builder;
    private double builderProgress;
    private int builderRange;
    private String builderAbleToBuild;
    private boolean consumer;
    private int consumerWattage;
    private boolean generator;
    private int generatorWattage;
    private boolean itemContainer;
    private String itemContainerAbleToContain;
    private int itemContainerRange;
    private int itemContainerMaxCount;
    private boolean house;
    private int space;
    private boolean launcher;
    private double launcherProgress;
    private Integer launcherDbProjectileItemType;
    private boolean special;
    private String specialString;
    private String imageFileField;
    private Collection<DbBaseItemType> baseItemTypes;
    private Collection<DbProjectileItemType> projectileItemTypes;


    public BaseItemTypeEditor(DbBaseItemType dbBaseItemType) {
        // Prevent circular object from with same id -> Hibernate problem
        baseItemTypes = itemService.getDbBaseItemTypes();
        projectileItemTypes = itemService.getDbProjectileItemTypes();
        this.dbBaseItemType = ItemsUtil.getItemType4Id(dbBaseItemType.getId(), baseItemTypes);

        FeedbackPanel feedbackPanel = new FeedbackPanel("msgs");
        add(feedbackPanel);

        Form<BaseItemTypeEditor> form = new Form<BaseItemTypeEditor>("itemTypeForm", new CompoundPropertyModel<BaseItemTypeEditor>(this));

        form.add(new TextField<String>("name"));
        form.add(new TextArea<String>("description"));
        form.add(new TextArea<String>("proDescription"));
        form.add(new TextArea<String>("contraDescription"));
        form.add(new TextField<String>("health"));
        form.add(new TextField<String>("price"));
        form.add(new TextField<Double>("buildup"));
        form.add(new DropDownChoice<TerrainType>("terrainType", Arrays.asList(TerrainType.values())));
        form.add(new TextField<String>("upgradeable"));
        form.add(new TextField<String>("upgradeProgress"));
        form.add(new CheckBox("turnable"));
        form.add(new TextField("imageCount"));
        form.add(new CheckBox("movable"));
        form.add(new TextField("speed"));
        form.add(new CheckBox("weapon"));
        form.add(new TextField("damage"));
        form.add(new TextField("weaponRange"));
        form.add(new TextField("weaponReloadTime"));
        form.add(new FileUploadField("weaponSound"));
        form.add(new FileUploadField("weaponMuzzleImage"));
        form.add(new TextField("weaponMuzzlePointX_0"));
        form.add(new TextField("weaponMuzzlePointY_0"));
        form.add(new TextField("weaponMuzzlePointX_90"));
        form.add(new TextField("weaponMuzzlePointY_90"));
        form.add(new CheckBox("weaponMuzzleStretch"));
        form.add(new CheckBox("launcher"));
        form.add(new TextField("launcherProgress"));
        form.add(new TextField("launcherDbProjectileItemType"));
        form.add(new CheckBox("factory"));
        form.add(new TextField("factoryProgress"));
        form.add(new TextField("factoryAbleToBuild"));
        form.add(new CheckBox("harvester"));
        form.add(new TextField("harvesterRange"));
        form.add(new TextField("harvesterProgress"));
        form.add(new CheckBox("builder"));
        form.add(new TextField("builderProgress"));
        form.add(new TextField("builderRange"));
        form.add(new TextField("builderAbleToBuild"));
        form.add(new CheckBox("consumer"));
        form.add(new TextField("consumerWattage"));
        form.add(new CheckBox("generator"));
        form.add(new TextField("generatorWattage"));
        form.add(new CheckBox("itemContainer"));
        form.add(new TextField("itemContainerAbleToContain"));
        form.add(new TextField("itemContainerRange"));
        form.add(new TextField("itemContainerMaxCount"));
        form.add(new CheckBox("house"));
        form.add(new TextField("space"));
        form.add(new CheckBox("special"));
        form.add(new TextField("specialString"));
        form.add(new HiddenField("imageFileField"));

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                save();
                setResponsePage(ItemTypeTable.class);
            }
        });
        form.add(new Button("cancel") {
            @Override
            public void onSubmit() {
                setResponsePage(ItemTypeTable.class);
            }
        });
        add(form);

        load();
    }

    private void load() {
        if (dbBaseItemType.getTurnableType() != null) {
            turnable = true;
            imageCount = dbBaseItemType.getTurnableType().getImageCount();
        } else {
            turnable = false;
        }

        if (dbBaseItemType.getMovableType() != null) {
            movable = true;
            speed = dbBaseItemType.getMovableType().getSpeed();
        } else {
            movable = false;
        }

        if (dbBaseItemType.getWeaponType() != null) {
            weapon = true;
            damage = dbBaseItemType.getWeaponType().getDamage();
            weaponRange = dbBaseItemType.getWeaponType().getRange();
            weaponReloadTime = dbBaseItemType.getWeaponType().getReloadTime();
            weaponMuzzlePointX_0 = dbBaseItemType.getWeaponType().getMuzzlePointX_0();
            weaponMuzzlePointY_0 = dbBaseItemType.getWeaponType().getMuzzlePointY_0();
            weaponMuzzlePointX_90 = dbBaseItemType.getWeaponType().getMuzzlePointX_90();
            weaponMuzzlePointY_90 = dbBaseItemType.getWeaponType().getMuzzlePointY_90();
            weaponMuzzleStretch = dbBaseItemType.getWeaponType().isStretchMuzzleFlashToTarget();
        } else {
            weapon = false;
        }

        if (dbBaseItemType.getDbLauncherType() != null) {
            launcher = true;
            launcherProgress = dbBaseItemType.getDbLauncherType().getProgress();
            if (dbBaseItemType.getDbLauncherType().getDbProjectileItemType() != null) {
                launcherDbProjectileItemType = dbBaseItemType.getDbLauncherType().getDbProjectileItemType().getId();
            }
        } else {
            launcher = false;
        }

        if (dbBaseItemType.getFactoryType() != null) {
            factory = true;
            factoryProgress = dbBaseItemType.getFactoryType().getProgress();
            factoryAbleToBuild = ItemsUtil.itemTypesToString(dbBaseItemType.getFactoryType().getAbleToBuild());
        } else {
            factory = false;
        }

        if (dbBaseItemType.getHarvesterType() != null) {
            harvester = true;
            harvesterProgress = dbBaseItemType.getHarvesterType().getProgress();
            harvesterRange = dbBaseItemType.getHarvesterType().getRange();
        } else {
            harvester = false;
        }

        if (dbBaseItemType.getBuilderType() != null) {
            builder = true;
            builderProgress = dbBaseItemType.getBuilderType().getProgress();
            builderRange = dbBaseItemType.getBuilderType().getRange();
            builderAbleToBuild = ItemsUtil.itemTypesToString(dbBaseItemType.getBuilderType().getAbleToBuild());
        } else {
            builder = false;
        }

        if (dbBaseItemType.getConsumerType() != null) {
            consumer = true;
            consumerWattage = dbBaseItemType.getConsumerType().getWattage();
        } else {
            consumer = false;
        }

        if (dbBaseItemType.getGeneratorType() != null) {
            generator = true;
            generatorWattage = dbBaseItemType.getGeneratorType().getWattage();
        } else {
            generator = false;
        }

        if (dbBaseItemType.getDbItemContainerType() != null) {
            itemContainer = true;
            itemContainerAbleToContain = ItemsUtil.itemTypesToString(dbBaseItemType.getDbItemContainerType().getAbleToContain());
            itemContainerMaxCount = dbBaseItemType.getDbItemContainerType().getMaxCount();
            itemContainerRange = dbBaseItemType.getDbItemContainerType().getRange();
        } else {
            itemContainer = false;
        }

        if (dbBaseItemType.getDbHouseType() != null) {
            house = true;
            space = dbBaseItemType.getDbHouseType().getSpace();
        } else {
            house = false;
        }

        if (dbBaseItemType.getSpecialType() != null) {
            special = true;
            specialString = dbBaseItemType.getSpecialType().getString();
        } else {
            special = false;
        }
    }

    private void save() {
        if (turnable) {
            DbTurnableType turnableType = dbBaseItemType.getTurnableType();
            if (turnableType == null) {
                turnableType = new DbTurnableType();
                dbBaseItemType.setTurnableType(turnableType);
            }
            turnableType.setImageCount(imageCount);
        } else {
            dbBaseItemType.setTurnableType(null);
        }

        if (movable) {
            DbMovableType movableType = dbBaseItemType.getMovableType();
            if (movableType == null) {
                movableType = new DbMovableType();
                dbBaseItemType.setMovableType(movableType);
            }
            movableType.setSpeed(speed);
            movableType.setTerrainType(SurfaceType.LAND);
        } else {
            dbBaseItemType.setMovableType(null);
        }

        if (weapon) {
            DbWeaponType weaponType = dbBaseItemType.getWeaponType();
            if (weaponType == null) {
                weaponType = new DbWeaponType();
                dbBaseItemType.setWeaponType(weaponType);
            }
            weaponType.setRange(weaponRange);
            weaponType.setDamage(damage);
            weaponType.setReloadTime(weaponReloadTime);
            weaponType.setMuzzlePointX_0(weaponMuzzlePointX_0);
            weaponType.setMuzzlePointY_0(weaponMuzzlePointY_0);
            weaponType.setMuzzlePointX_90(weaponMuzzlePointX_90);
            weaponType.setMuzzlePointY_90(weaponMuzzlePointY_90);
            weaponType.setStretchMuzzleFlashToTarget(weaponMuzzleStretch);
            if (weaponMuzzleImage != null) {
                ImageIcon image = new ImageIcon(weaponMuzzleImage.getBytes());
                weaponType.setMuzzleFlashWidth(image.getIconWidth());
                weaponType.setMuzzleFlashLength(image.getIconHeight());
                DbItemTypeData itemTypeImage = new DbItemTypeData();
                itemTypeImage.setContentType(weaponMuzzleImage.getContentType());
                itemTypeImage.setData(weaponMuzzleImage.getBytes());
                weaponType.setDbMuzzleImage(itemTypeImage);
            }
            if (weaponSound != null) {
                DbItemTypeData sound = new DbItemTypeData();
                sound.setContentType(weaponSound.getContentType());
                sound.setData(weaponSound.getBytes());
                weaponType.setDbSound(sound);
            }
        } else {
            dbBaseItemType.setWeaponType(null);
        }

        if (launcher) {
            DbLauncherType dbLauncherType = dbBaseItemType.getDbLauncherType();
            if (dbLauncherType == null) {
                dbLauncherType = new DbLauncherType();
                dbBaseItemType.setDbLauncherType(dbLauncherType);
            }
            dbLauncherType.setProgress(launcherProgress);
            if (launcherDbProjectileItemType != null) {
                dbLauncherType.setDbProjectileItemType(ItemsUtil.getProjectileItemType4Id(launcherDbProjectileItemType, projectileItemTypes));
            }
        } else {
            dbBaseItemType.setDbLauncherType(null);
        }

        if (factory) {
            DbFactoryType factoryType = dbBaseItemType.getFactoryType();
            if (factoryType == null) {
                factoryType = new DbFactoryType();
                dbBaseItemType.setFactoryType(factoryType);
            }
            factoryType.setAbleToBuild(ItemsUtil.stringToItemTypes(factoryAbleToBuild, baseItemTypes));
            factoryType.setProgress(factoryProgress);
        } else {
            dbBaseItemType.setFactoryType(null);
        }

        if (harvester) {
            DbHarvesterType harvesterType = dbBaseItemType.getHarvesterType();
            if (harvesterType == null) {
                harvesterType = new DbHarvesterType();
                dbBaseItemType.setHarvesterType(harvesterType);
            }
            harvesterType.setProgress(harvesterProgress);
            harvesterType.setRange(harvesterRange);
        } else {
            dbBaseItemType.setHarvesterType(null);
        }

        if (consumer) {
            DbConsumerType consumerType = dbBaseItemType.getConsumerType();
            if (consumerType == null) {
                consumerType = new DbConsumerType();
                dbBaseItemType.setConsumerType(consumerType);
            }
            consumerType.setWattage(consumerWattage);
        } else {
            dbBaseItemType.setConsumerType(null);
        }

        if (builder) {
            DbBuilderType builderType = dbBaseItemType.getBuilderType();
            if (builderType == null) {
                builderType = new DbBuilderType();
                dbBaseItemType.setBuilderType(builderType);
            }
            builderType.setProgress(builderProgress);
            builderType.setRange(builderRange);
            builderType.setAbleToBuild(ItemsUtil.stringToItemTypes(builderAbleToBuild, baseItemTypes));
        } else {
            dbBaseItemType.setBuilderType(null);
        }

        if (generator) {
            DbGeneratorType generatorType = dbBaseItemType.getGeneratorType();
            if (generatorType == null) {
                generatorType = new DbGeneratorType();
                dbBaseItemType.setGeneratorType(generatorType);
            }
            generatorType.setWattage(generatorWattage);
        } else {
            dbBaseItemType.setGeneratorType(null);
        }

        if (itemContainer) {
            DbItemContainerType dbItemContainerType = dbBaseItemType.getDbItemContainerType();
            if (dbItemContainerType == null) {
                dbItemContainerType = new DbItemContainerType();
                dbBaseItemType.setDbItemContainerType(dbItemContainerType);
            }
            dbItemContainerType.setAbleToContain(ItemsUtil.stringToItemTypes(itemContainerAbleToContain, baseItemTypes));
            dbItemContainerType.setMaxCount(itemContainerMaxCount);
            dbItemContainerType.setRange(itemContainerRange);
        } else {
            dbBaseItemType.setDbItemContainerType(null);
        }

        if (house) {
            DbHouseType dbHouseType = new DbHouseType();
            dbHouseType.setSpace(space);
            dbBaseItemType.setDbHouseType(dbHouseType);
        } else {
            dbBaseItemType.setDbHouseType(null);
        }

        if (special) {
            DbSpecialType specialType = dbBaseItemType.getSpecialType();
            if (specialType == null) {
                specialType = new DbSpecialType();
                dbBaseItemType.setSpecialType(specialType);
            }
            specialType.setString(specialString);
        } else {
            dbBaseItemType.setSpecialType(null);
        }

        Html5ImagesUploadConverter html5ImagesUploadConverter = new Html5ImagesUploadConverter(imageFileField, dbBaseItemType);
        if (!html5ImagesUploadConverter.isEmpty()) {
            dbBaseItemType.setItemTypeImages(html5ImagesUploadConverter.getImages());
            ImageIcon image = new ImageIcon(html5ImagesUploadConverter.getFirst().getData());
            dbBaseItemType.setHeight(image.getIconHeight());
            dbBaseItemType.setWidth(image.getIconWidth());
        }

        itemService.saveDbItemType(dbBaseItemType);
    }

    public String getName() {
        return dbBaseItemType.getName();
    }

    public void setName(String name) {
        dbBaseItemType.setName(name);
    }

    public String getDescription() {
        return dbBaseItemType.getDescription();
    }

    public void setDescription(String description) {
        dbBaseItemType.setDescription(description);
    }

    public String getProDescription() {
        return dbBaseItemType.getProDescription();
    }

    public void setProDescription(String proDescription) {
        dbBaseItemType.setProDescription(proDescription);
    }

    public String getContraDescription() {
        return dbBaseItemType.getContraDescription();
    }

    public void setContraDescription(String contraDescription) {
        dbBaseItemType.setContraDescription(contraDescription);
    }

    public int getHealth() {
        return dbBaseItemType.getHealth();
    }

    public void setHealth(int health) {
        dbBaseItemType.setHealth(health);
    }

    public int getBuildup() {
        return dbBaseItemType.getBuildup();
    }

    public void setBuildup(int buildup) {
        dbBaseItemType.setBuildup(buildup);
    }

    public int getPrice() {
        return dbBaseItemType.getPrice();
    }

    public void setPrice(int price) {
        dbBaseItemType.setPrice(price);
    }

    public TerrainType getTerrainType() {
        return dbBaseItemType.getTerrainType();
    }

    public void setTerrainType(TerrainType terrainType) {
        dbBaseItemType.setTerrainType(terrainType);
    }

    public void setUpgradeable(Integer id) {
        if (id != null) {
            DbBaseItemType upgradeable = ItemsUtil.getItemType4Id(id, baseItemTypes);
            dbBaseItemType.setUpgradable(upgradeable);
        } else {
            dbBaseItemType.setUpgradable(null);
        }
    }

    public Integer getUpgradeable() {
        if (dbBaseItemType.getUpgradable() != null) {
            return dbBaseItemType.getUpgradable().getId();
        } else {
            return null;
        }
    }

    public void setUpgradeProgress(Integer progress) {
        dbBaseItemType.setUpgradeProgress(progress);
    }

    public Integer getUpgradeProgress() {
        return dbBaseItemType.getUpgradeProgress();
    }
}