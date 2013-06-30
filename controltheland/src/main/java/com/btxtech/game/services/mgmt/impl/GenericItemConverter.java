/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.mgmt.impl;

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncProjectileItem;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.jsre.common.packets.SyncItemInfo;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.inventory.DbInventoryArtifact;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: Sep 23, 2009
 * Time: 12:06:40 PM
 */
@Component("mgmtServiceGenericItemConverter")
public class GenericItemConverter {
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private ServerGlobalServices serverGlobalServices;
    @Autowired
    private ServerUnlockService serverUnlockService;
    private DbBackupEntry dbBackupEntry;
    private HashMap<Id, GenericItem> genericItems = new HashMap<>();
    private HashMap<Id, SyncBaseObject> syncBaseObject = new HashMap<>();
    private HashMap<Integer, DbItemType> dbItemTypeCache = new HashMap<>();
    private HashMap<Base, DbBase> bases = new HashMap<>();
    private Log log = LogFactory.getLog(GenericItemConverter.class);

    public void clear() {
        dbBackupEntry = null;
        genericItems.clear();
        syncBaseObject.clear();
        dbItemTypeCache.clear();
        bases.clear();
    }

    public DbBackupEntry generateBackupEntry() {
        dbBackupEntry = new DbBackupEntry();
        fillHelperCache();
        dbBackupEntry.setTimeStamp(new Date());


        for (Planet planet : planetSystemService.getRunningPlanets()) {
            Collection<SyncItem> syncItems = planet.getPlanetServices().getItemService().getItems4Backup();
            for (SyncItem item : syncItems) {
                addGenericItem(item);
            }
            // post process references
            for (SyncItem item : syncItems) {
                postProcessBackup(planet.getPlanetServices(), item);
            }
        }

        dbBackupEntry.setItems(new HashSet<>(genericItems.values()));

        // User state
        Set<DbUserState> dbUserStates = new HashSet<>();
        for (UserState userState : userService.getAllUserStates()) {
            if (!userState.isRegistered()) {
                continue;
            }
            try {
                DbUserState dbUserState = createDbUserState(userState);
                userGuidanceService.createAndAddBackup(dbUserState, userState);
                statisticsService.createAndAddBackup(dbUserState, userState);
                dbUserStates.add(dbUserState);
            } catch (Exception e) {
                log.error("Can not back user: " + userState, e);
            }
        }

        dbBackupEntry.setUserStates(dbUserStates);
        return dbBackupEntry;
    }

    private DbUserState createDbUserState(UserState userState) {
        Collection<DbInventoryItem> inventoryItems = new ArrayList<>();
        for (Integer inventoryItemId : userState.getInventoryItemIds()) {
            inventoryItems.add(globalInventoryService.getItemCrud().readDbChild(inventoryItemId));
        }
        Collection<DbInventoryArtifact> inventoryArtifacts = new ArrayList<>();
        for (Integer inventoryArtifactId : userState.getInventoryArtifactIds()) {
            inventoryArtifacts.add(globalInventoryService.getArtifactCrud().readDbChild(inventoryArtifactId));
        }

        DbUserState dbUserState = new DbUserState(dbBackupEntry,
                userService.getUser(userState.getUser()),
                userState,
                userGuidanceService.getDbLevel(userState),
                inventoryItems,
                inventoryArtifacts,
                serverUnlockService.getUnlockDbBaseItemTypes(userState),
                serverUnlockService.getUnlockQuests(userState),
                serverUnlockService.getUnlockPlanets(userState));
        dbUserState.setStorablePackets(createDbStorablePackets(dbUserState, userState));
        if (userState.getBase() != null) {
            DbBase dbBase = bases.get(userState.getBase());
            if (dbBase != null) {
                dbBase.setUserState(dbUserState);
                dbUserState.setBase(dbBase);
            } else {
                log.error("dbBase == null for base: " + userState.getBase() + " UserState: " + userState);
            }
        }
        return dbUserState;
    }

