package com.btxtech.game.services.bot.impl;

import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: beat
 * Date: 10.10.2011
 * Time: 22:56:44
 */
public class ServerBotRunner extends BotRunner {
    private Services services;
    private ScheduledThreadPoolExecutor botThread;
    private ScheduledThreadPoolExecutor botTimer;
    private ScheduledFuture botThreadScheduledFuture;
    private Log log = LogFactory.getLog(ServerBotRunner.class);

    public ServerBotRunner(BotConfig botConfig, Services services) {
        super(botConfig);
        this.services = services;
        botThread = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("BotRunner botThread: " + botConfig.getName() + " "));
        botTimer = new ScheduledThreadPoolExecutor(1, new CustomizableThreadFactory("BotRunner botTimer: " + botConfig.getName() + " "));
    }

    @Override
    protected void scheduleTimer(long delay, Runnable runnable) {
        botTimer.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void killTimer() {
        if (botTimer != null) {
            botTimer.shutdownNow();
            botTimer = null;
        }
    }

    @Override
    protected void startBotThread(int actionDelayMs, Runnable runnable) {
        if (botThreadScheduledFuture != null) {
            log.warn("Bot is already running: " + getBotConfig());
            killBotThread();
        }
        botThreadScheduledFuture = botThread.scheduleAtFixedRate(runnable, 0, actionDelayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void killResources() {
        killTimer();
        killBotThread();
        if (botThread != null) {
            botThread.shutdownNow();
            botThread = null;
        }

    }

    @Override
    protected void killBotThread() {
        if (botThreadScheduledFuture != null) {
            botThreadScheduledFuture.cancel(false);
            botThreadScheduledFuture = null;
        }
    }

    @Override
    protected Services getServices() {
        return services;
    }
}
