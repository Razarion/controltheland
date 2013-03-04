package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.inventory.CommonInventoryService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.user.UserState;

import java.util.Collection;

/**
 * User: beat
 * Date: 15.05.12
 * Time: 12:48
 */
public interface InventoryService extends CommonInventoryService {
    void onSyncBaseItemKilled(SyncBaseItem syncBaseItem);

    void useInventoryItem(UserState userState, SimpleBase simpleBase, DbInventoryItem dbInventoryItem, Collection<Index> positionToBePlaced) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException;

    void activate(DbPlanet dbPlanet);

    void deactivate();

    void reactivate(DbPlanet dbPlanet);
}
