package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccess;
import com.btxtech.game.services.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 13:40:38
 */
@Component("itemTypeAccess")
public class ItemTypeAccessImpl implements ItemTypeAccess {
    @Autowired
    private ItemService itemService;

    @Override
    public ItemType getItemType(int itemTypeId) throws NoSuchItemTypeException {
        return itemService.getDbItemType(itemTypeId).createItemType();
    }

    @Override
    public void saveItemTypeProperties(int itemTypeId, BoundingBox boundingBox, WeaponType weaponType) throws NoSuchItemTypeException {
        itemService.saveBoundingBox(itemTypeId, boundingBox);
        itemService.saveWeaponType(itemTypeId, weaponType);
    }
}
