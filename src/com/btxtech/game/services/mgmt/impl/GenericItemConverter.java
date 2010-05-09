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
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.item.itemType.DbBaseItemType;
import com.btxtech.game.services.resource.ResourceService;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: beat
 * Date: Sep 23, 2009
 * Time: 12:06:40 PM
 */
public class GenericItemConverter {
    private BackupEntry backupEntry;
    private HashMap<Id, GenericItem> genericItems = new HashMap<Id, GenericItem>();
    private HashMap<Id, SyncItem> syncItems = new HashMap<Id, SyncItem>();
    private BaseService baseService;
    private ActionService actionService;
    private ServerEnergyService serverEnergyService;
    private ItemService itemService;
    private Services services;
    private Log log = LogFactory.getLog(GenericItemConverter.class);

    public GenericItemConverter(BaseService baseService, ItemService itemService, Services services, ServerEnergyService serverEnergyService, ActionService actionService) {
        this.baseService = baseService;
        this.itemService = itemService;
        this.services = services;
        this.serverEnergyService = serverEnergyService;
        this.actionService = actionService;
    }

    public BackupEntry generateBackupEntry() {
        backupEntry = new BackupEntry();
        List<SyncItem> syncItems = itemService.getItemsCopyNoDummiesNoBots();
        backupEntry.setTimeStamp(new Date());

        for (SyncItem item : syncItems) {
            addGenericItem(item);
        }
        // postprocess refermces
        for (SyncItem item : syncItems) {
            postprocessGenericRefernces(item);
        }
        backupEntry.setItems(genericItems.values());

        return backupEntry;
    }

    public void restorBackup(BackupEntry backupEntry) throws NoSuchItemTypeException {
        actionService.pause(true);
        serverEnergyService.pauseService(true);
        Collection<GenericItem> genericItems = backupEntry.getItems();
        Collection<Base> bases = new HashSet<Base>();
        for (GenericItem genericItem : genericItems) {
            if (genericItem instanceof GenericBaseItem) {
                SyncBaseItem syncItem = addSyncBaseItem((GenericBaseItem) genericItem);
                Base base = ((GenericBaseItem) genericItem).getBase();
                bases.add(base);
                base.addItem(syncItem);
            } else if (genericItem instanceof GenericResourceItem) {
                addSyncItem((GenericResourceItem) genericItem);
            } else {
                log.error("restorBackup: unknwon type: " + genericItem);
            }
        }

        // post process
        for (GenericItem genericItem : genericItems) {
            if (genericItem instanceof GenericBaseItem) {
                checkSyncItemReference((GenericBaseItem) genericItem);
            }
        }

        baseService.restoreBases(bases);
        itemService.restoreItems(syncItems.values());
        serverEnergyService.pauseService(false);
        serverEnergyService.restoreItems(syncItems.values());
        actionService.pause(false);
    }


    private void addGenericItem(SyncItem item) {
        if (item instanceof SyncBaseItem) {
            addGenericBaseItem((SyncBaseItem) item);
        } else if (item instanceof SyncResourceItem) {
            addGenericResourceItem((SyncResourceItem) item);
        } else {
            throw new IllegalArgumentException("Unknwon SyncItem: " + item);
        }
    }

    private void addGenericResourceItem(SyncResourceItem item) {
        GenericResourceItem genericItem = new GenericResourceItem(backupEntry);
        fillGenericItem(item, genericItem);
        genericItem.setAmount(item.getAmount());
        genericItems.put(item.getId(), genericItem);
    }

    private void addGenericBaseItem(SyncBaseItem item) {

        GenericBaseItem genericItem = new GenericBaseItem(backupEntry);

        fillGenericItem(item, genericItem);

        genericItem.setHealth((int)item.getHealth());
        genericItem.setBuild(item.isReady());
        Base base = baseService.getBase(item.getBase());
        if (base == null) {
            throw new IllegalStateException("No base for " + item.getBase());
        }
        genericItem.setBase(base);
        if (item.hasSyncMovable()) {
            genericItem.setPathToAbsoluteDestination(item.getSyncMovable().getPathToDestination());
        }
        if (item.hasSyncTurnable()) {
            genericItem.setAngel(item.getSyncTurnable().getAngel());
        }
        if (item.hasSyncBuilder()) {
            genericItem.setPositionToBeBuilt(item.getSyncBuilder().getToBeBuildPosition());
            if (item.getSyncBuilder().getToBeBuiltType() != null) {
                genericItem.setToBeBuilt((DbBaseItemType) itemService.getDbItemType(item.getSyncBuilder().getToBeBuiltType().getId()));
            }
            genericItem.setCreatedChildCount(item.getSyncBuilder().getCreatedChildCount());
        }
        if (item.hasSyncFactory()) {
            if (item.getSyncFactory().getToBeBuiltType() != null) {
                genericItem.setToBeBuilt((DbBaseItemType) itemService.getDbItemType(item.getSyncFactory().getToBeBuiltType().getId()));
            }
            genericItem.setBuildupProgress(item.getSyncFactory().getBuildupProgress());
            genericItem.setCreatedChildCount(item.getSyncFactory().getCreatedChildCount());
            genericItem.setRallyPoint(item.getSyncFactory().getRallyPoint());
        }
        if (item.hasSyncWaepon()) {
            genericItem.setFollowTarget(item.getSyncWaepon().isFollowTarget());
            genericItem.setReloadProgress(item.getSyncWaepon().getReloadProgress());
        }
        genericItems.put(item.getId(), genericItem);

    }

