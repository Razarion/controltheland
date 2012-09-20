package com.btxtech.game.jsre.client.bot;

import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotRunner;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.user.client.Timer;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 12.10.2011
 * Time: 13:46:17
 */
public class ClientBotRunner extends BotRunner {
    private Timer botThread;
    private Timer botTimer;
    private Logger log = Logger.getLogger(ClientBotRunner.class.getName());

    public ClientBotRunner(BotConfig botConfig) {
        super(botConfig);
    }

    @Override
    protected void scheduleTimer(long delay, final Runnable runnable) {
        killTimer();
        botThread = new TimerPerfmon(PerfmonEnum.BOT_RUNNER) {
            @Override
            public void runPerfmon() {
                runnable.run();
            }
        };
        botThread.schedule((int) delay);
    }

    @Override
    protected void killTimer() {
        if (botTimer != null) {
            botTimer.cancel();
            botTimer = null;
        }
    }

    @Override
    protected void startBotThread(int actionDelayMs, final Runnable runnable) {
        if (botThread != null) {
            log.severe("Bot thread was not stopped before: " + getBotConfig());
            killBotThread();
        }
        botThread = new TimerPerfmon(PerfmonEnum.BOT_THREAD) {
            @Override
            public void runPerfmon() {
                runnable.run();
            }
        };
        botThread.scheduleRepeating(actionDelayMs);
    }

    @Override
    protected void killBotThread() {
        if (botThread != null) {
            botThread.cancel();
            botThread = null;
        }
    }

    @Override
    protected PlanetServices getPlanetServices() {
        return ClientPlanetServices.getInstance();
    }

    @Override
    protected void killResources() {
        killTimer();
        killBotThread();
    }
}
