package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.inventory.impl.DbInventoryNewUser;
import com.btxtech.game.services.user.UserState;

import java.util.Collection;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */
public interface GlobalInventoryService {
    CrudRootServiceHelper<DbInventoryArtifact> getArtifactCrud();

    CrudRootServiceHelper<DbInventoryItem> getItemCrud();

    CrudRootServiceHelper<DbInventoryNewUser> getNewUserCrud();

    void assembleInventoryItem(int inventoryItemId);

    void buyInventoryItem(int inventoryItemId);

    void buyInventoryArtifact(int inventoryArtifactId);

    InventoryInfo getInventory(Integer filterPlanetId, boolean filterLevel);

    void setupNewUserState(UserState userState);

    void onSyncBoxItemPicked(SyncBoxItem box, SyncBaseItem picker);

    BoxItemType getDropBox4ItemType(BaseItemType baseItemType);

    void useInventoryItem(int inventoryItemId, Collection<Index> positionToBePlaced) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException;
}
