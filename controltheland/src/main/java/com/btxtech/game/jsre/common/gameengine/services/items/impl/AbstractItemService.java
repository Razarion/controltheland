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

package com.btxtech.game.jsre.common.gameengine.services.items.impl;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.ObjectHolder;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.PositionTakenException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.BoxItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ProjectileItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.ResourceType;
import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBoxItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.TargetHasNoPositionException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncProjectileItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncResourceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 28.11.2009
 * Time: 13:04:56
 */
abstract public class AbstractItemService implements ItemService {
    private Logger log = Logger.getLogger(AbstractItemService.class.getName());

    /**
     * Iterates over all sync items
     *
     * @param includeNoPosition include items which have no position (e.g. items which are inside a container)
     * @param includeDead       includes dead items (isAlive = false)
     * @param defaultReturn     if iteration is finished without an aport, this param is returned
     * @param itemHandler       see ItemHandler
     * @return the parameter from the itemHandler or the defaultReturn
     */
    protected abstract <T> T iterateOverItems(boolean includeNoPosition, boolean includeDead, T defaultReturn, ItemHandler<T> itemHandler);

    protected abstract GlobalServices getGlobalServices();

    protected abstract PlanetServices getPlanetServices();

    public SyncItem newSyncItem(Id id, Index position, int itemTypeId, SimpleBase base, GlobalServices globalServices, PlanetServices planetServices) throws NoSuchItemTypeException {
        ItemType itemType = getGlobalServices().getItemTypeService().getItemType(itemTypeId);
        SyncItem syncItem;
        if (itemType instanceof BaseItemType) {
            if (base == null) {
                throw new NullPointerException(this + " base must be set for a SyncBaseItem");
            }
            syncItem = new SyncBaseItem(id, position, (BaseItemType) itemType, globalServices, planetServices, base);
        } else if (itemType instanceof ResourceType) {
            if (base != null) {
                throw new IllegalArgumentException(this + " ResourceType does not have a base");
            }
            syncItem = new SyncResourceItem(id, position, (ResourceType) itemType, globalServices, planetServices);
        } else if (itemType instanceof ProjectileItemType) {
            if (base == null) {
                throw new NullPointerException(this + " base must be set for a ProjectileItemType");
            }
            syncItem = new SyncProjectileItem(id, position, (ProjectileItemType) itemType, globalServices, planetServices, base);
        } else if (itemType instanceof BoxItemType) {
            if (base != null) {
                throw new IllegalArgumentException(this + " BoxItemType does not have a base");
            }
            syncItem = new SyncBoxItem(id, position, (BoxItemType) itemType, globalServices, planetServices);
        } else {
            throw new IllegalArgumentException(this + " ItemType not supported: " + itemType);
        }
        return syncItem;
    }

    @Override
    public List<SyncBaseItem> getBaseItems(List<Id> baseItemsIds) throws ItemDoesNotExistException {
        ArrayList<SyncBaseItem> syncBaseItems = new ArrayList<SyncBaseItem>();
        for (Id baseItemsId : baseItemsIds) {
            syncBaseItems.add((SyncBaseItem) getItem(baseItemsId));
        }
        return syncBaseItems;
    }

    @Override
    public List<Id> getBaseItemIds(List<SyncBaseItem> baseItems) {
        ArrayList<Id> syncBaseItemIds = new ArrayList<Id>();
        for (SyncBaseItem baseItem : baseItems) {
            syncBaseItemIds.add(baseItem.getId());
        }
        return syncBaseItemIds;
    }

    @Override
    public Collection<SyncBaseItem> getBaseItemsInRadius(Index position, int radius, SimpleBase simpleBase, Collection<BaseItemType> baseItemTypeFilter) {
        Collection<SyncBaseItem> syncBaseItems = getBaseItemsInRectangle(position.getRegion(2 * radius, 2 * radius), simpleBase, baseItemTypeFilter);
        for (Iterator<SyncBaseItem> iterator = syncBaseItems.iterator(); iterator.hasNext(); ) {
            SyncBaseItem syncBaseItem = iterator.next();
            if (!syncBaseItem.getSyncItemArea().isInRange(radius, position)) {
                iterator.remove();
            }
        }
        return syncBaseItems;
    }

    @Override
    public boolean isSyncItemOverlapping(SyncItem syncItem) {
        return isSyncItemOverlapping(syncItem, null, null, null);
    }

