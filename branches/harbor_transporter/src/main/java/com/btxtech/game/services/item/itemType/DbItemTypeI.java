package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.common.db.DbI18nString;

/**
 * User: beat
 * Date: 16.03.2011
 * Time: 10:40:19
 */
public interface DbItemTypeI {
    Integer getId();

    String getName();

    DbI18nString getDbI18nName();

    DbI18nString getDbI18nDescription();

    void setName(String name);

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

    int getBoundingBoxRadius();

    void setBoundingBoxRadius(int boundingBoxRadius);
}
