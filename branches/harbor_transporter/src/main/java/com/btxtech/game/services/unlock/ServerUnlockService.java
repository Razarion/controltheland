package com.btxtech.game.services.unlock;

import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.unlock.UnlockService;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevelTask;

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

    UnlockContainer unlockQuest(int questId) throws NoSuchItemTypeException;

    UnlockContainer unlockPlanet(int planetId);

    Collection<DbBaseItemType> getUnlockDbBaseItemTypes(UserState userState);

    Collection<DbLevelTask> getUnlockQuests(UserState userState);

    Collection<DbPlanet> getUnlockPlanets(UserState userState);

    void setUnlockedBaseItemTypesBackend(Collection<DbBaseItemType> object, UserState userStates);

    void setUnlockedQuestsBackend(Collection<DbLevelTask> dbLevelTasks, UserState userStates);

    void setUnlockedPlanetsBackend(Collection<DbPlanet> dbPlanets, UserState userState);

    boolean isQuestLocked(QuestInfo questInfo, UserState userState);

    boolean isPlanetLocked(PlanetLiteInfo planetLiteInfo, UserState userState);

    UnlockContainer getUnlockContainer(UserState userState);
}