    private void fillGenericItem(SyncItem item, GenericItem genericItem) {
        SyncItemInfo syncInfo = item.getSyncInfo();
        genericItem.setItemId(syncInfo.getId());
        genericItem.setPosition(syncInfo.getPosition());
        genericItem.setItemTypeId(itemService.getDbItemType(syncInfo.getItemTypeId()));
    }

    private void postprocessGenericRefernces(SyncItem syncItem) {
        GenericItem genericItem = genericItems.get(syncItem.getId());
        if (!(genericItem instanceof GenericBaseItem)) {
            return;
        }
        GenericBaseItem genericBaseItem = (GenericBaseItem) genericItem;
        SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
        if (syncBaseItem.hasSyncHarvester()) {
            if (syncBaseItem.getSyncHarvester().getTarget() != null) {
                GenericResourceItem target = (GenericResourceItem) genericItems.get(syncBaseItem.getSyncHarvester().getTarget());
                if (target == null) {
                    log.info("BACKUP: No generic syncResourceItem for: " + syncBaseItem.getSyncHarvester().getTarget());
                }
                genericBaseItem.setResourceTarget(target);
            }

        }
        if (syncBaseItem.hasSyncWaepon()) {
            if (syncBaseItem.getSyncWaepon().getTarget() != null) {
                GenericBaseItem target = (GenericBaseItem) genericItems.get(syncBaseItem.getSyncWaepon().getTarget());
                if (target == null) {
                    log.info("BACKUP: No generic syncBaseItem for: " + syncBaseItem.getSyncWaepon().getTarget());
                }
                genericBaseItem.setBaseTarget(target);
            }
        }
        if (syncBaseItem.hasSyncBuilder()) {
            if (syncBaseItem.getSyncBuilder().getCurrentBuildup() != null) {
                GenericBaseItem target = (GenericBaseItem) genericItems.get(syncBaseItem.getSyncBuilder().getCurrentBuildup().getId());
                if (target == null) {
                    log.info("BACKUP: No generic syncBaseItem for: " + syncBaseItem.getSyncBuilder().getCurrentBuildup());
                }
                genericBaseItem.setBaseTarget(target);
            }
        }
    }

    private SyncBaseItem addSyncBaseItem(GenericBaseItem genericItem) throws NoSuchItemTypeException {
        SyncBaseItem item = (SyncBaseItem) itemService.newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getItemTyp().getId(), genericItem.getBase().getSimpleBase(), services);

        item.setHealth(genericItem.getHealth());
        item.setBuild(genericItem.isBuild());
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
        if (item.hasSyncWaepon()) {
            item.getSyncWaepon().setFollowTarget(genericItem.isFollowTarget());
            item.getSyncWaepon().setReloadProgress(genericItem.getReloadProgress());

            if (genericItem.getBaseTarget() != null) {
                item.getSyncWaepon().setTarget(genericItem.getBaseTarget().getItemId());
            }
        }
        if (item.hasSyncHarvester()) {
            if (genericItem.getResourceTarget() != null) {
                item.getSyncHarvester().setTarget(genericItem.getResourceTarget().getItemId());
            }
        }
        syncItems.put(genericItem.getItemId(), item);
        return item;
    }

    private void addSyncItem(GenericResourceItem genericItem) throws NoSuchItemTypeException {
        SyncResourceItem item = (SyncResourceItem) itemService.newSyncItem(genericItem.getItemId(), genericItem.getPosition(), genericItem.getItemTyp().getId(), null, services);
        item.setAmount(genericItem.getAmount());
        syncItems.put(genericItem.getItemId(), item);
    }


    private void checkSyncItemReference(GenericBaseItem genericItem) {
        SyncBaseItem syncItem = (SyncBaseItem) syncItems.get(genericItem.getItemId());
        if (syncItem.hasSyncHarvester()) {
            if (genericItem.getBaseTarget() != null) {
                SyncItem target = syncItems.get(genericItem.getBaseTarget().getItemId());
                if (target == null) {
                    throw new IllegalStateException("Harvester, no sync item for: " + genericItem.getBaseTarget());
                }
            }
        }
        if (syncItem.hasSyncWaepon()) {
            if (genericItem.getBaseTarget() != null) {
                SyncItem target = syncItems.get(genericItem.getBaseTarget().getItemId());
                if (target == null) {
                    throw new IllegalStateException("Waepon, no sync item for: " + genericItem.getBaseTarget());
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
    }

}

