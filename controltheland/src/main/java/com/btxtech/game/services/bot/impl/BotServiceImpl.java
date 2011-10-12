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

import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.BotRunner;
import com.btxtech.game.jsre.common.gameengine.services.bot.impl.CommonBotServiceImpl;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.item.ItemService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:18:11
 */
@Component(value = "botService")
public class BotServiceImpl extends CommonBotServiceImpl implements BotService {
    @Autowired
    private CrudRootServiceHelper<DbBotConfig> dbBotConfigCrudServiceHelper;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ServerServices serverServices;
    private Log log = LogFactory.getLog(BotServiceImpl.class);
    private Map<Integer, BotConfig> simulatedBotConfigs = new HashMap<Integer, BotConfig>();

    // TODO save & restore

    private void fillBotConfigs() {
        simulatedBotConfigs.clear();
        Collection<BotConfig> realGameBotConfigs = new ArrayList<BotConfig>();
        for (DbBotConfig botConfig : dbBotConfigCrudServiceHelper.readDbChildren()) {
            try {
                if (botConfig.isRealGameBot()) {
                    realGameBotConfigs.add(botConfig.createBotConfig(itemService));
                } else {
                    simulatedBotConfigs.put(botConfig.getId(), botConfig.createBotConfig(itemService));
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        setBotConfigs(realGameBotConfigs);
    }


    @PostConstruct
    public void init() {
        dbBotConfigCrudServiceHelper.init(DbBotConfig.class);
    }

    @Override
    public CrudRootServiceHelper<DbBotConfig> getDbBotConfigCrudServiceHelper() {
        return dbBotConfigCrudServiceHelper;
    }

    @Override
    public void activate() {
        killAllBots();
        fillBotConfigs();
        startAllBots();
    }

    @PreDestroy
    @Override
    public void cleanup() {
        killAllBots();
    }

    @Override
    public BotConfig getSimulationBotConfig(int id) {
        return simulatedBotConfigs.get(id);
    }

    @Override
    protected BotRunner createBotRunner() {
       return new ServerBotRunner(serverServices);
    }
}
