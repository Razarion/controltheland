package com.btxtech.game.jsre.common.gameengine.services.bot.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 10.10.2011
 * Time: 13:30:00
 */
public abstract class CommonBotServiceImpl implements CommonBotService {
    private Collection<BotConfig> botConfigs;
    final private Map<BotConfig, BotRunner> botRunners = new HashMap<BotConfig, BotRunner>();
    private Logger log = Logger.getLogger(CommonBotServiceImpl.class.getName());

    protected abstract BotRunner createBotRunner(BotConfig botConfig);

    protected void startAllBots() {
        for (BotConfig botConfig : botConfigs) {
            try {
                startBot(botConfig);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Starting bot failed: " + botConfig.getName(), e);
            }
        }
    }

    private void startBot(BotConfig botConfig) {
        BotRunner botRunner = createBotRunner(botConfig);
        botRunner.start();
        synchronized (botRunners) {
            botRunners.put(botConfig, botRunner);
        }
    }

    protected void killAllBots() {
        // Kill all bots
        synchronized (botRunners) {
            for (BotRunner botRunner : botRunners.values()) {
                botRunner.kill();
            }
        }
        botRunners.clear();
    }

    public void setBotConfigs(Collection<BotConfig> botConfigs) {
        this.botConfigs = botConfigs;
    }

    public BotRunner getBotRunner(BotConfig botConfig) {
        return botRunners.get(botConfig);
    }

    @Override
    public void onBotItemKilled(SyncBaseItem syncBaseItem, SimpleBase actor) {
        synchronized (botRunners) {
            for (BotRunner botRunner : botRunners.values()) {
                botRunner.onBotItemKilled(syncBaseItem, actor);
            }
        }
    }

    @Override
    public boolean isInRealm(Index point) {
        synchronized (botRunners) {
            for (BotRunner botRunner : botRunners.values()) {
                if (botRunner.isInRealm(point)) {
                    return true;
                }
            }
        }
        return false;
    }


}