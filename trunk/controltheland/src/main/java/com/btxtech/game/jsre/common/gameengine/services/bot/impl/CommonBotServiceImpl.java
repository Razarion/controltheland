package com.btxtech.game.jsre.common.gameengine.services.bot.impl;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 10.10.2011
 * Time: 13:30:00
 */
public abstract class CommonBotServiceImpl implements CommonBotService {
    private Collection<BotConfig> botConfigs;
    final private Map<BotConfig, BotRunner> botRunners = new HashMap<BotConfig, BotRunner>();

    protected abstract BotRunner createBotRunner();

    protected void startAllBots() {
        for (BotConfig botConfig : botConfigs) {
            startBot(botConfig);
        }
    }

    private void startBot(BotConfig botConfig) {
        BotRunner botRunner = createBotRunner();
        botRunner.start(botConfig);
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
