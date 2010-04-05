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

import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.bot.BaseExecutor;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:18:11
 */
@Component(value = "botService")
public class BotServiceImpl implements BotService {
    @Autowired
    private UserService userService;
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ConnectionService connectionService;
    private BaseBalance baseBalance;
    private Thread botThread;
    private Log log = LogFactory.getLog(BotServiceImpl.class);
    private BaseExecutor baseExecutor;
    private BotServiceConfig botServiceConfig = new BotServiceConfig();
    private Base botBase;


    public User getBotUser() {
        User user = userService.getUser(botServiceConfig.getUserName());
        if (user == null) {
            throw new IllegalStateException("User does not exist: " + botServiceConfig.getUserName());
        }
        return user;
    }

    private Base getBotBase() {
        Base base = baseService.getBase(getBotUser());
        if (base == null) {
            throw new IllegalStateException("Bot has no base");
        }
        return base;
    }

    @Override
    public void start() {
        botBase = getBotBase();
        botBase.setBot(true);
        baseExecutor = new BaseExecutor(serverServices, botBase.getSimpleBase());
        baseBalance = new BaseBalance(baseExecutor);
        for (SyncBaseItem syncBaseItem : botBase.getItems()) {
            baseBalance.addItemPosAndType(new ItemPosAndType(syncBaseItem));
        }
        runBot();
        connectionService.sendOnlineBasesUpdate();
    }

    private void stop() {
        if (botThread == null) {
            throw new IllegalStateException("Bot thread is not running");
        }
        Thread tmp = botThread;
        botThread = null;
        tmp.interrupt();
        botBase.setBot(false);
        connectionService.sendOnlineBasesUpdate();
    }

    private void runBot() {
        if (botThread != null) {
            throw new IllegalStateException("Bot is already running");
        }
        if (baseBalance.isEmpty()) {
            throw new IllegalStateException("Base does not have any items");
        }

        botThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (botThread != null) {
                        doItemBalance();
                        doMoneyBalance();
                        Thread.sleep(botServiceConfig.getBotActionDelay());
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

    private void doItemBalance() {
        // Get Dead items
        List<ItemPosAndType> deadItems = baseBalance.getDeadItems();
        for (ItemPosAndType deadItem : deadItems) {
            log.info("Bot: Killed item " + deadItem.getSyncBaseItem());
        }

        // Recreate dead items
        Map<BaseItemType, List<SyncBaseItem>> availableItems = itemService.getItems4Base(botBase.getSimpleBase());
        for (ItemPosAndType deadItem : deadItems) {
            baseExecutor.doBalanceItemType(availableItems, deadItem.getBaseItemType(), deadItem.getPosition());
        }

        // Insert new items
        List<SyncBaseItem> aliveItems = baseBalance.getAliveItems();
        ArrayList<SyncBaseItem> newItems = new ArrayList<SyncBaseItem>(botBase.getItems());
        newItems.removeAll(aliveItems);
        baseBalance.addSyncBaseItems(newItems);
    }

    private void doMoneyBalance() {
        int money = (int) botBase.getAccountBalance();
        if (money < botServiceConfig.getMinMoney()) {
            try {
                baseExecutor.doAllIdleHarvest();
            } catch (NoSuchItemTypeException e) {
                log.error("", e);
            }
        }
    }


    @Override
    public SimpleBase getOnlineBotBase() {
        if (botThread != null) {
            return botBase.getSimpleBase();
        } else {
            return null;
        }
    }

    @Override
    public void onConnectionClosed(Base base) {
        if (base.equals(botBase)) {
            start();
        }
    }

    @Override
    public void onConnectionCreated(Base base) {
        if (base.equals(botBase)) {
            stop();
        }
    }
}
