package com.btxtech.game.services.inventory;

import com.btxtech.game.services.common.CrudRootServiceHelper;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */
public interface InventoryService {
    CrudRootServiceHelper<DbInventoryArtifact> getArtifactCrud();

    CrudRootServiceHelper<DbInventoryItem> getItemCrud();
}
