package com.btxtech.game.services.unlock;

import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.unlock.UnlockService;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.user.UserState;

import java.util.Collection;
import java.util.Map;

/**
 * User: beat
 * Date: 10.02.13
 * Time: 20:50
 */
public interface ServerUnlockService extends UnlockService {
    void fillAllUnlockContainer(Map<DbUserState, UserState> userStates);

    void onUserStateRemoved(UserState userStates);

    void onUserStateCreated(UserState userState);

    UnlockContainer unlockItemType(int itemTypeId) throws NoSuchItemTypeException;

    Collection<DbBaseItemType> getUnlockDbBaseItemTypes(UserState userState);

    void setUnlockedBaseItemTypesBackend(Collection<DbBaseItemType> object, UserState userStates);
}
