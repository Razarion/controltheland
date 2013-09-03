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
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

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
    private BotEnragementState botEnragementState;
    private IntruderHandler intruderHandler;
    private final Object syncObject = new Object();
    private IntervalState intervalState;
    private Logger log = Logger.getLogger(BotRunner.class.getName());

    protected abstract void scheduleTimer(long delay, Runnable runnable);

    protected abstract void killTimer();

    protected abstract void startBotThread(int actionDelayMs, Runnable runnable);

    protected abstract void killBotThread();

    protected abstract PlanetServices getPlanetServices();

    protected abstract void killResources();

    protected BotEnragementState.Listener getEnragementStateListener() {
        return null;
    }

    private class BotTicker implements Runnable {
        @Override
        public void run() {
            try {
                synchronized (syncObject) {
                    if (botEnragementState == null || intruderHandler == null) {
                        return;
                    }
                    createBaseIfNeeded();
                    botEnragementState.work(base);
                    createBaseIfNeeded();
                    intruderHandler.handleIntruders(base);
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, "Exception in BotRunner (BotTicker): " + botConfig.getName(), t);
            }
        }
    }

    private void createBaseIfNeeded() {
        if (base == null || !getPlanetServices().getBaseService().isAlive(base)) {
            base = getPlanetServices().getBaseService().createBotBase(botConfig);
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

    protected BotRunner(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public void start() {
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

    /**
     * Only used for test purpose
     *
     * @return true if fulfilled
     */
    public boolean isBuildupUseInTestOnly() {
        synchronized (syncObject) {
            return botEnragementState != null && botEnragementState.isFulfilledUseInTestOnly(base);
        }
    }

    public SimpleBase getBase() {
        return base;
    }

    public boolean isInRealm(Index point) {
        return botConfig.getRealm().isInsideAbsolute(point);
    }

    public void onBotItemKilled(SyncBaseItem syncBaseItem, SimpleBase actor) {
        if (botEnragementState != null) {
            // Timer bot is may inactive
            botEnragementState.onBotItemKilled(syncBaseItem, actor);
        }
    }

    private void killBot() {
        synchronized (syncObject) {
            killBotThread();
            if (botEnragementState != null) {
                botEnragementState.killAllItems(base);
            }
            botEnragementState = null;
            intruderHandler = null;
        }
    }


    private void startBot() {
        synchronized (syncObject) {
            botEnragementState = new BotEnragementState(botConfig.getBotEnragementStateConfigs(), botConfig.getRealm(), getPlanetServices(), botConfig.getName(), getEnragementStateListener());
            intruderHandler = new IntruderHandler(botEnragementState, botConfig.getRealm(), getPlanetServices());
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
            log.log(Level.SEVERE, "Exception in BotRunner (runBotTimer): " + botConfig.getName(), t);
        }
    }

    private void scheduleTimer(long min, long max) {
        Random random = new Random();
        long delay = min + (long) (random.nextDouble() * (double) (max - min));
        scheduleTimer(delay, new BotTimer());
    }

    protected BotConfig getBotConfig() {
        return botConfig;
    }
}
