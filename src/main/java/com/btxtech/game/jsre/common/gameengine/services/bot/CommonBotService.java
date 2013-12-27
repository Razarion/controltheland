package com.btxtech.game.jsre.common.gameengine.services.bot;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotRunner;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

/**
 * User: beat
 * Date: 10.10.2011
 * Time: 13:26:43
 */
public interface CommonBotService {
    boolean isInRealm(Index point);

    BotRunner getBotRunner(BotConfig botConfig);

    void onBotItemKilled(SyncBaseItem syncBaseItem, SimpleBase actor);

    void killBot(int botId);
}
