package com.btxtech.game.services.unlock.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockContainer;
import com.btxtech.game.jsre.common.gameengine.services.unlock.impl.UnlockServiceImpl;
import com.btxtech.game.jsre.common.packets.UnlockContainerPacket;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.mgmt.impl.DbUserState;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.wicket.pages.mgmt.items.ItemsUtil;
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

    @Override
    protected boolean isMission() {
        return false;
    }

    @Override
    public UnlockContainer getUnlockContainer(SimpleBase simpleBase) {
        return getUnlockContainer(userService.getUserState(simpleBase));
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
                unlockContainers.put(entry.getValue(), new UnlockContainer(ItemsUtil.itemTypesToIntegers(entry.getKey().getUnlockedItemTypes())));
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
            unlockContainers.put(userState, new UnlockContainer(null));
        }
    }

    @Override
    public UnlockContainer unlockItemType(int itemTypeId) throws NoSuchItemTypeException {
        UserState userState = userService.getUserState();
        BaseItemType baseItemType = (BaseItemType) itemTypeService.getItemType(itemTypeId);
        if (!baseItemType.isUnlockNeeded()) {
            throw new IllegalArgumentException("Base item type can not be unlocked: " + baseItemType);
        }
        if (baseItemType.getUnlockRazarion() > userState.getRazarion()) {
            throw new IllegalArgumentException("Not enough razarion to by: " + baseItemType + " user: " + userState);
        }
        UnlockContainer unlockContainer;
        synchronized (unlockContainers) {
            unlockContainer = unlockContainers.get(userState);
            if (unlockContainer == null) {
                throw new IllegalStateException("unlockContainer == null");
            }
            if (unlockContainer.containsItemTypeId(itemTypeId)) {
                throw new IllegalArgumentException("itemTypeId is already unlocked: " + itemTypeId);
            }
            unlockContainer.unlockItemType(itemTypeId);
            userState.subRazarion(baseItemType.getUnlockRazarion());
            historyService.addItemUnlocked(userState, baseItemType);
        }
        return unlockContainer;
    }

    @Override
    public Collection<DbBaseItemType> getUnlockDbBaseItemTypes(UserState userState) {
        Collection<DbBaseItemType> unlockedItems = new ArrayList<>();
        try {
            UnlockContainer unlockContainer = getUnlockContainer(userState);
            for (Integer itemTypeId : unlockContainer.getItemTypes()) {
                unlockedItems.add(serverItemTypeService.getDbBaseItemType(itemTypeId));
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e, "Unable to generate DbBaseItemType collection for: " + userState);
        }
        return unlockedItems;
    }

    @Override
    public void setUnlockedBaseItemTypesBackend(Collection<DbBaseItemType> dbBaseItemTypes, UserState userStates) {
        UnlockContainer unlockContainer = new UnlockContainer(ItemsUtil.itemTypesToIntegers(dbBaseItemTypes));
        unlockContainers.put(userStates, unlockContainer);
        sendPacket(userStates, unlockContainer);
    }

    private void sendPacket(UserState userState, UnlockContainer unlockContainer) {
        ServerPlanetServices serverPlanetServices = planetSystemService.getServerPlanetServices(userState);
        UnlockContainerPacket unlockContainerPacket = new UnlockContainerPacket();
        unlockContainerPacket.setUnlockContainer(unlockContainer);
        serverPlanetServices.getConnectionService().sendPacket(serverPlanetServices.getBaseService().getBase(userState).getSimpleBase(), unlockContainerPacket);
    }

    private UnlockContainer getUnlockContainer(UserState userState) {
        synchronized (unlockContainers) {
            UnlockContainer unlockContainer = unlockContainers.get(userState);
            if (unlockContainer == null) {
                throw new IllegalStateException("unlockContainer == null");
            }
            return unlockContainer;
        }
    }
}
