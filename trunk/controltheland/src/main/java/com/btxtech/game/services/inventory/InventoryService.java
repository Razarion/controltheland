package com.btxtech.game.services.inventory;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.dialogs.inventory.InventoryInfo;
import com.btxtech.game.jsre.common.gameengine.services.inventory.CommonInventoryService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */
public interface InventoryService extends CommonInventoryService {
    CrudRootServiceHelper<DbInventoryArtifact> getArtifactCrud();

    CrudRootServiceHelper<DbInventoryItem> getItemCrud();

    CrudRootServiceHelper<DbBoxRegion> getBoxRegionCrud();

    void onSyncBaseItemKilled(SyncBaseItem syncBaseItem);

    void activate();

    void restore();

    void assembleInventoryItem(int inventoryItemId);

    void useInventoryItem(int inventoryItemId, Index position);

    InventoryInfo getInventory();
}
