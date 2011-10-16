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
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.collision.PlaceCanNotBeFoundException;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 21.05.2010
 * Time: 21:51:58
 */
public abstract class BotRunner {
    private enum IntervalState {
        INACTIVE,
        ACTIVE
    }

    private BotConfig botConfig;
    private SimpleBase base;
    private BotItemContainer botItemContainer;
    private IntruderHandler intruderHandler;
    private final Object syncObject = new Object();
    private IntervalState intervalState;
    private Logger log = Logger.getLogger(BotRunner.class.getName());

    protected abstract void scheduleTimer(long delay, Runnable runnable);

    protected abstract void killTimer();

    protected abstract void startBotThread(int actionDelayMs, Runnable runnable);

    protected abstract void killBotThread();

    protected abstract Services getServices();

    protected abstract void killResources();

    private class BotTicker implements Runnable {
        @Override
        public void run() {
            try {
                synchronized (syncObject) {
                    if (botItemContainer == null || intruderHandler == null) {
                        return;
                    }
                    if (base == null || !getServices().getBaseService().isAlive(base)) {
                        base = getServices().getBaseService().createBotBase(botConfig);
                    }
                    botItemContainer.buildup(base);
                    intruderHandler.handleIntruders(base);
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, "", t);
            }
        }
    }

    private class BotTimer implements Runnable {
        @Override
        public void run() {
            try {
                runBotTimer();
            } catch (Throwable t) {
                log.log(Level.SEVERE, "", t);
            }
        }
    }

    public void start(BotConfig botConfig) {
        this.botConfig = botConfig;
        if (botConfig.isIntervalBot()) {
            if (botConfig.isIntervalValid()) {
                intervalState = IntervalState.INACTIVE;
                scheduleTimer(botConfig.getMinInactiveMs(), botConfig.getMaxInactiveMs());
            } else {
                log.warning("Bot has invalid interval configuration: " + botConfig.getName());
            }
        } else {
            startBot();
        }
    }

    public void kill() {
        killTimer();
        killBot();
        killResources();
    }

    public boolean isBuildup() {
        synchronized (syncObject) {
            return botItemContainer != null && botItemContainer.isFulfilled(base);
        }
    }

    public SimpleBase getBase() {
        return base;
    }

    public boolean isInRealm(Index point) {
        return botConfig.getRealm().contains(point);
    }

    private void killBot() {
        synchronized (syncObject) {
            killBotThread();
            if (botItemContainer != null) {
                botItemContainer.killAllItems();
            }
            botItemContainer = null;
            intruderHandler = null;
        }
    }


    private void startBot() {
        synchronized (syncObject) {
            botItemContainer = new BotItemContainer(botConfig.getBotItems(), getServices());
            intruderHandler = new IntruderHandler(botItemContainer, botConfig.getRealm(), getServices());
        }
        startBotThread(botConfig.getActionDelay(), new BotTicker());
    }

    protected void runBotTimer() {
        try {
            switch (intervalState) {
                case INACTIVE:
                    startBot();
                    intervalState = IntervalState.ACTIVE;
                    scheduleTimer(botConfig.getMinActiveMs(), botConfig.getMaxActiveMs());
                    break;
                case ACTIVE:
                    killBot();
                    intervalState = IntervalState.INACTIVE;
                    scheduleTimer(botConfig.getMinInactiveMs(), botConfig.getMaxInactiveMs());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown intervalState: " + intervalState);
            }
        } catch (Throwable t) {
            log.log(Level.SEVERE, "", t);
        }
    }

    private void scheduleTimer(long min, long max) {
        Random random = new Random();
        long delay = min + (long) (random.nextDouble() * (double) (max - min));
        scheduleTimer(delay, new BotTimer());
    }
}
