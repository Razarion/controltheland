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
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.item.itemType.DbItemType;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.ServerConditionService;
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
import java.util.List;
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
    private BackupEntry backupEntry;
    private HashMap<Id, GenericItem> genericItems = new HashMap<Id, GenericItem>();
    private HashMap<Id, SyncItem> syncItems = new HashMap<Id, SyncItem>();
    private HashMap<Integer, DbItemType> dbItemTypeCache = new HashMap<Integer, DbItemType>();
    private Log log = LogFactory.getLog(GenericItemConverter.class);

    public void clear() {
        backupEntry = null;
        genericItems.clear();
        syncItems.clear();
        dbItemTypeCache.clear();
    }

    public BackupEntry generateBackupEntry() {
        backupEntry = new BackupEntry();
        fillHelperCache();
        List<SyncItem> syncItems = itemService.getItemsCopy();
        backupEntry.setTimeStamp(new Date());

        for (SyncItem item : syncItems) {
            addGenericItem(item);
        }
        // post process references
        for (SyncItem item : syncItems) {
            postProcessBackup(item);
        }
        backupEntry.setItems(new HashSet<GenericItem>(genericItems.values()));
        serverConditionService.backup();

        Set<UserState> userStates = new HashSet<UserState>();
        for (UserState userState : userService.getAllUserStates()) {
            userState.prepareForBackup(backupEntry);
            if (userState.isRegistered() || userState.isBot()) {
                userStates.add(userState);
            }
        }

        backupEntry.setUserStates(userStates);
        return backupEntry;
    }

    public void restoreBackup(BackupEntry backupEntry) throws NoSuchItemTypeException {
        actionService.pause(true);
        serverEnergyService.pauseService(true);
        Collection<GenericItem> genericItems = backupEntry.getItems();
        Collection<UserState> userStateAbandoned = new ArrayList<UserState>();
        Collection<Base> bases = new HashSet<Base>();
        for (GenericItem genericItem : genericItems) {
            try {
                if (genericItem instanceof GenericBaseItem) {
                    SyncBaseItem syncItem = addSyncBaseItem((GenericBaseItem) genericItem);
                    Base base = ((GenericBaseItem) genericItem).getBase();
                    bases.add(base);
                    UserState userState = base.getUserState();
                    if (userState != null) {
                        if (!userState.isRegistered() && !userState.isBot()) {
                            base.setAbandoned();
                            userStateAbandoned.add(userState);
                        }
                    }
                    base.addItemNoCreateCount(syncItem);
                } else if (genericItem instanceof GenericResourceItem) {
                    addSyncItem((GenericResourceItem) genericItem);
                } else if (genericItem instanceof GenericProjectileItem) {
                    addSyncItem((GenericProjectileItem) genericItem);
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

        userService.restore(backupEntry.getUserStates());
        baseService.restoreBases(bases);
        itemService.restoreItems(syncItems.values());
        serverEnergyService.pauseService(false);
        serverEnergyService.restoreItems(syncItems.values());
        serverConditionService.restore(backupEntry.getUserStates());
        actionService.pause(false);
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
        Base base = baseService.getBase(item.getBase());
        if (base == null) {
            throw new IllegalStateException("No base for " + item.getBase());
        }
        genericProjectileItem.setBase(base);
        genericProjectileItem.setTargetPosition(item.getTarget());
        genericItems.put(item.getId(), genericProjectileItem);
    }

    private void addGenericBaseItem(SyncBaseItem item) {
        GenericBaseItem genericItem = new GenericBaseItem(backupEntry);

        fillGenericItem(item, genericItem);

        genericItem.setHealth((int) item.getHealth());
        genericItem.setBuildup(item.getBuildup());
        Base base = baseService.getBase(item.getBase());
        if (base == null) {
            throw new IllegalStateException("No base for " + item.getBase());
        }
        genericItem.setBase(base);
        genericItem.setUpgrading(item.isUpgrading());
        if (item.getUpgradingItemType() != null) {
            genericItem.setUpgradingItemType((DbBaseItemType) dbItemTypeCache.get(item.getUpgradingItemType().getId()));
        }
        genericItem.setUpgradeProgress(item.getUpgradeProgress());
        if (item.hasSyncMovable()) {
            genericItem.setPathToAbsoluteDestination(item.getSyncMovable().getPathToDestination());
        }
        if (item.hasSyncTurnable()) {
            genericItem.setAngel(item.getSyncTurnable().getAngel());
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
                GenericBaseItem target = (GenericBaseItem) genericItems.get(syncBaseItem.getSyncWeapon().getTarget());
                if (target == null) {
                    log.error("BACKUP: No generic syncBaseItem for: " + syncBaseItem.getSyncWeapon().getTarget());
                }
                genericBaseItem.setBaseTarget(target);
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

    private SyncBaseItem addSyncBaseItem(GenericBaseItem genericItem) throws NoSuchItemTypeException {
        SyncBaseItem item = (SyncBaseItem) itemService.newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getDbItemTyp().getId(), genericItem.getBase().getSimpleBase(), services);

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
        if (item.hasSyncTurnable()) {
            item.getSyncTurnable().setAngel(genericItem.getAngel());
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

    private void addSyncItem(GenericProjectileItem genericItem) throws NoSuchItemTypeException {
        SyncProjectileItem item = (SyncProjectileItem) itemService.newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getDbItemTyp().getId(), genericItem.getBase().getSimpleBase(), services);
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

        if (syncItem.getPosition() == null && !syncItem.isContainedIn()) {
            throw new IllegalStateException("SyncBaseItem has no position but is not contained in: " + syncItem);
        }

    }
}

