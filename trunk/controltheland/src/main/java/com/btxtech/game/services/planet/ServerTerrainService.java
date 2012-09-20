package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.common.info.GameInfo;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;

/**
 * User: beat
 * Date: 31.08.12
 * Time: 13:15
 */
public interface ServerTerrainService extends AbstractTerrainService {
    void setupTerrainRealGame(GameInfo gameInfo);
}
