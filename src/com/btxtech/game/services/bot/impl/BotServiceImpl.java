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
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 14.03.2010
 * Time: 17:18:11
 */
@Component(value = "botService")
public class BotServiceImpl implements BotService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private ApplicationContext applicationContext;
    private HibernateTemplate hibernateTemplate;
    final private Map<DbBotConfig, BotRunner> botRunners = new HashMap<DbBotConfig, BotRunner>();
    private Collection<SimpleBase> simpleBases = new ArrayList<SimpleBase>();
    private Log log = LogFactory.getLog(BotServiceImpl.class);

    public void start() {
        for (DbBotConfig botConfig : getDbBotConfigs()) {
            try {
                startBot(botConfig);
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void addDbBotConfig() {
        DbBotConfig dbBotConfig = new DbBotConfig();
        dbBotConfig.setActionDelay(3000);
        hibernateTemplate.save(dbBotConfig);
    }

    @Override
    public void saveDbBotConfig(List<DbBotConfig> dbLevels) {
        hibernateTemplate.saveOrUpdateAll(dbLevels);
        refreshBotRunners();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DbBotConfig> getDbBotConfigs() {
        return (List<DbBotConfig>) hibernateTemplate.executeFind(new HibernateCallback() {
            @Override
            public Object doInHibernate(org.hibernate.Session session) throws HibernateException, SQLException {
                Criteria criteria = session.createCriteria(DbBotConfig.class);
                criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                return criteria.list();
            }
        });
    }

    @Override
    public void removeDbBotConfig(DbBotConfig dbBotConfig) {
        hibernateTemplate.delete(dbBotConfig);
        stopBot(dbBotConfig);
    }

    private void startBot(DbBotConfig botConfig) {
        if (botConfig.getUser() == null) {
            return;
        }
        Base base = baseService.getBase(botConfig.getUser());
        if (base == null) {
            log.info("Can not start bot. No base found for " + botConfig.getUser().getName());
            return;
        }
        base.setBot(true);
        BotRunner botRunner = (BotRunner) applicationContext.getBean("botRunner");
        botRunner.setBase(base);
        botRunner.setBotConfig(botConfig);
        botRunner.start();
        synchronized (botRunners) {
            botRunners.put(botConfig, botRunner);
            simpleBases.add(base.getSimpleBase());
        }
    }

    private void stopBot(DbBotConfig botConfig) {
        if (botConfig.getUser() == null) {
            return;
        }
        BotRunner botRunner;
        synchronized (botRunners) {
            botRunner = botRunners.remove(botConfig);
        }
        if (botRunner == null) {
            throw new IllegalArgumentException("Can not stop bot. No such bot " + botConfig.getUser().getName());
        }
        simpleBases.remove(botRunner.getBase().getSimpleBase());

        botRunner.stop();
        botRunner.getBase().setBot(false);
    }

    private void refreshBotRunners() {
        List<DbBotConfig> newDbBotConfigs = new ArrayList<DbBotConfig>();
        List<DbBotConfig> dbBotConfigs = getDbBotConfigs();
        for (DbBotConfig botConfig : dbBotConfigs) {
            BotRunner botRunner = botRunners.get(botConfig);
            if (botRunner != null) {
                botRunner.synchronize(botConfig);
            } else {
                newDbBotConfigs.add(botConfig);
            }
        }

        // Start new bots
        for (DbBotConfig botConfig : newDbBotConfigs) {
            try {
                startBot(botConfig);
            } catch (Exception e) {
                log.error("", e);
            }
        }

        // Remove old bots
        List<DbBotConfig> oldDbBotConfigs = new ArrayList<DbBotConfig>(botRunners.keySet());
        oldDbBotConfigs.removeAll(dbBotConfigs);
        for (DbBotConfig botConfig : oldDbBotConfigs) {
            stopBot(botConfig);
        }
    }

    @Override
    public Collection<SimpleBase> getRunningBotBases() {
        return simpleBases;
    }

    @Override
    public void onConnectionClosed(Base base) {
        BotRunner botRunner = getBotRunner(base);
        if (botRunner != null) {
            botRunner.pause(false);
        }
    }

    @Override
    public void onConnectionCreated(Base base) {
        BotRunner botRunner = getBotRunner(base);
        if (botRunner != null) {
            botRunner.pause(true);
        }
    }

    private BotRunner getBotRunner(Base base) {
        if (!simpleBases.contains(base.getSimpleBase())) {
            return null;
        }
        synchronized (botRunners) {
            for (BotRunner botRunner : botRunners.values()) {
                if (botRunner.getBase().equals(base)) {
                    return botRunner;
                }
            }
        }
        return null;
    }
}
