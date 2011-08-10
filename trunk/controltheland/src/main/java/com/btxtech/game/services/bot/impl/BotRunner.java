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
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private BaseService baseService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationContext applicationContext;
    private DbBotConfig botConfig;
    private Base base;
    private Thread botThread;
    private Log log = LogFactory.getLog(BotRunner.class);
    private BotItemContainer botItemContainer;
    private IntruderHandler intruderHandler;
    private UserState userState;
    private final Object syncObject = new Object();

    public void start(DbBotConfig botConfig) {
        this.botConfig = botConfig;
        userState = userService.getUserState(botConfig);
        checkBase();
        runBot();
    }

    public void kill() {
        if (botThread == null) {
            throw new IllegalStateException("Bot thread is not running");
        }
        Thread tmp = botThread;
        botThread = null;
        tmp.interrupt();

        synchronized (syncObject) {
            if (botItemContainer != null) {
                botItemContainer.killAllItems();
            }
        }
        userService.deleteUserState(botConfig);
    }

    public boolean isBuildup() {
        synchronized (syncObject) {
            return botItemContainer != null && botItemContainer.isFulfilled(userState);
        }
    }

    public Base getBase() {
        return base;
    }

    public boolean isInRealm(Index point) {
        return botConfig.getRealm().contains(point);
    }

    private void setupBot() {
        synchronized (syncObject) {
            botItemContainer = (BotItemContainer) applicationContext.getBean("botItemContainer");
            botItemContainer.init(botConfig.getBotItemCrud().readDbChildren());
            intruderHandler = (IntruderHandler) applicationContext.getBean("intruderHandler");
            intruderHandler.init(botItemContainer, botConfig.getRealm());
        }
        if (base != null && baseService.isAlive(base.getSimpleBase())) {
            baseService.changeBotBaseName(base, botConfig.getName());
        }
    }

    private void checkBase() {
        if (base == null || !baseService.isAlive(base.getSimpleBase())) {
            base = baseService.getBase(userState);
            if (base == null) {
                base = baseService.createBotBase(userState, botConfig.getName());
            }
        }
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
                            synchronized (syncObject) {
                                botItemContainer.buildup(base.getSimpleBase(), userState);
                                intruderHandler.handleIntruders(base.getSimpleBase());
                            }
                            Thread.sleep(botConfig.getActionDelay());
                        } catch (InterruptedException e) {
                            throw e;
                        } catch (Exception e) {
                            log.error("Bot " + botConfig.getName() + ": " + e.getMessage());
                        }
                    }
                } catch (InterruptedException e) {
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
}
