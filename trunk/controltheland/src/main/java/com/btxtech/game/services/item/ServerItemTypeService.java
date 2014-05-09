package com.btxtech.game.services.item;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemClipPosition;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbBoxItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.item.itemType.DbItemTypeImage;
import com.btxtech.game.services.item.itemType.DbResourceItemType;

import java.util.Collection;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 23:35
 */
public interface ServerItemTypeService extends ItemTypeService {
    void saveDbItemTypes(Collection<DbItemType> itemTypes);

    void saveAttackMatrix(Collection<DbBaseItemType> dbBaseItemTypes);

    void saveDbItemType(DbItemType dBItemType);

    Collection<DbItemType> getDbItemTypes();

    Collection<DbBaseItemType> getDbBaseItemTypes();

    Collection<DbBaseItemType> getWeaponDbBaseItemTypes();

    void activate();

    DbItemType getDbItemType(int itemTypeId);

    DbBaseItemType getDbBaseItemType(int itemBaseTypeId);

    DbResourceItemType getDbResourceItemType(int resourceItemType);

    DbBoxItemType getDbBoxItemType(int boxItemType);

    void deleteItemType(DbItemType dbItemType);

    DbItemTypeImage getCmsDbItemTypeImage(int itemTypeId) throws NoSuchItemTypeException;

    ImageHolder getItemTypeSpriteMap(int itemTypeId);

    ItemType getItemType(DbItemType dbItemType);

    CrudRootServiceHelper<DbItemType> getDbItemTypeCrud();

     void saveItemTypeProperties(int itemTypeId,
                                 BoundingBox boundingBox,
                                 ItemTypeSpriteMap itemTypeSpriteMap,
                                 WeaponType weaponType,
                                 Collection<ItemTypeImageInfo> buildupImages,
                                 Collection<ItemTypeImageInfo> runtimeImages,
                                 Collection<ItemTypeImageInfo> demolitionImages,
                                 ItemClipPosition harvesterItemClipPosition,
                                 ItemClipPosition buildupItemClipPosition) throws NoSuchItemTypeException;
}