    @Override
    public boolean isSyncItemOverlapping(final SyncItem syncItem, final Index positionToCheck, final Double angelToCheck, final Collection<SyncItem> exceptionThem) {

        return iterateOverItems(false, false, false, new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem otherItem) {
                if (otherItem.equals(syncItem)) {
                    return null;
                }
                if (exceptionThem != null && exceptionThem.contains(otherItem)) {
                    return null;
                }

                if (otherItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) otherItem;
                    if (!syncBaseItem.hasSyncMovable() || !syncBaseItem.getSyncMovable().isActive()) {
                        if (positionToCheck == null) {
                            if (syncItem.getSyncItemArea().contains(syncBaseItem)) {
                                return true;
                            }
                        } else {
                            if (syncItem.getSyncItemArea().contains(syncBaseItem, positionToCheck, angelToCheck)) {
                                return true;
                            }
                        }
                    }
                } else if (otherItem instanceof SyncResourceItem) {
                    if (positionToCheck == null) {
                        if (syncItem.getSyncItemArea().contains(otherItem)) {
                            return true;
                        }
                    } else {
                        if (syncItem.getSyncItemArea().contains(otherItem, positionToCheck, angelToCheck)) {
                            return true;
                        }
                    }
                }
                return null;
            }
        });
    }

    @Override
    public boolean hasStandingItemsInRect(final Rectangle rectangle, final SyncItem exceptThat) {
        return iterateOverItems(false, false, false, new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (syncItem.equals(exceptThat)) {
                    return null;
                }
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (!syncBaseItem.hasSyncMovable() || !syncBaseItem.getSyncMovable().isActive()) {
                        if (syncItem.getSyncItemArea().contains(rectangle)) {
                            return true;
                        }
                    }
                } else if (syncItem instanceof SyncResourceItem) {
                    if (syncItem.getSyncItemArea().contains(rectangle)) {
                        return true;
                    }
                }
                return null;
            }
        });
    }

    @Override
    public boolean hasItemsInRectangle(final Rectangle rectangle) {
        return iterateOverItems(false, false, false, new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                return syncItem.getSyncItemArea().contains(rectangle) ? true : null;
            }
        });
    }

    @Override
    public void checkBuildingsInRect(BaseItemType toBeBuiltType, Index toBeBuildPosition) {
        if (isUnmovableSyncItemOverlapping(toBeBuiltType.getBoundingBox(), toBeBuildPosition)) {
            throw new PositionTakenException(toBeBuildPosition, toBeBuiltType);
        }
    }

    @Override
    public boolean isUnmovableSyncItemOverlapping(final BoundingBox boundingBox, final Index positionToCheck) {
        return iterateOverItems(false, false, false, new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (syncItem instanceof SyncBaseItem) {
                    SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                    if (!syncBaseItem.hasSyncMovable()) {
                        if (syncBaseItem.getSyncItemArea().contains(boundingBox, positionToCheck)) {
                            return true;
                        }
                    }
                } else if (syncItem instanceof SyncResourceItem) {
                    if (syncItem.getSyncItemArea().contains(boundingBox, positionToCheck)) {
                        return true;
                    }
                }
                return null;
            }
        });
    }

    @Override
    public Collection<SyncBaseItem> getItems4Base(final SimpleBase simpleBase) {
        final Collection<SyncBaseItem> itemsInBase = new ArrayList<SyncBaseItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).getBase().equals(simpleBase)) {
                    itemsInBase.add((SyncBaseItem) syncItem);
                }
                return null;
            }
        });
        return itemsInBase;
    }

    @Override
    public Collection<SyncBaseItem> getItems4BaseAndType(final SimpleBase simpleBase, final int itemTypeId) {
        final Collection<SyncBaseItem> itemsInBase = new ArrayList<SyncBaseItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).getBase().equals(simpleBase) && syncItem.getItemType().getId() == itemTypeId) {
                    itemsInBase.add((SyncBaseItem) syncItem);
                }
                return null;
            }
        });
        return itemsInBase;
    }

    @Override
    public void killSyncItems(Collection<SyncItem> itemsToKill) {
        for (SyncItem syncItem : itemsToKill) {
            try {
                killSyncItem(syncItem, null, true, false);
            } catch (Exception e) {
                log.log(Level.SEVERE, "", e);
            }
        }
    }

    @Override
    public Collection<SyncBaseItem> getEnemyItems(final SimpleBase simpleBase, final Rectangle region) {
        final Collection<SyncBaseItem> enemyItems = new ArrayList<SyncBaseItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (syncItem instanceof SyncBaseItem
                        && ((SyncBaseItem) syncItem).isEnemy(simpleBase)
                        && region.contains(syncItem.getSyncItemArea().getPosition())) {
                    enemyItems.add((SyncBaseItem) syncItem);
                }

                return null;
            }
        });
        return enemyItems;
    }

    @Override
    public Collection<SyncBaseItem> getEnemyItems(final SimpleBase simpleBase, final Region region) {
        final Collection<SyncBaseItem> enemyItems = new ArrayList<SyncBaseItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (syncItem instanceof SyncBaseItem
                        && ((SyncBaseItem) syncItem).isEnemy(simpleBase)
                        && region.isInside(syncItem)) {
                    enemyItems.add((SyncBaseItem) syncItem);
                }

                return null;
            }
        });
        return enemyItems;
    }

    @Override
    public SyncBaseItem getFirstEnemyItemInRange(final SyncBaseItem baseSyncItem) {
        return iterateOverItems(false, false, null, new ItemHandler<SyncBaseItem>() {
            @Override
            public SyncBaseItem handleItem(SyncItem syncItem) {
                try {
                    if (syncItem instanceof SyncBaseItem
                            && baseSyncItem.isEnemy((SyncBaseItem) syncItem)
                            && baseSyncItem.getSyncWeapon().isAttackAllowedWithoutMoving(syncItem)) {
                        return (SyncBaseItem) syncItem;
                    }
                } catch (TargetHasNoPositionException e) {
                    // Target moved to a container
                }
                return null;
            }
        });
    }

    @Override
    public boolean hasEnemyInRange(final SimpleBase simpleBase, final Index middlePoint, final int range) {
        return iterateOverItems(false, false, false, new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).isEnemy(simpleBase) && syncItem.getSyncItemArea().getDistance(middlePoint) <= range) {
                    return true;
                }

                return null;
            }
        });
    }

    @Override
    public Collection<SyncItem> getItemsInRectangle(final Rectangle rectangle) {
        final Collection<SyncItem> itemsInBase = new ArrayList<SyncItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!syncItem.getSyncItemArea().contains(rectangle)) {
                    return null;
                }

                itemsInBase.add(syncItem);
                return null;
            }
        });
        return itemsInBase;
    }

    @Override
    public Collection<SyncItem> getItemsInRectangleFast(final Rectangle rectangle) {
        final Collection<SyncItem> itemsInBase = new ArrayList<SyncItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (rectangle.contains(syncItem.getSyncItemArea().getPosition())) {
                    itemsInBase.add(syncItem);
                }
                return null;
            }
        });
        return itemsInBase;
    }

    @Override
    public Collection<SyncItem> getItemsInRectangleFastIncludingDead(final Rectangle rectangle) {
        final Collection<SyncItem> itemsInBase = new ArrayList<SyncItem>();
        iterateOverItems(false, true, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (rectangle.contains(syncItem.getSyncItemArea().getPosition())) {
                    itemsInBase.add(syncItem);
                }
                return null;
            }
        });
        return itemsInBase;
    }

    @Override
    public boolean hasItemsInRectangleFast(final Rectangle rectangle) {
        return iterateOverItems(false, false, false, new ItemHandler<Boolean>() {
            @Override
            public Boolean handleItem(SyncItem syncItem) {
                if (rectangle.contains(syncItem.getSyncItemArea().getPosition())) {
                    return true;
                }
                return null;
            }
        });
    }

    @Override
    public Collection<SyncBaseItem> getBaseItemsInRectangle(final Rectangle rectangle, final SimpleBase simpleBase, final Collection<BaseItemType> baseItemTypeFilter) {
        final Collection<SyncBaseItem> itemsInBase = new ArrayList<SyncBaseItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!(syncItem instanceof SyncBaseItem)) {
                    return null;
                }
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                if (simpleBase != null && !(syncBaseItem.getBase().equals(simpleBase))) {
                    return null;
                }
                if (!syncBaseItem.getSyncItemArea().contains(rectangle)) {
                    return null;
                }
                if (baseItemTypeFilter != null && !baseItemTypeFilter.contains(syncBaseItem.getBaseItemType())) {
                    return null;
                }

                itemsInBase.add((SyncBaseItem) syncItem);
                return null;
            }
        });
        return itemsInBase;
    }

    @Override
    public Collection<SyncBaseItem> getBaseItemsInRectangle(final Region region, final SimpleBase simpleBase, final Collection<BaseItemType> baseItemTypeFilter) {
        final Collection<SyncBaseItem> itemsInBase = new ArrayList<SyncBaseItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!(syncItem instanceof SyncBaseItem)) {
                    return null;
                }
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;
                if (simpleBase != null && !(syncBaseItem.getBase().equals(simpleBase))) {
                    return null;
                }
                if (!region.isInside(syncBaseItem)) {
                    return null;
                }
                if (baseItemTypeFilter != null && !baseItemTypeFilter.contains(syncBaseItem.getBaseItemType())) {
                    return null;
                }

                itemsInBase.add((SyncBaseItem) syncItem);
                return null;
            }
        });
        return itemsInBase;
    }

    @Override
    public Collection<? extends SyncItem> getItems(final ItemType itemType, final SimpleBase simpleBase) {
        final Collection<SyncItem> items = new ArrayList<SyncItem>();
        iterateOverItems(true, false, null, new ItemHandler<Void>() {
            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!syncItem.getItemType().equals(itemType)) {
                    return null;
                }
                if (simpleBase != null) {
                    if (syncItem instanceof SyncBaseItem && ((SyncBaseItem) syncItem).getBase().equals(simpleBase)) {
                        items.add(syncItem);
                    }
                } else {
                    items.add(syncItem);
                }
                return null;
            }
        });
        return items;
    }

    @Override
    public SyncBaseItem getNearestEnemyItem(final Index middle, final Set<Integer> filter, final SimpleBase simpleBase) {
        final ObjectHolder<SyncBaseItem> itemObjectHolder = new ObjectHolder<SyncBaseItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            int nearestDistance = Integer.MAX_VALUE;

            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!(syncItem instanceof SyncBaseItem)) {
                    return null;
                }
                SyncBaseItem syncBaseItem = (SyncBaseItem) syncItem;

                if (filter != null && !filter.contains(syncBaseItem.getBaseItemType().getId())) {
                    return null;
                }

                if (!syncBaseItem.isEnemy(simpleBase)) {
                    return null;
                }

                int distance = middle.getDistance(syncBaseItem.getSyncItemArea().getPosition());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    itemObjectHolder.setObject(syncBaseItem);
                }

                return null;
            }
        });
        return itemObjectHolder.getObject();
    }

    @Override
    public SyncResourceItem getNearestResourceItem(final Index middle) {
        final ObjectHolder<SyncResourceItem> itemObjectHolder = new ObjectHolder<SyncResourceItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            int nearestDistance = Integer.MAX_VALUE;

            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!(syncItem instanceof SyncResourceItem)) {
                    return null;
                }

                int distance = middle.getDistance(syncItem.getSyncItemArea().getPosition());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    itemObjectHolder.setObject((SyncResourceItem) syncItem);
                }

                return null;
            }
        });
        return itemObjectHolder.getObject();
    }

    @Override
    public SyncBoxItem getNearestBoxItem(final Index middle) {
        final ObjectHolder<SyncBoxItem> itemObjectHolder = new ObjectHolder<SyncBoxItem>();
        iterateOverItems(false, false, null, new ItemHandler<Void>() {
            int nearestDistance = Integer.MAX_VALUE;

            @Override
            public Void handleItem(SyncItem syncItem) {
                if (!(syncItem instanceof SyncBoxItem)) {
                    return null;
                }

                int distance = middle.getDistance(syncItem.getSyncItemArea().getPosition());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    itemObjectHolder.setObject((SyncBoxItem) syncItem);
                }

                return null;
            }
        });
        return itemObjectHolder.getObject();
    }

    @Override
    public SyncItem getItemAtAbsolutePosition(Index absolutePosition) {
        int maxItemDiameter = getGlobalServices().getItemTypeService().getMaxItemDiameter();
        Rectangle rectangle = Rectangle.generateRectangleFromMiddlePoint(absolutePosition, maxItemDiameter, maxItemDiameter);
        for (SyncItem syncItem : ItemContainer.getInstance().getItemsInRectangleFast(rectangle)) {
            if (syncItem.getSyncItemArea().contains(absolutePosition)) {
                return syncItem;
            }
        }
        return null;
    }

    @Override
    public void sellItem(Id id) throws ItemDoesNotExistException, NotYourBaseException {
        try {
            SyncBaseItem syncBaseItem = (SyncBaseItem) getItem(id);
            getPlanetServices().getBaseService().checkBaseAccess(syncBaseItem);
            double health = syncBaseItem.getHealth();
            double fullHealth = syncBaseItem.getBaseItemType().getHealth();
            double price = syncBaseItem.getBaseItemType().getPrice();
            double buildup = syncBaseItem.getBuildup();
            killSyncItem(syncBaseItem, null, true, false);
            SimpleBase simpleBase = syncBaseItem.getBase();
            // May last item sold
            if (getPlanetServices().getBaseService().isAlive(simpleBase)) {
                double money = health / fullHealth * buildup * price * Constants.ITEM_SELL_FACTOR;
                getPlanetServices().getBaseService().depositResource(money, simpleBase);
                getPlanetServices().getBaseService().sendAccountBaseUpdate(simpleBase);
            }
        } catch (ItemDoesNotExistException ignore) {
            // Ignore
            // Item may have been killed
        }
    }

    protected void killContainedItems(SyncBaseItem syncBaseItem, SimpleBase actor) {
        if (!syncBaseItem.hasSyncItemContainer()) {
            return;
        }
        for (Id id : syncBaseItem.getSyncItemContainer().getContainedItems()) {
            try {
                SyncBaseItem baseItem = (SyncBaseItem) getItem(id);
                killSyncItem(baseItem, actor, true, false);
            } catch (ItemDoesNotExistException e) {
                log.log(Level.SEVERE, "AbstractItemService.killContainedItems()", e);
            }
        }
    }
}
