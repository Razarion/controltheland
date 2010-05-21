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
import com.btxtech.game.jsre.common.bot.BaseExecutor;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.bot.DbBotConfig;
import com.btxtech.game.services.common.ServerServices;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.user.UserService;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserService userService;
    @Autowired
    private ServerServices serverServices;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ConnectionService connectionService;
    private HibernateTemplate hibernateTemplate;
    private BaseBalance baseBalance;
    private Thread botThread;
    private Log log = LogFactory.getLog(BotServiceImpl.class);
    private BaseExecutor baseExecutor;
    private DbBotConfig dbBotConfig = new DbBotConfig();
    private Base botBase;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void addDbBotConfig() {
        hibernateTemplate.save(new DbBotConfig());
    }

    @Override
    public void saveDbBotConfig(List<DbBotConfig> dbLevels) {
        hibernateTemplate.saveOrUpdateAll(dbLevels);
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
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void start() {
        try {
            DbBotConfig dbBotConfig = null;
            if (dbBotConfig == null || dbBotConfig.getUser() == null) {
                return;
            }
            botBase = baseService.getBase(dbBotConfig.getUser());
            if (botBase == null) {
                log.info("Can not start bot. No base found for " + dbBotConfig.getUser().getName());
                return;
            }
            botBase.setBot(true);
            baseExecutor = new BaseExecutor(serverServices, botBase.getSimpleBase());
            baseBalance = new BaseBalance(baseExecutor);
            for (SyncBaseItem syncBaseItem : botBase.getItems()) {
                baseBalance.addItemPosAndType(new ItemPosAndType(syncBaseItem));
            }
            runBot();
            connectionService.sendOnlineBasesUpdate();
        } catch (Throwable t) {
            log.error("", t);
        }
    }

    private void stop() {
        if (botThread == null) {
            throw new IllegalStateException("Bot thread is not running");
        }
        Thread tmp = botThread;
        botThread = null;
        tmp.interrupt();
        connectionService.sendOnlineBasesUpdate();
    }

    private void runBot() {
        if (botThread != null) {
            throw new IllegalStateException("Bot is already running");
        }
        if (baseBalance.isEmpty()) {
            throw new IllegalStateException("Base does not have any items");
        }

        botThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (botThread != null) {
                        doItemBalance();
                        Thread.sleep(dbBotConfig.getActionDelay());
                    }
                } catch (InterruptedException ignore) {
                    botThread = null;
                } catch (Throwable t) {
                    log.error("", t);
                    botThread = null;
                }
            }
        };
        botThread.setDaemon(true);
        botThread.start();
    }

    private void doItemBalance() {
        // Get Dead items
        List<ItemPosAndType> deadItems = baseBalance.getDeadItems();
        for (ItemPosAndType deadItem : deadItems) {
            log.info("Bot: Killed item " + deadItem.getSyncBaseItem());
        }

        // Recreate dead items
        Map<BaseItemType, List<SyncBaseItem>> availableItems = itemService.getItems4Base(botBase.getSimpleBase());
        for (ItemPosAndType deadItem : deadItems) {
            baseExecutor.doBalanceItemType(availableItems, deadItem.getBaseItemType(), deadItem.getPosition());
        }

        // Insert new items
        List<SyncBaseItem> aliveItems = baseBalance.getAliveItems();
        ArrayList<SyncBaseItem> newItems = new ArrayList<SyncBaseItem>(botBase.getItems());
        newItems.removeAll(aliveItems);
        baseBalance.addSyncBaseItems(newItems);
    }

    @Override
    public SimpleBase getOnlineBotBase() {
        if (botThread != null) {
            return botBase.getSimpleBase();
        } else {
            return null;
        }
    }

    @Override
    public void onConnectionClosed(Base base) {
        if (base.equals(botBase)) {
            start();
        }
    }

    @Override
    public void onConnectionCreated(Base base) {
        if (base.equals(botBase)) {
            stop();
        }
    }
}
