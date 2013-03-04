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
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotEnragementStateConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotEnragementState;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotRunner;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.CommonBotServiceImpl;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.db.DbPlanet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:18:11
 */
public class BotServiceImpl extends CommonBotServiceImpl implements BotService {
    private PlanetServices planetServices;
    private ServerGlobalServices serverGlobalServices;
    private Log log = LogFactory.getLog(BotServiceImpl.class);

    public void init(ServerPlanetServices planetServices, ServerGlobalServices serverGlobalServices) {
        this.planetServices = planetServices;
        this.serverGlobalServices = serverGlobalServices;
    }

    @Override
    public void activate(DbPlanet dbPlanet) {
        killAllBots();
        fillBotConfigs(dbPlanet);
        startAllBots();
    }

    @Override
    public void deactivate() {
        try {
            killAllBots();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    @Override
    public void reactivate(DbPlanet dbPlanet) {
        deactivate();
        activate(dbPlanet);
    }

    private void fillBotConfigs(DbPlanet dbPlanet) {
        Collection<BotConfig> realGameBotConfigs = new ArrayList<>();
        for (DbBotConfig botConfig : dbPlanet.getBotCrud().readDbChildren()) {
            try {
                realGameBotConfigs.add(botConfig.createBotConfig(serverGlobalServices.getItemTypeService()));
            } catch (Exception e) {
                log.error("", e);
            }
        }
        setBotConfigs(realGameBotConfigs);
    }

    @Override
    protected BotRunner createBotRunner(BotConfig botConfig) {
        return new ServerBotRunner(botConfig, planetServices, new BotEnragementState.Listener() {
            @Override
            public void onEnrageNormal(String botName, BotEnragementStateConfig botEnragementStateConfig) {
                serverGlobalServices.getHistoryService().addBotEnrageNormal(botName, botEnragementStateConfig);
            }

            @Override
            public void onEnrageUp(String botName, BotEnragementStateConfig botEnragementStateConfig, SimpleBase actor) {
                serverGlobalServices.getHistoryService().addBotEnrageUp(botName, botEnragementStateConfig, actor);
            }
        });
    }
}
