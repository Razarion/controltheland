package com.btxtech.game.services.overall.helpers;

import com.btxtech.game.jsre.client.common.GameInfo;

/**
 * User: beat
 * Date: 28.10.2009
 * Time: 12:03:16
 */
public class BaseHelper {
    private GameInfo gameInfo;
    private ConstructionVehicleSyncInfo constructionVehicleSyncInfo;

    BaseHelper(GameInfo gameInfo, ConstructionVehicleSyncInfo constructionVehicleSyncInfo) {
        this.gameInfo = gameInfo;
        this.constructionVehicleSyncInfo = constructionVehicleSyncInfo;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public ConstructionVehicleSyncInfo getConstructionVehicleSyncInfo() {
        return constructionVehicleSyncInfo;
    }
}


