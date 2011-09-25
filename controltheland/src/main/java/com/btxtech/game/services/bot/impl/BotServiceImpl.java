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

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.bot.DbBotItemConfig;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:18:11
 */
@Component(value = "botService")
public class BotServiceImpl implements BotService {
    @Autowired
    private CrudRootServiceHelper<DbBotConfig> dbBotConfigCrudServiceHelper;
    @Autowired
    private ApplicationContext applicationContext;
    final private Map<DbBotConfig, BotRunner> botRunners = new HashMap<DbBotConfig, BotRunner>();
    private Log log = LogFactory.getLog(BotServiceImpl.class);

    // TODO save & restore

    @PostConstruct
    public void init() {
        dbBotConfigCrudServiceHelper.init(DbBotConfig.class);
    }

    @Override
    public void start() {
        for (DbBotConfig botConfig : dbBotConfigCrudServiceHelper.readDbChildren()) {
            try {
                hibernateInitialize(botConfig);
                Hibernate.initialize(botConfig);
                Hibernate.initialize(botConfig.getBotItemCrud().readDbChildren());
                startBot(botConfig);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private void hibernateInitialize(DbBotConfig botConfig) {
        Hibernate.initialize(botConfig);
        for (DbBotItemConfig dbBotItemConfig : botConfig.getBotItemCrud().readDbChildren()) {
            Hibernate.initialize(dbBotItemConfig);
            Hibernate.initialize(dbBotItemConfig.getBaseItemType());
        }
    }

    @Override
    public CrudRootServiceHelper<DbBotConfig> getDbBotConfigCrudServiceHelper() {
        return dbBotConfigCrudServiceHelper;
    }

    private void startBot(DbBotConfig botConfig) {
        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.start(botConfig);
        synchronized (botRunners) {
            botRunners.put(botConfig, botRunner);
        }
    }

    @Override
    public void activate() {
        destroy();
        start();
    }

    @PreDestroy
    public void destroy() {
        // Kill all bots
        for (BotRunner botRunner : botRunners.values()) {
            botRunner.kill();
        }
        botRunners.clear();
    }

    public BotRunner getBotRunner(DbBotConfig dbBotConfig) {
        return botRunners.get(dbBotConfig);
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