    private Collection<DbStorablePacket> createDbStorablePackets(DbUserState dbUserState, UserState userState) {
        Collection<DbStorablePacket> dbStorablePackets = new ArrayList<>();
        for (StorablePacket storablePacket : userState.getStorablePackets()) {
            dbStorablePackets.add(new DbStorablePacket(dbUserState, storablePacket));
        }
        return dbStorablePackets;
    }

    public void restoreBackup(DbBackupEntry dbBackupEntry) throws NoSuchItemTypeException {
        planetSystemService.beforeRestore();

        Map<DbUserState, UserState> userStates = new HashMap<>();
        for (DbUserState dbUserState : dbBackupEntry.getUserStates()) {
            UserState userState = dbUserState.createUserState(userService);
            if (userState != null) {
                userStates.put(dbUserState, userState);
            }
        }

        Collection<GenericItem> genericItems = dbBackupEntry.getItems();
        Collection<UserState> userStateAbandoned = new ArrayList<>();
        Map<DbBase, Base> dbBases = new HashMap<>();
        for (GenericItem genericItem : genericItems) {
            try {
                if (genericItem instanceof DbGenericBaseItem) {
                    DbBase dBbase = ((DbGenericBaseItem) genericItem).getBase();
                    Base base = getBase(userStates, dbBases, dBbase);
                    SyncBaseItem syncItem = addSyncBaseItem(base.getPlanet().getPlanetServices(), (DbGenericBaseItem) genericItem, base);
                    base.addItem(syncItem);
                } else if (genericItem instanceof GenericProjectileItem) {
                    DbBase dBbase = ((GenericProjectileItem) genericItem).getBase();
                    Base base = getBase(userStates, dbBases, dBbase);
                    addSyncItem(base.getPlanet().getPlanetServices(), (GenericProjectileItem) genericItem, base);
                } else {
                    log.error("restoreBackup: unknown type: " + genericItem);
                }
            } catch (Throwable t) {
                log.error("Error restoring GenericItem: " + genericItem.getItemId(), t);
            }
        }
        dbBackupEntry.getUserStates().removeAll(userStateAbandoned);
        // post process
        for (Iterator<GenericItem> iterator = genericItems.iterator(); iterator.hasNext(); ) {
            GenericItem genericItem = iterator.next();
            try {
                if (genericItem instanceof DbGenericBaseItem) {
                    postProcessRestore((DbGenericBaseItem) genericItem);
                }
            } catch (Throwable t) {
                log.error("Error post process restore GenericItem: " + genericItem.getItemId(), t);
                iterator.remove();
            }
        }

        userService.restore(userStates.values());
        serverUnlockService.fillAllUnlockContainer(userStates);
        userGuidanceService.restoreBackup(userStates);
        statisticsService.restoreBackup(userStates);
        planetSystemService.restore(dbBases.values(), syncBaseObject.values());
        planetSystemService.afterRestore();
    }

    private Base getBase(Map<DbUserState, UserState> userStates, Map<DbBase, Base> dbBases, DbBase dBbase) {
        Base base = dbBases.get(dBbase);
        if (base == null) {
            UserState userState = userStates.get(dBbase.getUserState());
            base = dBbase.createBase(userState, planetSystemService.getPlanet(dBbase.getDbPlanet()));
            if (userState != null) {
                userState.setBase(base);
            }
            dbBases.put(dBbase, base);
        }
        return base;
    }

    private void fillHelperCache() {
        for (DbItemType dbItemType : serverItemTypeService.getDbItemTypes()) {
            dbItemTypeCache.put(dbItemType.getId(), dbItemType);
        }
    }

    private void addGenericItem(SyncItem item) {
        if (item instanceof SyncBaseItem) {
            addGenericBaseItem((SyncBaseItem) item);
        } else if (item instanceof SyncProjectileItem) {
            addGenericProjectileItem((SyncProjectileItem) item);
        } else {
            throw new IllegalArgumentException("Unknown SyncItem: " + item);
        }
    }

