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

package com.btxtech.game.services.bot.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.action.ActionService;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.base.GameFullException;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotItemCount;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 21.05.2010
 * Time: 21:51:58
 */
@Component(value = "botRunner")
@Scope("prototype")
public class BotRunner {
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private BotService botService;
    @Autowired
    private UserService userService;
    private DbBotConfig botConfig;
    private Base base;
    private Thread botThread;
    private Log log = LogFactory.getLog(BotRunner.class);
    private HashMap<SyncBaseItem, BotItemContainer> botItems = new HashMap<SyncBaseItem, BotItemContainer>();
    private BotItemContainer baseFundamental;
    private BotItemContainer baseBuilders;
    private BotItemContainer defence;
    private BotDefenseContainer realmDefense;
    private BotDefenseContainer coreDefense;
    private UserState userState;


    public void setBotConfig(DbBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public void synchronize(DbBotConfig botConfig) {
        this.botConfig = botConfig;
        setupBot();
    }

    public void start() {
        userState = userService.getUserState(botConfig);
        checkBase();
        runBot();
    }

    public void stop() {
        if (botThread == null) {
            throw new IllegalStateException("Bot thread is not running");
        }
        Thread tmp = botThread;
        botThread = null;
        tmp.interrupt();
        if (base != null) {
            baseService.setBot(base, false);
        }
    }

    private void setupBot() {
        botItems.clear();
        baseFundamental = createBotItemContainer(botConfig.getBaseFundamental());
        baseBuilders = createBotItemContainer(botConfig.getBaseBuildup());
        defence = createBotItemContainer(botConfig.getDefence());
        realmDefense = new BotDefenseContainer(defence, botConfig.getRealmSuperiority(), null, botConfig.getCore());
        coreDefense = new BotDefenseContainer(defence, botConfig.getRealmSuperiority(), realmDefense, botConfig.getRealm());
    }

    private void runBot() {
        if (botThread != null) {
            throw new IllegalStateException("Bot is already running");
        }

        botThread = new Thread() {
            @Override
            public void run() {
                setupBot();
                try {
                    while (botThread != null) {
                        try {
                            checkBase();
                            // Fundamentals
                            setupBaseBuildup(baseFundamental, new BotItemFactory() {
                                @Override
                                public BotSyncBaseItem createItem(BaseItemType toBeBuilt) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
                                    Index position = collisionService.getFreeRandomPosition(toBeBuilt, botConfig.getCore(), 100);
                                    SyncBaseItem newItem = (SyncBaseItem) itemService.createSyncObject(toBeBuilt, position, null, base.getSimpleBase(), 0);
                                    newItem.setBuildup(1.0);
                                    return null;
                                }
                            });
                            // Fundamentals
                            setupBaseBuildup(baseBuilders, new BotItemFactory() {
                                @Override
                                public BotSyncBaseItem createItem(BaseItemType toBeBuilt) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
                                    BotSyncBaseItem botSyncBaseItem = baseFundamental.getFirstIdleItem();
                                    if (botSyncBaseItem == null) {
                                        return null;
                                    }
                                    Index position = collisionService.getFreeRandomPosition(toBeBuilt, botConfig.getCore(), 0);
                                    botSyncBaseItem.buildBuilding(position, toBeBuilt);
                                    return botSyncBaseItem;
                                }
                            });
                            // Defense
                            setupBaseBuildup(defence, new BotItemFactory() {
                                @Override
                                public BotSyncBaseItem createItem(BaseItemType toBeBuilt) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
                                    BotSyncBaseItem botSyncBaseItem = baseBuilders.getFirstIdleItem();
                                    if (botSyncBaseItem == null) {
                                        return null;
                                    }
                                    botSyncBaseItem.buildUnit(toBeBuilt);
                                    return botSyncBaseItem;
                                }
                            });
                            protectCore(coreDefense);
                            protectCore(realmDefense);
                            Thread.sleep(botConfig.getActionDelay());
                        } catch (ItemLimitExceededException e) {
                            log.error("Bot " + botConfig.getName() + ": " + e.getMessage());
                        } catch (HouseSpaceExceededException e) {
                            log.error("Bot " + botConfig.getName() + ": " + e.getMessage());
                        }
                    }
                } catch (InterruptedException ignore) {
                    botThread = null;
                } catch (Throwable t) {
                    log.error("", t);
                    botThread = null;
                }
            }
        };
        botThread.setDaemon(true);
        botThread.start();
    }

    private void checkBase() {
        if (base == null || !baseService.isAlive(base.getSimpleBase())) {
            base = baseService.getBase(userState);
            if (base == null) {
                try {
                    base = baseService.createBotBase(userState);
                } catch (GameFullException e) {
                    log.error("", e);
                }
            }
            baseService.setBot(base, true);
        }

    }

    private void protectCore(BotDefenseContainer botDefenseContainer) {
        List<SyncBaseItem> intruders = itemService.getEnemyItems(base.getSimpleBase(), botDefenseContainer.getRegion());
        botDefenseContainer.handleIntruders(intruders);
    }

    private BotItemContainer createBotItemContainer(Collection<DbBotItemCount> dbBotItemCounts) {
        Map<BaseItemType, Integer> needs = getBaseItemTypes(dbBotItemCounts);
        return new BotItemContainer(needs);
    }

    private void setupBaseBuildup(BotItemContainer botItemContainer, BotItemFactory botItemFactory) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        botItemContainer.updateState();
        if (botItemContainer.isFulfilled()) {
            return;
        }
        Collection<SyncBaseItem> availableSyncBaseItems = itemService.getBaseItemsInRectangle(botConfig.getRealm(), base.getSimpleBase(), botItemContainer.getNeeds().keySet());
        assignItemPosAndTypes(availableSyncBaseItems, botItemContainer);
        for (Map.Entry<BaseItemType, Integer> entry : botItemContainer.getNeeds().entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                BotSyncBaseItem botSyncBaseItem = botItemFactory.createItem(entry.getKey());
                if (botSyncBaseItem != null) {
                    botItemContainer.addBuildingItem(botSyncBaseItem);
                }
            }
        }
    }

    private Map<BaseItemType, Integer> getBaseItemTypes(Collection<DbBotItemCount> dbBotItemCounts) {
        Map<BaseItemType, Integer> baseItemTypes = new HashMap<BaseItemType, Integer>();
        for (DbBotItemCount dbBotItemCount : dbBotItemCounts) {
            BaseItemType baseItemType = (BaseItemType) itemService.getItemType(dbBotItemCount.getBaseItemType());
            Integer count = baseItemTypes.get(baseItemType);
            if (count == null) {
                count = 0;
            }
            baseItemTypes.put(baseItemType, count + dbBotItemCount.getCount());
        }
        return baseItemTypes;
    }

    private void assignItemPosAndTypes(Collection<SyncBaseItem> syncBaseItems, BotItemContainer botItemContainer) {
        for (SyncBaseItem syncBaseItem : syncBaseItems) {
            if (botItems.containsKey(syncBaseItem)) {
                continue;
            }
            BotSyncBaseItem botSyncBaseItem = new BotSyncBaseItem(syncBaseItem, actionService);
            if (botItemContainer.add(botSyncBaseItem)) {
                botItems.put(syncBaseItem, botItemContainer);
            }
        }
    }

    public Base getBase() {
        return base;
    }
}
