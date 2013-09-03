package com.btxtech.game.jsre.common.gameengine.services.inventory;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;

/**
 * User: beat
 * Date: 21.05.12
 * Time: 00:39
 */
public interface CommonInventoryService {
    void onSyncBoxItemPicked(SyncBoxItem box, SyncBaseItem picker);
}