    private void addGenericProjectileItem(SyncProjectileItem item) {
        if (!item.isAlive()) {
            return;
        }
        GenericProjectileItem genericProjectileItem = new GenericProjectileItem(dbBackupEntry);
        fillGenericItem(item, genericProjectileItem);
        genericProjectileItem.setBase(getDbBase(item.getBase()));
        genericProjectileItem.setTargetPosition(item.getTarget());
        genericItems.put(item.getId(), genericProjectileItem);
    }

    private void addGenericBaseItem(SyncBaseItem item) {
        DbGenericBaseItem genericItem = new DbGenericBaseItem(dbBackupEntry);

        fillGenericItem(item, genericItem);

        genericItem.setHealth((int) item.getHealth());
        genericItem.setBuildup(item.getBuildup());
        DbBase dbBase = getDbBase(item.getBase());
        genericItem.setBase(dbBase);
        genericItem.setUpgrading(item.isUpgrading());
        if (item.getUpgradingItemType() != null) {
            genericItem.setUpgradingItemType((DbBaseItemType) dbItemTypeCache.get(item.getUpgradingItemType().getId()));
        }
        genericItem.setUpgradeProgress(item.getUpgradeProgress());
        if (item.hasSyncMovable()) {
            genericItem.setPathToAbsoluteDestination(item.getSyncMovable().getPathToDestination());
            genericItem.setDestinationAngel(item.getSyncMovable().getDestinationAngel());
        }
        if (item.getItemType().getBoundingBox().isTurnable()) {
            genericItem.setAngel(item.getSyncItemArea().getAngel());
        }
        if (item.hasSyncBuilder()) {
            genericItem.setPositionToBeBuilt(item.getSyncBuilder().getToBeBuildPosition());
            if (item.getSyncBuilder().getToBeBuiltType() != null) {
                genericItem.setToBeBuilt((DbBaseItemType) dbItemTypeCache.get(item.getSyncBuilder().getToBeBuiltType().getId()));
            }
        }
        if (item.hasSyncFactory()) {
            if (item.getSyncFactory().getToBeBuiltType() != null) {
                genericItem.setToBeBuilt((DbBaseItemType) dbItemTypeCache.get(item.getSyncFactory().getToBeBuiltType().getId()));
            }
            genericItem.setBuildupProgress((int) item.getSyncFactory().getBuildupProgress());
            genericItem.setRallyPoint(item.getSyncFactory().getRallyPoint());
        }
        if (item.hasSyncWeapon()) {
            genericItem.setFollowTarget(item.getSyncWeapon().isFollowTarget());
            genericItem.setReloadProgress(item.getSyncWeapon().getReloadProgress());
        }
        if (item.hasSyncItemContainer()) {
            genericItem.setUnloadPos(item.getSyncItemContainer().getUnloadPos());
        }
        if (item.hasSyncLauncher()) {
            genericItem.setLauncherBuildup(item.getSyncLauncher().getBuildup());
        }
        genericItems.put(item.getId(), genericItem);

    }

    private DbBase getDbBase(SimpleBase simpleBase) {
        Base base = planetSystemService.getServerPlanetServices(simpleBase).getBaseService().getBase(simpleBase);
        if (base == null) {
            throw new IllegalStateException("No base for " + simpleBase);
        }

        DbBase dbBase = bases.get(base);
        if (dbBase == null) {
            dbBase = new DbBase(base);
            dbBase.setDbPlanet(planetSystemService.getDbPlanetCrud().readDbChild(base.getPlanet().getPlanetServices().getPlanetInfo().getPlanetId()));
            bases.put(base, dbBase);
        }
        return dbBase;
    }

    private void fillGenericItem(SyncItem item, GenericItem genericItem) {
        SyncItemInfo syncInfo = item.getSyncInfo();
        genericItem.setItemId(syncInfo.getId());
        genericItem.setPosition(syncInfo.getPosition());
        genericItem.setDbItemType(dbItemTypeCache.get(syncInfo.getItemTypeId()));
    }

