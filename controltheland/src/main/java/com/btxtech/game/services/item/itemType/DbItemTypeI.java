package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;

/**
 * User: beat
 * Date: 16.03.2011
 * Time: 10:40:19
 */
public interface DbItemTypeI {
    Integer getId();

    String getName();

    void setDescription(String description);

    String getDescription();

    void setName(String name);

    String getProDescription();

    void setProDescription(String proDescription);

    String getContraDescription();

    void setContraDescription(String contraDescription);

    TerrainType getTerrainType();

    void setTerrainType(TerrainType terrainType);

    int getImageWidth();

    int getImageHeight();

    int getBuildupSteps();

    void setBuildupSteps(int buildupSteps);

    int getBuildupAnimationFrames();

    void setBuildupAnimationFrames(int buildupAnimationFrames);

    int getBuildupAnimationDuration();

    void setBuildupAnimationDuration(int buildupAnimationDuration);

    int getRuntimeAnimationFrames();

    void setRuntimeAnimationFrames(int runtimeAnimationFrames);

    int getRuntimeAnimationDuration();

    void setRuntimeAnimationDuration(int runtimeAnimationDuration);

    int getDemolitionSteps();

    void setDemolitionSteps(int demolitionSteps);

    int getDemolitionAnimationFrames();

    void setDemolitionAnimationFrames(int demolitionAnimationFrames);

    int getDemolitionAnimationDuration();

    void setDemolitionAnimationDuration(int demolitionAnimationDuration);

    int getBoundingBoxRadius();

    void setBoundingBoxRadius(int boundingBoxRadius);
}
