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

package com.btxtech.game.services.base.impl;

import com.btxtech.game.jsre.client.AlreadyUsedException;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.common.AccountBalancePacket;
import com.btxtech.game.jsre.common.BaseChangedPacket;
import com.btxtech.game.jsre.common.EnergyPacket;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.XpBalancePacket;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseColor;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.base.GameFullException;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.energy.impl.BaseEnergy;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.market.ServerMarketService;
import com.btxtech.game.services.market.impl.UserItemTypeAccess;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.mgmt.StartupData;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * User: beat
 * Date: May 31, 2009
 * Time: 8:15:53 PM
 */
public class BaseServiceImpl extends AbstractBaseServiceImpl implements BaseService {
    private static final String DEFAULT_BASE_NAME_PREFIX = "Base ";
    private Log log = LogFactory.getLog(BaseServiceImpl.class);
    @Autowired
    private Session session;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServerMarketService serverMarketService;
    @Autowired
    private ServerEnergyService serverEnergyService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private BotService botService;
    private final HashMap<SimpleBase, Base> bases = new HashMap<SimpleBase, Base>();
    private int lastBaseId = 0;
    private HibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }


    @Override
    public void checkBaseAccess(SyncBaseItem item) throws IllegalAccessException {
        if (!getBase().getSimpleBase().equals(item.getBase())) {
            throw new IllegalAccessException("Invalid access from base: " + getBaseName(item.getBase()));
        }
    }

    @Override
    public void checkCanBeAttack(SyncBaseItem victim) {
        if (victim.getBase().equals(getBase().getSimpleBase())) {
            throw new IllegalArgumentException("The Item: " + victim + " can not be attacked be the base: " + getBase());
        }
    }

    private Base createNewBase() throws AlreadyUsedException, NoSuchItemTypeException, GameFullException {
        synchronized (bases) {
            List<BaseColor> baseColors = getFreeBaseColors(0, 1);
            if (baseColors.isEmpty()) {
                throw new GameFullException();
            }
            return createNewBase(baseColors.get(0));
        }
    }

    @Override
    public Base continueOrCreateBase() throws AlreadyUsedException, NoSuchItemTypeException, GameFullException {
        if (userService.isLoggedin()) {
            Base base = getBaseForLoggedInUser();
            if (base != null) {
                continueBase();
                return base;
            }
        }
        return createNewBase();
    }

    private Base createNewBase(BaseColor baseColor) throws AlreadyUsedException, NoSuchItemTypeException {
        Base base;
        ItemType constructionVehicle = itemService.getItemType(Constants.CONSTRUCTION_VEHICLE);
        synchronized (bases) {
            lastBaseId++;
            base = new Base(baseColor, userService.getLoggedinUser(), lastBaseId);
            createBase(base.getSimpleBase(), setupBaseName(base), base.getBaseColor().getHtmlColor(), base.isBot(), false);
            log.info("Base created: " + base);
            base.setAccountBalance(mgmtService.getStartupData().getStartMoney());
            bases.put(base.getSimpleBase(), base);
        }
        userGuidanceService.setupLevel4NewBase(base);
        base.setUser(userService.getLoggedinUser());
        sendBaseChangedPacket(BaseChangedPacket.Type.CREATED, base.getSimpleBase());
        connectionService.createConnection(base);
        base.setUserItemTypeAccess(serverMarketService.getUserItemTypeAccess());
        StartupData startupData = mgmtService.getStartupData();
        Index startPoint = collisionService.getFreeRandomPosition(constructionVehicle, startupData.getStartRectangle(), startupData.getStartItemFreeRange());
        SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.createSyncObject(constructionVehicle, startPoint, null, base.getSimpleBase(), 0);
        syncBaseItem.setBuildup(1.0);
        syncBaseItem.getSyncTurnable().setAngel(Math.PI / 4.0); // Cosmetis shows vehicle from side
        historyService.addBaseStartEntry(base.getSimpleBase());
        if (userService.getLoggedinUser() != null) {
            userTrackingService.onBaseCreated(userService.getLoggedinUser(), base);
        }
        return base;
    }

    private void continueBase() {
        Base base = getBaseForLoggedInUser();
        if (base == null) {
            throw new IllegalStateException("User does not have any running base");
        }
        connectionService.createConnection(base);
        base.setUserItemTypeAccess(serverMarketService.getUserItemTypeAccess());
    }

    private void deleteBase(Base base) {
        log.info("Base deleted: " + base);
        synchronized (bases) {
            if (bases.remove(base.getSimpleBase()) == null) {
                throw new IllegalArgumentException("Base does not exist: " + getBaseName(base.getSimpleBase()));
            }
            sendBaseChangedPacket(BaseChangedPacket.Type.REMOVED, base.getSimpleBase());
            removeBase(base.getSimpleBase());
            serverEnergyService.onBaseKilled(base);
        }
        userGuidanceService.onBaseDeleted(base);
    }

    @Override
    public Base getBase() {
        Connection connection = session.getConnection();
        if (connection == null) {
            throw new NoConnectionException("No connection", session.getSessionId());
        }
        Base base = connection.getBase();
        if (base == null) {
            throw new NoConnectionException("Base does not exist", session.getSessionId());
        }
        return base;
    }

    @Override
    public Base getBase(SyncBaseItem baseSyncItem) {
        Base base = bases.get(baseSyncItem.getBase());
        if (base == null) {
            throw new IllegalArgumentException("Base does not exist: " + baseSyncItem.getBase());
        }
        return base;
    }

    @Override
    public Base getBase(SimpleBase simpleBase) {
        return bases.get(simpleBase);
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<String> getFreeColors(final int index, final int count) {
        ArrayList<String> colors = new ArrayList<String>();
        for (BaseColor baseColor : getFreeBaseColors(index, count)) {
            colors.add(baseColor.getHtmlColor());
        }
        return colors;
    }

    private BaseColor getBaseColor4HtmlColor(final String htmlColor) {
        @SuppressWarnings("unchecked")
        List<BaseColor> baseColors = (List<BaseColor>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(org.hibernate.Session session) {
                Criteria criteria = session.createCriteria(BaseColor.class);
                criteria.add(Restrictions.eq("htmlColor", htmlColor));
                return criteria.list();
            }
        });
        if (baseColors.isEmpty()) {
            throw new IllegalArgumentException("HTML color does not exist: " + htmlColor);
        }
        return baseColors.get(0);
    }


    @SuppressWarnings("unchecked")
    private List<BaseColor> getFreeBaseColors(final int index, final int count) {
        return (List<BaseColor>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(org.hibernate.Session session) {
                Criteria criteria = session.createCriteria(BaseColor.class);
                Collection<String> colorsUsed = getHtmlColors();
                if (!colorsUsed.isEmpty()) {
                    criteria.add(Restrictions.not(Restrictions.in("htmlColor", colorsUsed)));
                }
                criteria.setMaxResults(count);
                criteria.setFirstResult(index);
                criteria.addOrder(Order.asc("id"));
                return criteria.list();
            }
        });
    }

    public void fillBaseColors() {
        final ArrayList<BaseColor> list = new ArrayList<BaseColor>();
        for (int red = 1; red < 5; red++) {
            for (int green = 1; green < 5; green++) {
                for (int blue = 1; blue < 5; blue++) {
                    BaseColor baseColor = new BaseColor(red * 64 - 1, green * 64 - 1, blue * 64 - 1);
                    list.add(baseColor);
                }
            }
        }

        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(org.hibernate.Session session) {
                while (!list.isEmpty()) {
                    int randIndex = (int) (Math.random() * list.size());
                    BaseColor baseColor = list.remove(randIndex);
                    session.saveOrUpdate(baseColor);

                }
                return null;
            }
        });
    }

    @Override
    public void itemCreated(SyncBaseItem syncItem) {
        Base base = getBase(syncItem);
        base.addItem(syncItem);
    }

    @Override
    public void itemDeleted(SyncBaseItem syncItem, SyncBaseItem actor) {
        Base base = getBase(syncItem);
        base.removeItem(syncItem);
        if (!base.hasItems()) {
            if (actor != null) {
                historyService.addBaseDefeatedEntry(actor, base.getSimpleBase());
                sendDefeatedMessage(syncItem, actor);
            }
            if (base.getUser() != null) {
                userTrackingService.onBaseDefeated(base.getUser(), base);
            }
            deleteBase(base);
        }
    }

    private void sendDefeatedMessage(SyncBaseItem victim, SyncBaseItem actor) {
        Message message = new Message();
        message.setTitle("Game over!");
        message.setMessage("You have been defeated by " + getBaseName(actor.getBase()));
        connectionService.sendPacket(victim.getBase(), message);
        message = new Message();
        message.setTitle("Congratulations!");
        message.setMessage("You defeated " + getBaseName(victim.getBase()));
        connectionService.sendPacket(actor.getBase(), message);
    }

    @Override
    public void sendPackage(Packet packet) {
        Base base = getBase();
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendAccountBaseUpdate(SyncBaseItem syncItem) {
        Base base = getBase(syncItem);
        AccountBalancePacket packet = new AccountBalancePacket();
        packet.setAccountBalance(base.getAccountBalance());
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendAccountBaseUpdate(Base base) {
        AccountBalancePacket packet = new AccountBalancePacket();
        packet.setAccountBalance(base.getAccountBalance());
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendXpUpdate(UserItemTypeAccess userItemTypeAccess, Base base) {
        XpBalancePacket packet = new XpBalancePacket();
        packet.setXp(userItemTypeAccess.getXp());
        connectionService.sendPacket(base.getSimpleBase(), packet);
    }

    @Override
    public void sendEnergyUpdate(BaseEnergy baseEnergy, Base base) {
        EnergyPacket packt = new EnergyPacket();
        packt.setConsuming(baseEnergy.getConsuming());
        packt.setGenerating(baseEnergy.getGenerating());
        connectionService.sendPacket(base.getSimpleBase(), packt);
    }

    @Override
    public Base getBase(User user) {
        synchronized (bases) {
            for (Base base : bases.values()) {
                if (user.equals(base.getUser())) {
                    return base;
                }
            }
        }
        return null;
    }

    @Override
    public Base getBaseForLoggedInUser() {
        User user = userService.getLoggedinUser();
        if (user == null) {
            return null;
        }
        return getBase(user);
    }

    @Override
    public User getUser(SimpleBase simpleBase) {
        Base base = getBase(simpleBase);
        return base.getUser();
    }

    @Override
    public void surrenderBase(Base base) {
        if (base.getUser() != null) {
            historyService.addBaseSurrenderedEntry(base.getSimpleBase());
            userTrackingService.onBaseSurrender(base.getUser(), base);
        }
        setBaseAbandoned(base.getSimpleBase(), true);
        base.setUser(null);
        setBaseName(base.getSimpleBase(), setupBaseName(base));
        base.setAbandoned(true);
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, base.getSimpleBase());
        connectionService.sendOnlineBasesUpdate();
    }

    @Override
    public List<Base> getBases() {
        return new ArrayList<Base>(bases.values());
    }

    @Override
    @Deprecated
    public List<Base> getBasesNoDummy() {
        return getBases();
    }

    @Override
    public List<SimpleBase> getSimpleBases() {
        ArrayList<SimpleBase> simpleBases = new ArrayList<SimpleBase>();
        for (Base base : bases.values()) {
            simpleBases.add(base.getSimpleBase());
        }
        return simpleBases;
    }

    @Override
    public void restoreBases(Collection<Base> newBases) {
        synchronized (bases) {
            bases.clear();
            lastBaseId = 0;
            clear();
            for (Base newBase : newBases) {
                bases.put(newBase.getSimpleBase(), newBase);
                createBase(newBase.getSimpleBase(), setupBaseName(newBase), newBase.getBaseColor().getHtmlColor(), newBase.isBot(), newBase.isAbandoned());
                if (newBase.getBaseId() > lastBaseId) {
                    lastBaseId = newBase.getBaseId();
                }
            }
        }
    }

    private String setupBaseName(Base base) {
        if (base.getUser() != null) {
            return base.getUser().getName();
        } else {
            return DEFAULT_BASE_NAME_PREFIX + base.getBaseId();
        }
    }

    @Override
    public void depositResource(double price, SimpleBase simpleBase) {
        Base base = getBase(simpleBase);
        if (!base.isBot()) {
            base.depositMoney(price);
            userGuidanceService.onMoneyIncrease(base);
        }
    }

    @Override
    public void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException {
        Base base = getBase(simpleBase);
        if (!base.isBot()) {
            base.withdrawalMoney(price);
        }
    }

    private void sendBaseChangedPacket(BaseChangedPacket.Type type, SimpleBase simpleBase) {
        BaseAttributes baseAttributes = getBaseAttributes(simpleBase);
        if (baseAttributes != null) {
            BaseChangedPacket baseChangedPacket = new BaseChangedPacket();
            baseChangedPacket.setType(type);
            baseChangedPacket.setBaseAttributes(baseAttributes);
            connectionService.sendPacket(baseChangedPacket);
        } else {
            log.error("Base does not exist: " + simpleBase);
        }
    }

    @Override
    public void onUserRegistered(User user) {
        Base base = getBase();
        base.setUser(user);
        setBaseName(base.getSimpleBase(), setupBaseName(base));
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, base.getSimpleBase());
        connectionService.sendOnlineBasesUpdate();
    }

    @Override
    public void setBaseColor(String color) throws AlreadyUsedException {
        BaseColor baseColor = getBaseColor4HtmlColor(color);
        Base base = getBase();
        base.setBaseColor(baseColor);
        hibernateTemplate.save(base);
        SimpleBase simpleBase = base.getSimpleBase();
        setBaseColor(simpleBase, color);
        sendBaseChangedPacket(BaseChangedPacket.Type.CHANGED, simpleBase);
        connectionService.sendOnlineBasesUpdate();
    }
}
