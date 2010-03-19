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
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
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
    public static final int BASE_MIN_RANGE = 300;
    public static final int BASE_MAX_RANGE = 600;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private ConnectionService connectionService;
    private Log log = LogFactory.getLog(BotServiceImpl.class);
    private final ArrayList<Bot> bots = new ArrayList<Bot>();
    private BotConfig botConfig = new BotConfig();
    private List<SimpleBase> botBases = new ArrayList<SimpleBase>();

    @Override
    public void onHumanBaseCreated(Base base) {
        createBot(base);
    }

    @Override
    public void onHumanBaseDefeated(Base base) {
        stopBotForEnemyBase(base);
    }

    @Override
    public void onConnectionClosed(Base base) {
        stopBotForEnemyBase(base);
    }

    private void stopBotForEnemyBase(Base humanBase) {
        synchronized (bots) {
            for (Iterator<Bot> it = bots.iterator(); it.hasNext();) {
                Bot bot = it.next();
                if (bot.getHumanBase().equals(humanBase.getSimpleBase())) {
                    botBases.remove(bot.getBotBase());
                    it.remove();
                    bot.stop();
                    connectionService.sendOnlineBasesUpdate();                    
                    return;
                }
            }
        }
    }

    private void createBot(final Base humanBase) {
        Thread botThread = new Thread() {

            @Override
            public void run() {
                try {
                    Thread.sleep(botConfig.getBotStartDelay());
                    if (humanBase.isAbandoned()) {
                        return;
                    }
                    SyncBaseItem enemyItem = getEnemyItemToPlaceBotBase(humanBase);
                    if (enemyItem == null) {
                        log.error("Can not create bot. Enemy does not have enough items: " + humanBase);
                        return;
                    }
                    Base botBase = baseService.createNewBotBase(enemyItem, BASE_MIN_RANGE, BASE_MAX_RANGE);
                    Bot bot = new Bot(botBase, humanBase, serverServices, this);
                    synchronized (bots) {
                        bots.add(bot);
                        botBases.add(bot.getBotBase());
                    }
                    connectionService.sendOnlineBasesUpdate();
                    while (bot.isRunning()) {
                        bot.action();
                        Thread.sleep(botConfig.getBotActionDelay());
                    }
                } catch (InterruptedException ignore) {
                } catch (Throwable t) {
                    log.error("", t);
                }
            }
        };
        botThread.setDaemon(true);
        botThread.start();
    }

    @Override
    public List<SimpleBase> getBotBases() {
        return botBases;
    }

    private SyncBaseItem getEnemyItemToPlaceBotBase(Base humanBase) {
        SyncBaseItem builder = null;
        for (SyncBaseItem syncBaseItem : humanBase.getItems()) {
            if (syncBaseItem.hasSyncFactory()) {
                return syncBaseItem;
            }
            if (syncBaseItem.hasSyncBuilder()) {
                builder = syncBaseItem;
            }
        }
        return builder;
    }
}