    private void postProcessBackup(ServerPlanetServices serverPlanetServices, SyncItem syncItem) {
        GenericItem genericItem = genericItems.get(syncItem.getId());
        if (!(genericItem instanceof DbGenericBaseItem)) {
            return;
        }
        DbGenericBaseItem dbGenericBaseItem = (DbGenericBaseItem) genericItem;
        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
        if (syncBaseItem.getContainedIn() != null) {
            DbGenericBaseItem container = (DbGenericBaseItem) genericItems.get(syncBaseItem.getContainedIn());
            if (container == null) {
                log.error("BACKUP: No container found for: " + syncBaseItem + " container: " + syncBaseItem.getContainedIn());
            }
            dbGenericBaseItem.setContainedIn(container);
        }
        if (syncBaseItem.hasSyncMovable()) {
            if (syncBaseItem.getSyncMovable().getTargetContainer() != null) {
                DbGenericBaseItem container = (DbGenericBaseItem) genericItems.get(syncBaseItem.getSyncMovable().getTargetContainer());
                if (container == null) {
                    log.error("BACKUP: No  target container found for: " + syncBaseItem + " taget container: " + syncBaseItem.getSyncMovable().getTargetContainer());
                }
                dbGenericBaseItem.setTargetContainer(container);
            }
        }
        if (syncBaseItem.hasSyncWeapon()) {
            if (syncBaseItem.getSyncWeapon().getTarget() != null) {
                try {
                    SimpleBase simpleBase = ((SyncBaseItem) serverPlanetServices.getItemService().getItem(syncBaseItem.getSyncWeapon().getTarget())).getBase();
                    if (!planetSystemService.getServerPlanetServices(simpleBase).getBaseService().isBot(simpleBase)) {
                        DbGenericBaseItem target = (DbGenericBaseItem) genericItems.get(syncBaseItem.getSyncWeapon().getTarget());
                        if (target == null) {
                            log.error("BACKUP: No generic syncBaseItem for: " + syncBaseItem.getSyncWeapon().getTarget());
                        }
                        dbGenericBaseItem.setBaseTarget(target);
                    }
                } catch (ItemDoesNotExistException e) {
                    log.error("", e);
                }
            }
        }
        if (syncBaseItem.hasSyncBuilder()) {
            if (syncBaseItem.getSyncBuilder().getCurrentBuildup() != null) {
                DbGenericBaseItem target = (DbGenericBaseItem) genericItems.get(syncBaseItem.getSyncBuilder().getCurrentBuildup().getId());
                if (target == null) {
                    log.error("BACKUP: No generic syncBaseItem for: " + syncBaseItem.getSyncBuilder().getCurrentBuildup());
                }
                dbGenericBaseItem.setBaseTarget(target);
            }
        }
    }

    private SyncBaseItem addSyncBaseItem(ServerPlanetServices serverPlanetServices, DbGenericBaseItem genericItem, Base base) throws NoSuchItemTypeException {
        SyncBaseItem item = (SyncBaseItem) serverPlanetServices.getItemService().newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getDbItemTyp().getId(), base.getSimpleBase(), serverGlobalServices, serverPlanetServices);

