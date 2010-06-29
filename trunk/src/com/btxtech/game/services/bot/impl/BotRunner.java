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

import com.btxtech.game.jsre.common.bot.BaseExecutor;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import java.util.ArrayList;
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
    private BaseBalance baseBalance;
    private BaseExecutor baseExecutor;
    private DbBotConfig botConfig;
    private Base base;
    private Thread botThread;
    private Log log = LogFactory.getLog(BotRunner.class);
    private boolean pause = false;

    public void setBase(Base base) {
        this.base = base;
    }

    public void setBotConfig(DbBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public void start() {
        baseExecutor = new BaseExecutor(serverServices, base.getSimpleBase());
        baseBalance = new BaseBalance(baseExecutor);
        fillBotItems();
        runBot();
        connectionService.sendOnlineBasesUpdate();
    }

    private void fillBotItems() {
        baseBalance.clear();
        for (SyncBaseItem syncBaseItem : base.getItems()) {
            baseBalance.addItemPosAndType(new ItemPosAndType(syncBaseItem));
        }
    }

    public void stop() {
        if (botThread == null) {
            throw new IllegalStateException("Bot thread is not running");
        }
        Thread tmp = botThread;
        botThread = null;
        tmp.interrupt();
        connectionService.sendOnlineBasesUpdate();
    }

    public void pause(boolean pause) {
        if (!pause) {
            fillBotItems();
        }
        this.pause = pause;
    }

    public void synchronize(DbBotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public Base getBase() {
        return base;
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
                        if (!pause) {
                            doItemBalance();
                        }
                        Thread.sleep(botConfig.getActionDelay());
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
            log.info("Bot " + botConfig.getUser().getName() + ": Killed item " + deadItem.getSyncBaseItem());
        }

        // Recreate dead items
        Map<BaseItemType, List<SyncBaseItem>> availableItems = itemService.getItems4Base(base.getSimpleBase());
        for (ItemPosAndType deadItem : deadItems) {
            baseExecutor.doBalanceItemType(availableItems, deadItem.getBaseItemType(), deadItem.getPosition());
        }

        // Insert new items
        List<SyncBaseItem> aliveItems = baseBalance.getAliveItems();
        ArrayList<SyncBaseItem> newItems = new ArrayList<SyncBaseItem>(base.getItems());
        newItems.removeAll(aliveItems);
        baseBalance.addSyncBaseItems(newItems);
    }
}
