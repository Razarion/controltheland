package com.btxtech.game.jsre.common.gameengine.services.unlock.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.unlock.UnlockService;

/**
 * User: beat
 * Date: 10.02.13
 * Time: 13:36
 */
public abstract class UnlockServiceImpl implements UnlockService {
    @Override
    public boolean isItemLocked(BaseItemType baseItemType, SimpleBase simpleBase) {
        return !isMission()
                && baseItemType.isUnlockNeeded()
                && !getPlanetServices(simpleBase).getBaseService().isBot(simpleBase)
                && !getUnlockContainer(simpleBase).containsItemTypeId(baseItemType.getId());
    }

    protected abstract PlanetServices getPlanetServices(SimpleBase simpleBase);

    protected abstract boolean isMission();
}
