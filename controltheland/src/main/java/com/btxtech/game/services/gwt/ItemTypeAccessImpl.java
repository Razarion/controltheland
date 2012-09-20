package com.btxtech.game.services.gwt;

import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemTypeSpriteMap;
import com.btxtech.game.jsre.common.gameengine.itemType.WeaponType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeAccess;
import com.btxtech.game.jsre.itemtypeeditor.ItemTypeImageInfo;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * User: beat
 * Date: 26.08.2011
 * Time: 13:40:38
 */
public class ItemTypeAccessImpl extends AutowiredRemoteServiceServlet implements ItemTypeAccess {
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    private Log log = LogFactory.getLog(ItemTypeAccessImpl.class);

    @Override
    public ItemType getItemType(int itemTypeId) throws NoSuchItemTypeException {
        return serverItemTypeService.getDbItemType(itemTypeId).createItemType();
    }

    @Override
    public void saveItemTypeProperties(int itemTypeId, BoundingBox boundingBox, ItemTypeSpriteMap itemTypeSpriteMap, WeaponType weaponType, Collection<ItemTypeImageInfo> buildupImages, Collection<ItemTypeImageInfo> runtimeImages, Collection<ItemTypeImageInfo> demolitionImages) throws NoSuchItemTypeException {
        try {
            serverItemTypeService.saveItemTypeProperties(itemTypeId, boundingBox, itemTypeSpriteMap, weaponType, buildupImages, runtimeImages, demolitionImages);
        } catch (NoSuchItemTypeException | RuntimeException e) {
            log.error("", e);
            throw e;
        }
    }
}