        item.setHealth(genericItem.getHealth());
        item.setBuildup(genericItem.getBuildup());
        item.setUpgrading(genericItem.isUpgrading());
        item.setUpgradeProgress(genericItem.getUpgradeProgress());
        if (genericItem.getContainedIn() != null) {
            item.setContained(genericItem.getContainedIn().getItemId());
        }
        if (genericItem.getUpgradingItemType() != null) {
            item.setUpgradingItemType((BaseItemType) genericItem.getUpgradingItemType().createItemType());
        }
        if (item.hasSyncMovable()) {
            item.getSyncMovable().setPathToDestination(genericItem.getPathToAbsoluteDestination(), genericItem.getDestinationAngel());
        }
        if (item.getItemType().getBoundingBox().isTurnable()) {
            item.getSyncItemArea().setAngel(genericItem.getAngel());
        }
        if (item.hasSyncBuilder()) {
            item.getSyncBuilder().setToBeBuildPosition(genericItem.getPositionToBeBuilt());
            if (genericItem.getToBeBuilt() != null) {
                item.getSyncBuilder().setToBeBuiltType((BaseItemType) genericItem.getToBeBuilt().createItemType());
            }
        }
        if (item.hasSyncFactory()) {
            if (genericItem.getToBeBuilt() != null) {
                item.getSyncFactory().setToBeBuiltType((BaseItemType) genericItem.getToBeBuilt().createItemType());
            }
            item.getSyncFactory().setBuildupProgress(genericItem.getBuildupProgress());
            item.getSyncFactory().setRallyPoint(genericItem.getRallyPoint());
        }
        if (item.hasSyncWeapon()) {
            item.getSyncWeapon().setFollowTarget(genericItem.isFollowTarget());
            item.getSyncWeapon().setReloadProgress(genericItem.getReloadProgress());

            if (genericItem.getBaseTarget() != null) {
                item.getSyncWeapon().setTarget(genericItem.getBaseTarget().getItemId());
            }
        }
        if (item.hasSyncItemContainer()) {
            item.getSyncItemContainer().setUnloadPos(genericItem.getUnloadPos());
        }
        if (item.hasSyncLauncher()) {
            item.getSyncLauncher().setBuildup(genericItem.getBuildup());
        }
        syncBaseObject.put(genericItem.getItemId(), item);
        return item;
    }

    private void addSyncItem(ServerPlanetServices serverPlanetServices, GenericProjectileItem genericItem, Base base) throws NoSuchItemTypeException {
        SyncProjectileItem item = (SyncProjectileItem) serverPlanetServices.getItemService().newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getDbItemTyp().getId(), base.getSimpleBase(), serverGlobalServices, serverPlanetServices);
        item.setTarget(genericItem.getTargetPosition());
        syncBaseObject.put(genericItem.getItemId(), item);
    }

    private void postProcessRestore(DbGenericBaseItem genericItem) {
        SyncBaseItem syncItem = (SyncBaseItem) syncBaseObject.get(genericItem.getItemId());
        if (syncItem == null) {
            log.error("Can not restore DbGenericBaseItem: " + genericItem.getItemId());
            return;
        }
        if (syncItem.hasSyncMovable()) {
            if (genericItem.getTargetContainer() != null) {
                syncItem.getSyncMovable().setTargetContainer(genericItem.getTargetContainer().getItemId());
            }
        }
        if (syncItem.hasSyncWeapon()) {
            if (genericItem.getBaseTarget() != null) {
                SyncItem target = (SyncItem) syncBaseObject.get(genericItem.getBaseTarget().getItemId());
                if (target == null) {
                    throw new IllegalStateException("Weapon, no sync item for: " + genericItem.getBaseTarget());
                }
            }
        }

        if (syncItem.hasSyncBuilder()) {
            if (genericItem.getBaseTarget() != null) {
                SyncBaseItem target = (SyncBaseItem) syncBaseObject.get(genericItem.getBaseTarget().getItemId());
                if (target == null) {
                    throw new IllegalStateException("Builder, no sync item for: " + genericItem.getBaseTarget());
                }
                syncItem.getSyncBuilder().setCurrentBuildup(target);
            }
        }

        if (syncItem.hasSyncItemContainer()) {
            if (genericItem.getContainedItems() != null && !genericItem.getContainedItems().isEmpty()) {
                ArrayList<Id> containedItems = new ArrayList<>();
                for (DbGenericBaseItem dbGenericBaseItem : genericItem.getContainedItems()) {
                    SyncBaseItem child = (SyncBaseItem) syncBaseObject.get(dbGenericBaseItem.getItemId());
                    if (child != null) {
                        containedItems.add(child.getId());
                    } else {
                        log.error("Can not add BaseSyncItem " + dbGenericBaseItem.getItemId() + " to container " + syncItem);
                    }
                }
                syncItem.getSyncItemContainer().setContainedItems(containedItems);
            }
        }

        if (!syncItem.getSyncItemArea().hasPosition() && !syncItem.isContainedIn()) {
            throw new IllegalStateException("SyncBaseItem has no position but is not contained in: " + syncItem);
        }

    }
}

