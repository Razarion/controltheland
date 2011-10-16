package com.btxtech.game.jsre.client.bot;

import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotRunner;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.CommonBotServiceImpl;

/**
 * User: beat
 * Date: 12.10.2011
 * Time: 13:44:27
 */
public class ClientBotService extends CommonBotServiceImpl {
    private static final ClientBotService INSTANCE = new ClientBotService();

    public static ClientBotService getInstance() {
        return INSTANCE;
    }

    @Override
    protected BotRunner createBotRunner() {
        return new ClientBotRunner();
    }

    public void clear() {
        killAllBots();
    }

    public void start() {
        startAllBots();
    }

}
