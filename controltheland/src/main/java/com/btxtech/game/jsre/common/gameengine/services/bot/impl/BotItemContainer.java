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

package com.btxtech.game.jsre.common.gameengine.services.bot.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotItemConfig;
import com.btxtech.game.jsre.common.gameengine.services.collision.PathCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 18.09.2010
 * Time: 11:41:21
 */
public class BotItemContainer {
    private static final int KILL_ITERATION_MAXIMUM = 100;
    private final HashMap<SyncBaseItem, BotSyncBaseItem> botItems = new HashMap<>();
    private Need need;
    private Logger log = Logger.getLogger(BotItemContainer.class.getName());
    private PlanetServices planetServices;
    private String botName;
    private Region realm;
    private CurrentBuildups currentBuildups = new CurrentBuildups();

    public BotItemContainer(Collection<BotItemConfig> botItems, Region realm, PlanetServices planetServices, String botName) {
        this.realm = realm;
        this.planetServices = planetServices;
        this.botName = botName;
        need = new Need(botItems);
    }

    public void work(SimpleBase simpleBase) {
        updateState(simpleBase);
        Map<BotItemConfig, Integer> effectiveNeeds = need.getEffectiveItemNeed();
        if (!effectiveNeeds.isEmpty()) {
            buildItems(simpleBase, effectiveNeeds);
        }
        handleIdleItems();
    }

    public void killAllItems(SimpleBase simpleBase) {
        try {
            internalKillAllItems(simpleBase);
        } catch (Exception e) {
            log.log(Level.SEVERE, "bot killAllItems failed " + botName, e);
        }
    }

    public void internalKillAllItems(SimpleBase simpleBase) {
        for (int i = 0; i < KILL_ITERATION_MAXIMUM; i++) {
            if (simpleBase != null) {
                updateState(simpleBase);
            }
            if (botItems.isEmpty()) {
                return;
            }
            synchronized (botItems) {
                for (SyncBaseItem syncBaseItem : botItems.keySet()) {
                    if (syncBaseItem.isAlive()) {
                        planetServices.getItemService().killSyncItem(syncBaseItem, null, true, false);
                    }
                }
            }
        }
        throw new IllegalStateException("internalKillAllItems has been called for more than " + KILL_ITERATION_MAXIMUM + " times.");
    }

