package com.btxtech.game.services.unlock.impl;

import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockServiceImpl;
import com.btxtech.game.jsre.common.packets.UnlockContainerPacket;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.common.Utils;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevelTask;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 10.02.13
 * Time: 15:32
 */
@Component("serverUnlockService")
public class ServerUnlockServiceImpl extends UnlockServiceImpl implements ServerUnlockService {
    final private Map<UserState, UnlockContainer> unlockContainers = new HashMap<>();
    @Autowired
    private UserService userService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private ItemTypeService itemTypeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserGuidanceService userGuidanceService;

    @Override
    protected boolean isMission() {
        return false;
    }

    @Override
    public boolean isQuestLocked(QuestInfo questInfo, UserState userState) {
        return isQuestLocked(questInfo, getUnlockContainer(userState));
    }

    @Override
    public boolean isPlanetLocked(PlanetLiteInfo planetLiteInfo, UserState userState) {
        return isPlanetLocked(planetLiteInfo, getUnlockContainer(userState));
    }

    @Override
    public UnlockContainer getUnlockContainer(SimpleBase simpleBase) {
        return getUnlockContainer(userService.getUserState(simpleBase));
    }

    @Override
    public UnlockContainer getUnlockContainer(UserState userState) {
        synchronized (unlockContainers) {
            UnlockContainer unlockContainer = unlockContainers.get(userState);
            if (unlockContainer == null) {
                throw new IllegalStateException("unlockContainer == null");
            }
            return unlockContainer;
        }
    }

    @Override
    protected PlanetServices getPlanetServices(SimpleBase simpleBase) {
        return planetSystemService.getServerPlanetServices(simpleBase);
    }

    @Override
    public void fillAllUnlockContainer(Map<DbUserState, UserState> userStates) {
        synchronized (unlockContainers) {
            unlockContainers.clear();
            for (Map.Entry<DbUserState, UserState> entry : userStates.entrySet()) {
                UnlockContainer unlockContainer = new UnlockContainer();
                unlockContainer.setItemTypes(Utils.dbBaseItemTypesToInts(entry.getKey().getUnlockedItemTypes()));
                unlockContainer.setQuests(Utils.crudChildToInts(entry.getKey().getUnlockedQuests()));
                unlockContainer.setPlanets(Utils.crudChildToInts(entry.getKey().getUnlockedPlanets()));
                unlockContainers.put(entry.getValue(), unlockContainer);
            }
        }
    }

    @Override
    public void onUserStateRemoved(UserState userState) {
        synchronized (unlockContainers) {
            unlockContainers.remove(userState);
        }
    }

    @Override
    public void onUserStateCreated(UserState userState) {
        synchronized (unlockContainers) {
            unlockContainers.put(userState, new UnlockContainer());
        }
    }

    @Override
    public UnlockContainer unlockItemType(int itemTypeId) throws NoSuchItemTypeException {
        UserState userState = userService.getUserState();
        BaseItemType baseItemType = (BaseItemType) itemTypeService.getItemType(itemTypeId);
        if (!baseItemType.isUnlockNeeded()) {
            throw new IllegalArgumentException("Base item type can not be unlocked: " + baseItemType);
        }
        if (baseItemType.getUnlockCrystals() > userState.getCrystals()) {
            throw new IllegalArgumentException("Not enough crystals to unlock: " + baseItemType + " user: " + userState);
        }
        UnlockContainer unlockContainer;
        synchronized (unlockContainers) {
            unlockContainer = unlockContainers.get(userState);
            if (unlockContainer == null) {
                throw new IllegalStateException("unlockContainer == null");
            }
            if (unlockContainer.containsItemTypeId(itemTypeId)) {
                throw new IllegalArgumentException("questId is already unlocked: " + itemTypeId);
            }
            unlockContainer.unlockItemType(itemTypeId);
            userState.subCrystals(baseItemType.getUnlockCrystals());
            historyService.addItemUnlocked(userState, baseItemType);
        }
        return unlockContainer;
    }

    @Override
    public UnlockContainer unlockQuest(int questId) throws NoSuchItemTypeException {
        UserState userState = userService.getUserState();
        DbLevelTask dbLevelTask = userGuidanceService.getDbLevelTask4Id(questId);
        if (!dbLevelTask.isUnlockNeeded()) {
            throw new IllegalArgumentException("Quest can not be unlocked: " + dbLevelTask);
        }
        if (dbLevelTask.getUnlockCrystals() > userState.getCrystals()) {
            throw new IllegalArgumentException("Not enough crystals to unlock: " + dbLevelTask + " user: " + userState);
        }
        UnlockContainer unlockContainer;
        synchronized (unlockContainers) {
            unlockContainer = unlockContainers.get(userState);
            if (unlockContainer == null) {
                throw new IllegalStateException("unlockContainer == null");
            }
            if (unlockContainer.containsQuestId(questId)) {
                throw new IllegalArgumentException("questId is already unlocked: " + questId);
            }
            unlockContainer.unlockQuest(questId);
            userState.subCrystals(dbLevelTask.getUnlockCrystals());
            historyService.addQuestUnlocked(userState, dbLevelTask);
        }
        userGuidanceService.onQuestUnlocked(questId);
        return unlockContainer;
    }

