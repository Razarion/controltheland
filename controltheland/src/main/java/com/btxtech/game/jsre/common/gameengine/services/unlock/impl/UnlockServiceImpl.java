package com.btxtech.game.jsre.common.gameengine.services.unlock.impl;

import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
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

    @Override
    public boolean isQuestLocked(QuestInfo questInfo, SimpleBase simpleBase) {
        return !isMission() && isQuestLocked(questInfo, getUnlockContainer(simpleBase));
    }

    protected boolean isQuestLocked(QuestInfo questInfo, UnlockContainer unlockContainer) {
        return !isMission() && questInfo.isUnlockNeeded() && !unlockContainer.containsQuestId(questInfo.getId());
    }

    protected boolean isPlanetLocked(PlanetLiteInfo planetLiteInfo, UnlockContainer unlockContainer) {
        return !isMission() && planetLiteInfo.isUnlockNeeded() && !unlockContainer.containsPlanetId(planetLiteInfo.getPlanetId());
    }

    protected abstract PlanetServices getPlanetServices(SimpleBase simpleBase);

    protected abstract boolean isMission();
}