    public Collection<BotSyncBaseItem> getAllIdleAttackers() {
        Collection<BotSyncBaseItem> idleAttackers = new ArrayList<>();
        synchronized (botItems) {
            for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
                if (botSyncBaseItem.isIdle()) {
                    idleAttackers.add(botSyncBaseItem);
                }
            }
        }
        return idleAttackers;
    }

    /**
     * Only used for test purpose
     *
     * @return true if fulfilled
     */
    public boolean isFulfilledUseInTestOnly(SimpleBase simpleBase) {
        updateState(simpleBase);
        return need.getEffectiveItemNeed().isEmpty();
    }

    public boolean itemBelongsToMy(SyncBaseItem syncBaseItem) {
        synchronized (botItems) {
            return botItems.containsKey(syncBaseItem);
        }
    }

    private void updateState(SimpleBase simpleBase) {
        Collection<SyncBaseItem> newItems = planetServices.getBaseService().getItems(simpleBase);
        if (newItems != null) {
            synchronized (botItems) {
                newItems.removeAll(botItems.keySet());
            }
            for (SyncBaseItem newItem : newItems) {
                BotItemConfig botItemConfig = currentBuildups.onItemBuilt(newItem);
                if (botItemConfig != null) {
                    add(newItem, botItemConfig);
                } else {
                    log.warning("BotItemContainer.updateState() can not find BotItemConfig for new SyncBaseItem. Items will be destroyed: " + newItem + " on bot: " + botName);
                    try {
                        planetServices.getItemService().killSyncItem(newItem, null, true, false);
                    } catch (Exception e) {
                        log.log(Level.WARNING, "BotItemContainer.updateState() error destroying item: " + newItem + " on bot: " + botName, e);
                    }
                }
            }
        }

        ArrayList<BotSyncBaseItem> remove = new ArrayList<>();
        synchronized (botItems) {
            for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
                if (botSyncBaseItem.isAlive()) {
                    botSyncBaseItem.updateIdleState();
                    if (botSyncBaseItem.isIdle()) {
                        currentBuildups.onItemRemoved(botSyncBaseItem.getSyncBaseItem());
                    }
                } else {
                    remove.add(botSyncBaseItem);
                }
            }
        }
        for (BotSyncBaseItem botSyncBaseItem : remove) {
            remove(botSyncBaseItem);
        }
    }

    private void add(SyncBaseItem syncBaseItem, BotItemConfig botItemConfig) {
        BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, botItemConfig, planetServices);
        synchronized (botItems) {
            botItems.put(syncBaseItem, botSyncBaseItem);
        }
        need.onItemAdded(botSyncBaseItem);
    }

    private void remove(BotSyncBaseItem botSyncBaseItem) {
        synchronized (botItems) {
            botItems.remove(botSyncBaseItem.getSyncBaseItem());
        }
        need.onItemRemoved(botSyncBaseItem);
        currentBuildups.onItemRemoved(botSyncBaseItem.getSyncBaseItem());
    }

    private void buildItems(SimpleBase simpleBase, Map<BotItemConfig, Integer> effectiveNeeds) {
        for (Map.Entry<BotItemConfig, Integer> entry : effectiveNeeds.entrySet()) {

            int effectiveNeed = entry.getValue();

            effectiveNeed -= currentBuildups.getBuildupCount(entry.getKey());
            if (effectiveNeed < 0) {
                effectiveNeed = 0;
            }

            for (int i = 0; i < effectiveNeed; i++) {
                try {
                    createItem(entry.getKey(), simpleBase);
                } catch (PlaceCanNotBeFoundException t) {
                    log.warning(botName + ": " + t.getMessage());
                } catch (Exception e) {
                    log.log(Level.SEVERE, botName, e);
                }
            }
        }
    }

    private void createItem(BotItemConfig botItemConfig, SimpleBase simpleBase) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        BaseItemType toBeBuilt = botItemConfig.getBaseItemType();
        if (botItemConfig.isCreateDirectly()) {
            Index position = planetServices.getCollisionService().getFreeRandomPosition(toBeBuilt, getSafeRegion(botItemConfig.getRegion()), 0, false, true);
            SyncBaseItem newItem = (SyncBaseItem) planetServices.getItemService().createSyncObject(toBeBuilt, position, null, simpleBase);
            newItem.setBuildup(1.0);
            add(newItem, botItemConfig);
        } else {
            BotSyncBaseItem botSyncBuilder = getFirstIdleBuilder(toBeBuilt);
            if (botSyncBuilder == null) {
                return;
            }
            if (botSyncBuilder.getSyncBaseItem().hasSyncFactory()) {
                botSyncBuilder.buildUnit(toBeBuilt);
            } else {
                Index position = planetServices.getCollisionService().getFreeRandomPosition(toBeBuilt, getSafeRegion(botItemConfig.getRegion()), 0, false, true);
                try {
                    botSyncBuilder.buildBuilding(position, toBeBuilt);
                } catch (PathCanNotBeFoundException e) {
                    log.warning("buildBuilding failed for bot: " + botName + " " + planetServices.getPlanetInfo().getPlanetLiteInfo() + " " + e.getMessage());
                } catch (Exception e) {
                    log.log(Level.SEVERE, "buildBuilding failed for bot: " + botName + " " + planetServices.getPlanetInfo().getPlanetLiteInfo(), e);
                }
            }
            currentBuildups.startBuildup(botItemConfig, botSyncBuilder.getSyncBaseItem());
        }
    }

    private Region getSafeRegion(Region region) {
        if (region != null) {
            return region;
        } else {
            return realm;
        }
    }


    private BotSyncBaseItem getFirstIdleBuilder(BaseItemType toBeBuilt) {
        synchronized (botItems) {
            for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
                if (botSyncBaseItem.isIdle() && botSyncBaseItem.isAbleToBuild(toBeBuilt)) {
                    return botSyncBaseItem;
                }
            }
        }
        return null;
    }

    private void handleIdleItems() {
        synchronized (botItems) {
            for (BotSyncBaseItem botSyncBaseItem : botItems.values()) {
                if (!botSyncBaseItem.isIdle()) {
                    continue;
                }

                BotItemConfig botItemConfig = botSyncBaseItem.getBotItemConfig();
                if (botItemConfig.isMoveRealmIfIdle() && botSyncBaseItem.canMove() && !realm.isInsideAbsolute(botSyncBaseItem.getPosition())) {
                    botSyncBaseItem.move(realm);
                } else if (botItemConfig.getIdleTtl() != null && botSyncBaseItem.getIdleTimeStamp() + botItemConfig.getIdleTtl() < System.currentTimeMillis()) {
                    botSyncBaseItem.kill();
                }
            }
        }
    }

    private class CurrentBuildups {
        private HashMap<Integer, BotItemConfig> builders = new HashMap<>();

        public int getBuildupCount(BotItemConfig botItemConfig) {
            int count = 0;
            for (BotItemConfig itemConfig : builders.values()) {
                if (itemConfig.equals(botItemConfig)) {
                    count++;
                }
            }
            return count;
        }

        public void startBuildup(BotItemConfig toBeBuilt, SyncBaseItem builder) {
            builders.put(builder.getId().getId(), toBeBuilt);
        }

        public BotItemConfig onItemBuilt(SyncBaseItem newItem) {
            if (newItem.getId().hasParent()) {
                return builders.remove(newItem.getId().getParentId());
            } else {
                return null;
            }
        }

        public void onItemRemoved(SyncBaseItem removedItem) {
            builders.remove(removedItem.getId().getId());
        }
    }
}