    @Override
    public UnlockContainer unlockPlanet(int planetId) {
        UserState userState = userService.getUserState();
        PlanetLiteInfo planetLiteInfo = planetSystemService.getPlanet(planetId).getPlanetServices().getPlanetInfo().getPlanetLiteInfo();
        if (!planetLiteInfo.isUnlockNeeded()) {
            throw new IllegalArgumentException("Planet can not be unlocked: " + planetLiteInfo);
        }
        if (planetLiteInfo.getUnlockCrystals() > userState.getCrystals()) {
            throw new IllegalArgumentException("Not enough crystals to unlock: " + planetLiteInfo + " user: " + userState);
        }
        UnlockContainer unlockContainer;
        synchronized (unlockContainers) {
            unlockContainer = unlockContainers.get(userState);
            if (unlockContainer == null) {
                throw new IllegalStateException("unlockContainer == null");
            }
            if (unlockContainer.containsPlanetId(planetId)) {
                throw new IllegalArgumentException("planetId is already unlocked: " + planetId);
            }
            unlockContainer.unlockPlanet(planetId);
            userState.subCrystals(planetLiteInfo.getUnlockCrystals());
            historyService.addPlanetUnlocked(userState, planetLiteInfo);
        }
        return unlockContainer;
    }

    @Override
    public Collection<DbBaseItemType> getUnlockDbBaseItemTypes(UserState userState) {
        Collection<DbBaseItemType> unlockedItems = new ArrayList<>();
        UnlockContainer unlockContainer = getUnlockContainer(userState);
        for (Integer itemTypeId : unlockContainer.getItemTypes()) {
            try {
                unlockedItems.add(serverItemTypeService.getDbBaseItemType(itemTypeId));
            } catch (Exception e) {
                ExceptionHandler.handleException(e, "Unable to generate DbBaseItemType collection for: " + userState);
            }
        }
        return unlockedItems;
    }

    @Override
    public Collection<DbLevelTask> getUnlockQuests(UserState userState) {
        Collection<DbLevelTask> unlockedQuests = new ArrayList<>();
        UnlockContainer unlockContainer = getUnlockContainer(userState);
        for (Integer questId : unlockContainer.getQuests()) {
            try {
                unlockedQuests.add(userGuidanceService.getDbLevelTask4Id(questId));
            } catch (Exception e) {
                ExceptionHandler.handleException(e, "Unable to generate DbLevelTask collection for: " + userState);
            }
        }
        return unlockedQuests;
    }

    @Override
    public Collection<DbPlanet> getUnlockPlanets(UserState userState) {
        Collection<DbPlanet> unlockedPlanets = new ArrayList<>();
        UnlockContainer unlockContainer = getUnlockContainer(userState);
        for (Integer planetId : unlockContainer.getPlanets()) {
            try {
                unlockedPlanets.add(planetSystemService.getDbPlanetCrud().readDbChild(planetId));
            } catch (Exception e) {
                ExceptionHandler.handleException(e, "Unable to generate DbPlanet collection for: " + userState);
            }
        }
        return unlockedPlanets;
    }

    @Override
    public void setUnlockedBaseItemTypesBackend(Collection<DbBaseItemType> dbBaseItemTypes, UserState userState) {
        UnlockContainer unlockContainer = getUnlockContainer(userState);
        unlockContainer.setItemTypes(Utils.dbBaseItemTypesToInts(dbBaseItemTypes));
        unlockContainers.put(userState, unlockContainer);
        sendPacket(userState, unlockContainer);
    }

    @Override
    public void setUnlockedQuestsBackend(Collection<DbLevelTask> dbLevelTasks, UserState userState) {
        UnlockContainer unlockContainer = getUnlockContainer(userState);
        unlockContainer.setQuests(Utils.crudChildToInts(dbLevelTasks));
        unlockContainers.put(userState, unlockContainer);
        sendPacket(userState, unlockContainer);
    }

    @Override
    public void setUnlockedPlanetsBackend(Collection<DbPlanet> dbPlanets, UserState userState) {
        UnlockContainer unlockContainer = getUnlockContainer(userState);
        unlockContainer.setPlanets(Utils.crudChildToInts(dbPlanets));
        unlockContainers.put(userState, unlockContainer);
        sendPacket(userState, unlockContainer);
    }

    private void sendPacket(UserState userState, UnlockContainer unlockContainer) {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(userState);
        if (serverPlanetServices.getBaseService().getBase(userState) == null) {
            return;
        }
        UnlockContainerPacket unlockContainerPacket = new UnlockContainerPacket();
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        serverPlanetServices.getConnectionService().sendPacket(serverPlanetServices.getBaseService().getBase(userState).getSimpleBase(), unlockContainerPacket);
    }
}
