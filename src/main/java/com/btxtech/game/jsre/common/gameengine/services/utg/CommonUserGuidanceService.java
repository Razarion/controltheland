package com.btxtech.game.jsre.common.gameengine.services.utg;

import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.common.SimpleBase;

/**
 * User: beat
 * Date: 11.01.2012
 * Time: 17:46:43
 */
public interface CommonUserGuidanceService {
    LevelScope getLevelScope();

    LevelScope getLevelScope(SimpleBase simpleBase);
}
