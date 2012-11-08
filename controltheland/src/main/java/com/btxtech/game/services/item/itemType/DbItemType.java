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

package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.DemolitionStepSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.media.ClipService;
import com.btxtech.game.services.media.DbSound;
import com.btxtech.game.services.user.UserService;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: beat
 * Date: 09.12.2009
 * Time: 14:51:59
 */
@Entity(name = "ITEM_TYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class DbItemType implements DbItemTypeI, CrudChild, CrudParent {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private String proDescription;
    private String contraDescription;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "itemType", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Set<DbItemTypeImage> itemTypeImages;
    private TerrainType terrainType;
    private int boundingBoxRadius;
    private int imageWidth;
    private int imageHeight;
    private int buildupSteps;
    private int buildupAnimationFrames;
    private int buildupAnimationDuration;
    private int runtimeAnimationFrames;
    private int runtimeAnimationDuration;
    @ElementCollection
    @CollectionTable(name = "ITEM_TYPE_ANGELS",
            joinColumns = @JoinColumn(name = "itemTypeId"))
    @Column(name = "angel")
    private List<Double> angels;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbSound selectionSound;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbSound buildupSound;
    @ManyToOne(fetch = FetchType.LAZY)
    private DbSound commandSound;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "itemType", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<DbItemTypeDemolitionStep> itemTypeDemolitionSteps;

    @Transient
    private CrudChildServiceHelper<DbItemTypeImage> itemTypeImageCrud;


    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getProDescription() {
        return proDescription;
    }

    @Override
    public void setProDescription(String proDescription) {
        this.proDescription = proDescription;
    }

    @Override
    public String getContraDescription() {
        return contraDescription;
    }

    @Override
    public void setContraDescription(String contraDescription) {
        this.contraDescription = contraDescription;
    }

    @Override
    public TerrainType getTerrainType() {
        return terrainType;
    }

    @Override
    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    @Override
    public int getImageWidth() {
        return imageWidth;
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    public int getBuildupSteps() {
        return buildupSteps;
    }

    @Override
    public void setBuildupSteps(int buildupSteps) {
        this.buildupSteps = buildupSteps;
    }

    @Override
    public int getBuildupAnimationFrames() {
        return buildupAnimationFrames;
    }

    @Override
    public void setBuildupAnimationFrames(int buildupAnimationFrames) {
        this.buildupAnimationFrames = buildupAnimationFrames;
    }

    @Override
    public int getBuildupAnimationDuration() {
        return buildupAnimationDuration;
    }

    @Override
    public void setBuildupAnimationDuration(int buildupAnimationDuration) {
        this.buildupAnimationDuration = buildupAnimationDuration;
    }

    @Override
    public int getRuntimeAnimationFrames() {
        return runtimeAnimationFrames;
    }

    @Override
    public void setRuntimeAnimationFrames(int runtimeAnimationFrames) {
        this.runtimeAnimationFrames = runtimeAnimationFrames;
    }

    @Override
    public int getRuntimeAnimationDuration() {
        return runtimeAnimationDuration;
    }

    @Override
    public void setRuntimeAnimationDuration(int runtimeAnimationDuration) {
        this.runtimeAnimationDuration = runtimeAnimationDuration;
    }

    @Override
    public int getBoundingBoxRadius() {
        return boundingBoxRadius;
    }

    @Override
    public void setBoundingBoxRadius(int boundingBoxRadius) {
        this.boundingBoxRadius = boundingBoxRadius;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public DbSound getSelectionSound() {
        return selectionSound;
    }

    public void setSelectionSound(DbSound selectionSound) {
        this.selectionSound = selectionSound;
    }

    public DbSound getBuildupSound() {
        return buildupSound;
    }

    public void setBuildupSound(DbSound buildupSound) {
        this.buildupSound = buildupSound;
    }

    public DbSound getCommandSound() {
        return commandSound;
    }

    public void setCommandSound(DbSound commandSound) {
        this.commandSound = commandSound;
    }

    public BoundingBox createBoundingBox() {
        double[] angelsCopy = ArrayUtils.toPrimitive(angels.toArray(new Double[angels.size()]));
        Arrays.sort(angelsCopy);
        return new BoundingBox(boundingBoxRadius, angelsCopy);
    }

    public ItemTypeSpriteMap createItemTypeSpriteMap(BoundingBox boundingBox) {
        return new ItemTypeSpriteMap(boundingBox, imageWidth, imageHeight, buildupSteps,
                buildupAnimationFrames, buildupAnimationDuration, runtimeAnimationFrames,
                runtimeAnimationDuration, createDemolitionSteps());
    }

    public void setBounding(BoundingBox boundingBox) {
        boundingBoxRadius = boundingBox.getRadius();
        angels = Arrays.asList(ArrayUtils.toObject(boundingBox.getAngels()));
    }

    public void setTypeSpriteMap(ItemTypeSpriteMap itemTypeSpriteMap, ClipService clipService) {
        imageWidth = itemTypeSpriteMap.getImageWidth();
        imageHeight = itemTypeSpriteMap.getImageHeight();
        buildupSteps = itemTypeSpriteMap.getBuildupSteps();
        buildupAnimationFrames = itemTypeSpriteMap.getBuildupAnimationFrames();
        buildupAnimationDuration = itemTypeSpriteMap.getBuildupAnimationDuration();
        runtimeAnimationFrames = itemTypeSpriteMap.getRuntimeAnimationFrames();
        runtimeAnimationDuration = itemTypeSpriteMap.getRuntimeAnimationDuration();
        saveDemolitionSteps(itemTypeSpriteMap.getDemolitionSteps(), clipService);
    }

    private DemolitionStepSpriteMap[] createDemolitionSteps() {
        CrudListChildServiceHelper<DbItemTypeDemolitionStep> crud = getDemolitionCrud();
        if (crud == null) {
            return null;
        }
        List<DemolitionStepSpriteMap> demolitionStepList = new ArrayList<>();
        for (DbItemTypeDemolitionStep dbItemTypeDemolitionStep : crud.readDbChildren()) {
            demolitionStepList.add(dbItemTypeDemolitionStep.createDemolitionStepSpriteMap());
        }
        return demolitionStepList.toArray(new DemolitionStepSpriteMap[demolitionStepList.size()]);
    }

    private CrudListChildServiceHelper<DbItemTypeDemolitionStep> getDemolitionCrud() {
        if (itemTypeDemolitionSteps == null || itemTypeDemolitionSteps.isEmpty()) {
            return null;
        }
        return new CrudListChildServiceHelper<>(itemTypeDemolitionSteps, DbItemTypeDemolitionStep.class, this, null, new Comparator<DbItemTypeDemolitionStep>() {
            @Override
            public int compare(DbItemTypeDemolitionStep o1, DbItemTypeDemolitionStep o2) {
                return Integer.compare(o1.getStep(), o2.getStep());
            }
        });
    }

    private void saveDemolitionSteps(DemolitionStepSpriteMap[] demolitionStepSpriteMap, ClipService clipService) {
        CrudListChildServiceHelper<DbItemTypeDemolitionStep> crud = new CrudListChildServiceHelper<>(itemTypeDemolitionSteps, DbItemTypeDemolitionStep.class, this);
        crud.deleteAllChildren();
        if (demolitionStepSpriteMap == null) {
            return;
        }
        for (int i = 0; i < demolitionStepSpriteMap.length; i++) {
            DemolitionStepSpriteMap demolitionStep = demolitionStepSpriteMap[i];
            DbItemTypeDemolitionStep dbItemTypeDemolitionStep = crud.createDbChild();
            dbItemTypeDemolitionStep.setDemolitionStepSpriteMap(demolitionStep, i, clipService);
        }
    }

    public void saveImages(Collection<ItemTypeImageInfo> buildupImages, Collection<ItemTypeImageInfo> runtimeImages, Collection<ItemTypeImageInfo> demolitionImages) {
        addImages(buildupImages, ItemTypeSpriteMap.SyncObjectState.BUILD_UP);
        addImages(runtimeImages, ItemTypeSpriteMap.SyncObjectState.RUN_TIME);
        addImages(demolitionImages, ItemTypeSpriteMap.SyncObjectState.DEMOLITION);
        correctImageEntries();
    }

    private void addImages(Collection<ItemTypeImageInfo> itemTypeImageInfos, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        for (ItemTypeImageInfo itemTypeImageInfo : itemTypeImageInfos) {
            removeImage(itemTypeImageInfo, syncObjectState);
            DbItemTypeImage dbItemTypeImage = getItemTypeImageCrud().createDbChild();
            dbItemTypeImage.setItemTypeImageInfo(itemTypeImageInfo, syncObjectState);
        }
    }

    private void removeImage(ItemTypeImageInfo itemTypeImageInfo, ItemTypeSpriteMap.SyncObjectState syncObjectState) {
        for (Iterator<DbItemTypeImage> iterator = itemTypeImages.iterator(); iterator.hasNext(); ) {
            DbItemTypeImage itemTypeImage = iterator.next();
            if (itemTypeImage.getType() == syncObjectState) {
                switch (itemTypeImage.getType()) {
                    case BUILD_UP:
                        if (itemTypeImage.getStep() == itemTypeImageInfo.getStep() && itemTypeImage.getFrame() == itemTypeImageInfo.getFrame()) {
                            iterator.remove();
                        }
                        break;
                    case RUN_TIME:
                        if (itemTypeImage.getAngelIndex() == itemTypeImageInfo.getAngelIndex() && itemTypeImage.getFrame() == itemTypeImageInfo.getFrame()) {
                            iterator.remove();
                        }
                        break;
                    case DEMOLITION:
                        if (itemTypeImage.getAngelIndex() == itemTypeImageInfo.getAngelIndex() && itemTypeImage.getStep() == itemTypeImageInfo.getStep() && itemTypeImage.getFrame() == itemTypeImageInfo.getFrame()) {
                            iterator.remove();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown SyncObjectState: " + itemTypeImage.getType() + " itemType: " + this);
                }
            }
        }
    }

    private void correctImageEntries() {
        List<DbItemTypeDemolitionStep> demolitionAnimationFrames = null;
        CrudListChildServiceHelper<DbItemTypeDemolitionStep> crud = getDemolitionCrud();
        if (crud != null) {
            demolitionAnimationFrames = crud.readDbChildren();
        }

        for (Iterator<DbItemTypeImage> iterator = itemTypeImages.iterator(); iterator.hasNext(); ) {
            DbItemTypeImage itemTypeImage = iterator.next();
            switch (itemTypeImage.getType()) {
                case BUILD_UP:
                    if (itemTypeImage.getStep() >= buildupSteps) {
                        iterator.remove();
                        continue;
                    }
                    if (itemTypeImage.getFrame() >= buildupAnimationFrames) {
                        iterator.remove();
                        continue;
                    }
                    break;
                case RUN_TIME:
                    if (itemTypeImage.getAngelIndex() >= angels.size()) {
                        iterator.remove();
                        continue;
                    }
                    if (itemTypeImage.getFrame() >= runtimeAnimationFrames) {
                        iterator.remove();
                        continue;
                    }
                    break;
                case DEMOLITION:
                    if (itemTypeImage.getAngelIndex() >= angels.size()) {
                        iterator.remove();
                        continue;
                    }
                    if (demolitionAnimationFrames == null || itemTypeImage.getStep() >= demolitionAnimationFrames.size()) {
                        iterator.remove();
                        continue;
                    }
                    if (itemTypeImage.getFrame() >= demolitionAnimationFrames.get(itemTypeImage.getStep()).getAnimationFrames()) {
                        iterator.remove();
                        continue;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown SyncObjectState: " + itemTypeImage.getType() + " itemType: " + this);
            }
        }
    }

    public abstract ItemType createItemType();

    protected void setupItemType(ItemType itemType) {
        itemType.setId(id);
        itemType.setName(getName());
        itemType.setDescription(getDescription());
        BoundingBox boundingBox = createBoundingBox();
        itemType.setBoundingBox(boundingBox);
        itemType.setItemTypeSpriteMap(createItemTypeSpriteMap(boundingBox));
        itemType.setTerrainType(terrainType);
        itemType.setSelectionSound(selectionSound != null ? selectionSound.getId() : null);
        itemType.setBuildupSound(buildupSound != null ? buildupSound.getId() : null);
        itemType.setCommandSound(commandSound != null ? commandSound.getId() : null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbItemType that = (DbItemType) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public void init(UserService userService) {
        itemTypeImages = new HashSet<>();
        angels = new ArrayList<>();
        angels.add(0.0);
        itemTypeDemolitionSteps = new ArrayList<>();
    }

    public List<Double> getAngels() {
        return angels;
    }

    @Override
    public void setParent(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParent() {
        return null;
    }

    public CrudChildServiceHelper<DbItemTypeImage> getItemTypeImageCrud() {
        if (itemTypeImageCrud == null) {
            itemTypeImageCrud = new CrudChildServiceHelper<>(itemTypeImages, DbItemTypeImage.class, this);
        }
        return itemTypeImageCrud;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + name + " id: " + id;
    }
}
