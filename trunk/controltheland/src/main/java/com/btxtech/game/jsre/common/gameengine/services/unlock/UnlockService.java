package com.btxtech.game.jsre.common.gameengine.services.unlock;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;

/**
 * User: beat
 * Date: 10.02.13
 * Time: 13:02
 */
public interface UnlockService {
    boolean isItemLocked(BaseItemType baseItemType, SimpleBase simpleBase);

    UnlockContainer getUnlockContainer(SimpleBase simpleBase);
}
