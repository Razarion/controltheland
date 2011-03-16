package com.btxtech.game.services.item.itemType;

import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;

import java.util.Collection;
import java.util.Set;

/**
 * User: beat
 * Date: 16.03.2011
 * Time: 10:40:19
 */
public interface DbItemTypeI {
    Integer getId();

    int getHeight();

    void setHeight(int height);

    int getWidth();

    void setWidth(int width);

    String getName();

    void setDescription(String description);

    String getDescription();

    void setName(String name);

    Set<DbItemTypeImage> getItemTypeImages();

    void setItemTypeImages(Collection<DbItemTypeImage> itemTypeImages);

    String getProDescription();

    void setProDescription(String proDescription);

    String getContraDescription();

    void setContraDescription(String contraDescription);

    TerrainType getTerrainType();

    void setTerrainType(TerrainType terrainType);
}
