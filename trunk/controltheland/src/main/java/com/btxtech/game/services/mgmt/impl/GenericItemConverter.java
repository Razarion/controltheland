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
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncProjectileItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.condition.ServerConditionService;
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
    private BaseService baseService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private Services services;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private BotService botService;
    private BackupEntry backupEntry;
    private HashMap<Id, GenericItem> genericItems = new HashMap<Id, GenericItem>();
    private HashMap<Id, SyncItem> syncItems = new HashMap<Id, SyncItem>();
    private HashMap<Integer, DbItemType> dbItemTypeCache = new HashMap<Integer, DbItemType>();
    private HashMap<Base, DbBase> bases = new HashMap<Base, DbBase>();
    private Log log = LogFactory.getLog(GenericItemConverter.class);

    public void clear() {
        backupEntry = null;
        genericItems.clear();
        syncItems.clear();
        dbItemTypeCache.clear();
        bases.clear();
    }

    public BackupEntry generateBackupEntry() {
        backupEntry = new BackupEntry();
        fillHelperCache();
        Collection<SyncItem> syncItems = itemService.getItemsCopyNoBot();
        backupEntry.setTimeStamp(new Date());

        for (SyncItem item : syncItems) {
            addGenericItem(item);
        }
        // post process references
        for (SyncItem item : syncItems) {
            postProcessBackup(item);
        }
        backupEntry.setItems(new HashSet<GenericItem>(genericItems.values()));

        // User state
        Set<DbUserState> dbUserStates = new HashSet<DbUserState>();
        for (UserState userState : userService.getAllUserStates()) {
            if (!userState.isRegistered()) {
                continue;
            }
            try {
                DbUserState dbUserState = createDbUserState(userState);
                dbUserState.setDbAbstractComparisonBackup(serverConditionService.createBackup(dbUserState, userState));
                dbUserStates.add(dbUserState);
            } catch (Exception e) {
                log.error("Can not back user: " + userState, e);
            }
        }

        backupEntry.setUserStates(dbUserStates);
        return backupEntry;
    }

    private DbUserState createDbUserState(UserState userState) {
        DbUserState dbUserState = new DbUserState(backupEntry, userState);
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

    public void restoreBackup(BackupEntry backupEntry) throws NoSuchItemTypeException {
        botService.cleanup();
        actionService.pause(true);
        serverEnergyService.pauseService(true);

        Map<DbUserState, UserState> userStates = new HashMap<DbUserState, UserState>();
        for (DbUserState dbUserState : backupEntry.getUserStates()) {
            UserState userState = dbUserState.createUserState(userGuidanceService);
            userStates.put(dbUserState, userState);
        }

        Collection<GenericItem> genericItems = backupEntry.getItems();
        Collection<UserState> userStateAbandoned = new ArrayList<UserState>();
        Map<DbBase, Base> dbBases = new HashMap<DbBase, Base>();
        for (GenericItem genericItem : genericItems) {
            try {
                if (genericItem instanceof GenericBaseItem) {
                    DbBase dBbase = ((GenericBaseItem) genericItem).getBase();
                    Base base = getBase(userStates, dbBases, dBbase);
                    SyncBaseItem syncItem = addSyncBaseItem((GenericBaseItem) genericItem, base);
                    base.addItem(syncItem);
                } else if (genericItem instanceof GenericResourceItem) {
                    addSyncItem((GenericResourceItem) genericItem);
                } else if (genericItem instanceof GenericProjectileItem) {
                    DbBase dBbase = ((GenericProjectileItem) genericItem).getBase();
                    Base base = getBase(userStates, dbBases, dBbase);
                    addSyncItem((GenericProjectileItem) genericItem, base);
                } else {
                    log.error("restoreBackup: unknown type: " + genericItem);
                }
            } catch (Throwable t) {
                log.error("Error restoring GenericItem: " + genericItem.getItemId(), t);
            }
        }
        backupEntry.getUserStates().removeAll(userStateAbandoned);
        // post process
        for (Iterator<GenericItem> iterator = genericItems.iterator(); iterator.hasNext();) {
            GenericItem genericItem = iterator.next();
            try {
                if (genericItem instanceof GenericBaseItem) {
                    postProcessRestore((GenericBaseItem) genericItem);
                }
            } catch (Throwable t) {
                log.error("Error post process restore GenericItem: " + genericItem.getItemId(), t);
                iterator.remove();
            }
        }

        userService.restore(userStates.values());
        baseService.restoreBases(dbBases.values());
        itemService.restoreItems(syncItems.values());
        serverConditionService.restoreBackup(userStates, itemService);
        serverEnergyService.pauseService(false);
        serverEnergyService.restoreItems(syncItems.values());
        actionService.pause(false);
        botService.activate();
    }

    private Base getBase(Map<DbUserState, UserState> userStates, Map<DbBase, Base> dbBases, DbBase dBbase) {
        Base base = dbBases.get(dBbase);
        if (base == null) {
            UserState userState = userStates.get(dBbase.getUserState());
            base = dBbase.createBase(userState);
            if (userState != null) {
                userState.setBase(base);
            }
            dbBases.put(dBbase, base);
        }
        return base;
    }

    private void fillHelperCache() {
        for (DbItemType dbItemType : itemService.getDbItemTypes()) {
            dbItemTypeCache.put(dbItemType.getId(), dbItemType);
        }
    }

    private void addGenericItem(SyncItem item) {
        if (item instanceof SyncBaseItem) {
            addGenericBaseItem((SyncBaseItem) item);
        } else if (item instanceof SyncResourceItem) {
            addGenericResourceItem((SyncResourceItem) item);
        } else if (item instanceof SyncProjectileItem) {
            addGenericProjectileItem((SyncProjectileItem) item);
        } else {
            throw new IllegalArgumentException("Unknown SyncItem: " + item);
        }
    }

    private void addGenericResourceItem(SyncResourceItem item) {
        GenericResourceItem genericItem = new GenericResourceItem(backupEntry);
        fillGenericItem(item, genericItem);
        genericItem.setAmount(item.getAmount());
        genericItems.put(item.getId(), genericItem);
    }

    private void addGenericProjectileItem(SyncProjectileItem item) {
        if (!item.isAlive()) {
            return;
        }
        GenericProjectileItem genericProjectileItem = new GenericProjectileItem(backupEntry);
        fillGenericItem(item, genericProjectileItem);
        genericProjectileItem.setBase(getDbBase(item.getBase()));
        genericProjectileItem.setTargetPosition(item.getTarget());
        genericItems.put(item.getId(), genericProjectileItem);
    }

    private void addGenericBaseItem(SyncBaseItem item) {
        GenericBaseItem genericItem = new GenericBaseItem(backupEntry);

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
        }
        if (item.getItemType().getBoundingBox().isTurnable()) {
            genericItem.setAngel(item.getSyncItemArea().getAngel());
        }
        if (item.hasSyncBuilder()) {
            genericItem.setPositionToBeBuilt(item.getSyncBuilder().getToBeBuildPosition());
            if (item.getSyncBuilder().getToBeBuiltType() != null) {
                genericItem.setToBeBuilt((DbBaseItemType) dbItemTypeCache.get(item.getSyncBuilder().getToBeBuiltType().getId()));
            }
            genericItem.setCreatedChildCount(item.getSyncBuilder().getCreatedChildCount());
        }
        if (item.hasSyncFactory()) {
            if (item.getSyncFactory().getToBeBuiltType() != null) {
                genericItem.setToBeBuilt((DbBaseItemType) dbItemTypeCache.get(item.getSyncFactory().getToBeBuiltType().getId()));
            }
            genericItem.setBuildupProgress((int) item.getSyncFactory().getBuildupProgress());
            genericItem.setCreatedChildCount(item.getSyncFactory().getCreatedChildCount());
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
        Base base = baseService.getBase(simpleBase);
        if (base == null) {
            throw new IllegalStateException("No base for " + simpleBase);
        }

        DbBase dbBase = bases.get(base);
        if (dbBase == null) {
            dbBase = new DbBase(base);
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

    private void postProcessBackup(SyncItem syncItem) {
        GenericItem genericItem = genericItems.get(syncItem.getId());
        if (!(genericItem instanceof GenericBaseItem)) {
            return;
        }
        GenericBaseItem genericBaseItem = (GenericBaseItem) genericItem;
        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
        if (syncBaseItem.getContainedIn() != null) {
            GenericBaseItem container = (GenericBaseItem) genericItems.get(syncBaseItem.getContainedIn());
            if (container == null) {
                log.error("BACKUP: No container found for: " + syncBaseItem + " container: " + syncBaseItem.getContainedIn());
            }
            genericBaseItem.setContainedIn(container);
        }
        if (syncBaseItem.hasSyncMovable()) {
            if (syncBaseItem.getSyncMovable().getTargetContainer() != null) {
                GenericBaseItem container = (GenericBaseItem) genericItems.get(syncBaseItem.getSyncMovable().getTargetContainer());
                if (container == null) {
                    log.error("BACKUP: No  target container found for: " + syncBaseItem + " taget container: " + syncBaseItem.getSyncMovable().getTargetContainer());
                }
                genericBaseItem.setTargetContainer(container);
            }
        }
        if (syncBaseItem.hasSyncHarvester()) {
            if (syncBaseItem.getSyncHarvester().getTarget() != null) {
                GenericResourceItem target = (GenericResourceItem) genericItems.get(syncBaseItem.getSyncHarvester().getTarget());
                if (target == null) {
                    log.error("BACKUP: No generic syncResourceItem for: " + syncBaseItem.getSyncHarvester().getTarget());
                }
                genericBaseItem.setResourceTarget(target);
            }

        }
        if (syncBaseItem.hasSyncWeapon()) {
            if (syncBaseItem.getSyncWeapon().getTarget() != null) {
                try {
                    SimpleBase simpleBase = ((SyncBaseItem) itemService.getItem(syncBaseItem.getSyncWeapon().getTarget())).getBase();
                    if (!baseService.isBot(simpleBase)) {
                        GenericBaseItem target = (GenericBaseItem) genericItems.get(syncBaseItem.getSyncWeapon().getTarget());
                        if (target == null) {
                            log.error("BACKUP: No generic syncBaseItem for: " + syncBaseItem.getSyncWeapon().getTarget());
                        }
                        genericBaseItem.setBaseTarget(target);
                    }
                } catch (ItemDoesNotExistException e) {
                    log.error("", e);
                }
            }
        }
        if (syncBaseItem.hasSyncBuilder()) {
            if (syncBaseItem.getSyncBuilder().getCurrentBuildup() != null) {
                GenericBaseItem target = (GenericBaseItem) genericItems.get(syncBaseItem.getSyncBuilder().getCurrentBuildup().getId());
                if (target == null) {
                    log.error("BACKUP: No generic syncBaseItem for: " + syncBaseItem.getSyncBuilder().getCurrentBuildup());
                }
                genericBaseItem.setBaseTarget(target);
            }
        }
    }

    private SyncBaseItem addSyncBaseItem(GenericBaseItem genericItem, Base base) throws NoSuchItemTypeException {
        SyncBaseItem item = (SyncBaseItem) itemService.newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getDbItemTyp().getId(), base.getSimpleBase(), services);

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
            item.getSyncMovable().setPathToDestination(genericItem.getPathToAbsoluteDestination());
        }
        if (item.getItemType().getBoundingBox().isTurnable()) {
            item.getSyncItemArea().setAngel(genericItem.getAngel());
        }
        if (item.hasSyncBuilder()) {
            item.getSyncBuilder().setToBeBuildPosition(genericItem.getPositionToBeBuilt());
            if (genericItem.getToBeBuilt() != null) {
                item.getSyncBuilder().setToBeBuiltType((BaseItemType) genericItem.getToBeBuilt().createItemType());
            }
            item.getSyncBuilder().setCreatedChildCount(genericItem.getCreatedChildCount());
        }
        if (item.hasSyncFactory()) {
            if (genericItem.getToBeBuilt() != null) {
                item.getSyncFactory().setToBeBuiltType((BaseItemType) genericItem.getToBeBuilt().createItemType());
            }
            item.getSyncFactory().setBuildupProgress(genericItem.getBuildupProgress());
            item.getSyncFactory().setCreatedChildCount(genericItem.getCreatedChildCount());
            item.getSyncFactory().setRallyPoint(genericItem.getRallyPoint());
        }
        if (item.hasSyncWeapon()) {
            item.getSyncWeapon().setFollowTarget(genericItem.isFollowTarget());
            item.getSyncWeapon().setReloadProgress(genericItem.getReloadProgress());

            if (genericItem.getBaseTarget() != null) {
                item.getSyncWeapon().setTarget(genericItem.getBaseTarget().getItemId());
            }
        }
        if (item.hasSyncHarvester()) {
            if (genericItem.getResourceTarget() != null) {
                item.getSyncHarvester().setTarget(genericItem.getResourceTarget().getItemId());
            }
        }
        if (item.hasSyncItemContainer()) {
            item.getSyncItemContainer().setUnloadPos(genericItem.getUnloadPos());
        }
        if (item.hasSyncLauncher()) {
            item.getSyncLauncher().setBuildup(genericItem.getBuildup());
        }
        syncItems.put(genericItem.getItemId(), item);
        return item;
    }

    private void addSyncItem(GenericResourceItem genericItem) throws NoSuchItemTypeException {
        SyncResourceItem item = (SyncResourceItem) itemService.newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getDbItemTyp().getId(), null, services);
        item.setAmount(genericItem.getAmount());
        syncItems.put(genericItem.getItemId(), item);
    }

    private void addSyncItem(GenericProjectileItem genericItem, Base base) throws NoSuchItemTypeException {
        SyncProjectileItem item = (SyncProjectileItem) itemService.newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getDbItemTyp().getId(), base.getSimpleBase(), services);
        item.setTarget(genericItem.getTargetPosition());
        syncItems.put(genericItem.getItemId(), item);
    }

    private void postProcessRestore(GenericBaseItem genericItem) {
        SyncBaseItem syncItem = (SyncBaseItem) syncItems.get(genericItem.getItemId());
        if (syncItem == null) {
            log.error("Can not restore GenericBaseItem: " + genericItem.getItemId());
            return;
        }
        if (syncItem.hasSyncMovable()) {
            if (genericItem.getTargetContainer() != null) {
                syncItem.getSyncMovable().setTargetContainer(genericItem.getTargetContainer().getItemId());
            }
        }
        if (syncItem.hasSyncHarvester()) {
            if (genericItem.getBaseTarget() != null) {
                SyncItem target = syncItems.get(genericItem.getBaseTarget().getItemId());
                if (target == null) {
                    throw new IllegalStateException("Harvester, no sync item for: " + genericItem.getBaseTarget());
                }
            }
        }
        if (syncItem.hasSyncWeapon()) {
            if (genericItem.getBaseTarget() != null) {
                SyncItem target = syncItems.get(genericItem.getBaseTarget().getItemId());
                if (target == null) {
                    throw new IllegalStateException("Weapon, no sync item for: " + genericItem.getBaseTarget());
                }
            }
        }

        if (syncItem.hasSyncBuilder()) {
            if (genericItem.getBaseTarget() != null) {
                SyncBaseItem target = (SyncBaseItem) syncItems.get(genericItem.getBaseTarget().getItemId());
                if (target == null) {
                    throw new IllegalStateException("Builder, no sync item for: " + genericItem.getBaseTarget());
                }
                syncItem.getSyncBuilder().setCurrentBuildup(target);
            }
        }

        if (syncItem.hasSyncItemContainer()) {
            if (genericItem.getContainedItems() != null && !genericItem.getContainedItems().isEmpty()) {
                ArrayList<Id> containedItems = new ArrayList<Id>();
                for (GenericBaseItem genericBaseItem : genericItem.getContainedItems()) {
                    SyncBaseItem child = (SyncBaseItem) syncItems.get(genericBaseItem.getItemId());
                    if (child != null) {
                        containedItems.add(child.getId());
                    } else {
                        log.error("Can not add BaseSyncItem " + genericBaseItem.getItemId() + " to container " + syncItem);
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

