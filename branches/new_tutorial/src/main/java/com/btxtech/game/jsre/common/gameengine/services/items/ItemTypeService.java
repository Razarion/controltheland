package com.btxtech.game.jsre.common.gameengine.services.items;

import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;

import java.util.List;

/**
 * User: beat
 * Date: 26.08.12
 * Time: 19:29
 */
public interface ItemTypeService {
    ItemType getItemType(int itemTypeId) throws NoSuchItemTypeException;

    List<ItemType> getItemTypes();

    boolean areItemTypesLoaded();

    List<BaseItemType> ableToBuild(BaseItemType toBeBuilt);

    int getMaxItemRadius();

    int getMaxItemDiameter();
}
