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

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.common.AccountBalancePackt;
import com.btxtech.game.jsre.common.EnergyPacket;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.XpBalancePackt;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.services.base.AlreadyUsedException;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseColor;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.connection.Connection;
import com.btxtech.game.services.connection.ConnectionService;
import com.btxtech.game.services.connection.NoConnectionException;
import com.btxtech.game.services.connection.Session;
import com.btxtech.game.services.energy.ServerEnergyService;
import com.btxtech.game.services.energy.impl.BaseEnergy;
import com.btxtech.game.services.history.BaseHasBeenDefeated;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.history.UserEntered;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import com.btxtech.game.services.itemTypeAccess.impl.UserItemTypeAccess;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.annotation.PostConstruct;
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
public class BaseServiceImpl implements BaseService {
    public static final String DEFAULT_PLAYER_NAME_PREFIX = "Player ";
    public static final int EDGE_LENGTH = 200;
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
    private ServerItemTypeAccessService serverItemTypeAccessService;
    @Autowired
    private ServerEnergyService serverEnergyService;
    private final HashMap<String, Base> bases = new HashMap<String, Base>();
    private HashSet<String> colorsUsed = new HashSet<String>();
    private HibernateTemplate hibernateTemplate;
    private Base dummyBase;

    @PostConstruct
    public void setupDummyBase() {
        BaseColor baseColor = new BaseColor(0, 0, 0);
        dummyBase = new Base(Constants.DUMMY_BASE_NAME, baseColor, null);
        bases.put(Constants.DUMMY_BASE_NAME, dummyBase);
        colorsUsed.add(baseColor.getHtmlColor());
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }


    @Override
    public void checkBaseAccess(SyncBaseItem item) throws IllegalAccessException {
        if (!getBase().getSimpleBase().equals(item.getBase())) {
            throw new IllegalAccessException("Invalid access from base: " + item.getBase().getName());
        }
    }

    @Override
    public void checkCanBeAttack(SyncBaseItem victim) {
        if (victim.getBase().equals(getBase().getSimpleBase())) {
            throw new IllegalArgumentException("The Item: " + victim + " can not be attacked be the base: " + getBase());
        }
    }

    @Override
    public void createNewBase(String name, BaseColor baseColor) throws AlreadyUsedException, NoSuchItemTypeException {
        Base base;
        ItemType constructionVehicle = itemService.getItemType("Construction Vehicle");
        synchronized (bases) {
            if (bases.containsKey(name)) {
                throw new AlreadyUsedException(name);
            }
            if (colorsUsed.contains(baseColor.getHtmlColor())) {
                throw new AlreadyUsedException(baseColor);
            }

            base = new Base(name, baseColor, userService.getLoggedinUser());
            base.setAccountBalance(Constants.START_MONEY);
            bases.put(name, base);
            colorsUsed.add(baseColor.getHtmlColor());
        }
        base.setUser(userService.getLoggedinUser());
        connectionService.createConnection(base);
        base.setUserItemTypeAccess(serverItemTypeAccessService.getUserItemTypeAccess());
        Index startPoint = collisionService.getFreeRandomPosition(constructionVehicle, EDGE_LENGTH);
        SyncBaseItem syncBaseItem = (SyncBaseItem) itemService.createSyncObject(constructionVehicle, startPoint, null, base.getSimpleBase(), 0);
        syncBaseItem.setBuild(true);
        syncBaseItem.setFullHealth();
        syncBaseItem.getSyncTurnable().setAngel(Math.PI / 4.0); // Cosmetis shows vehicle from side
        historyService.addHistoryElement(new UserEntered(base.getSimpleBase()));
    }

    @Override
    public void continueBase() {
        Base base = getBaseForLoggedInUser();
        if (base == null) {
            throw new IllegalStateException("User does not have any running base");
        }
        connectionService.createConnection(base);
    }

    private void deleteBase(Base base) {
        synchronized (bases) {
            if (bases.remove(base.getName()) == null) {
                throw new IllegalArgumentException("Base does not exist: " + base.getSimpleBase());
            }
            if (!colorsUsed.remove(base.getBaseColor().getHtmlColor())) {
                throw new IllegalArgumentException("Base does not exist: " + base.getSimpleBase());
            }
            serverEnergyService.onBaseKilled(base);
        }
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
        Base base = bases.get(baseSyncItem.getBase().getName());
        if (base == null) {
            throw new IllegalArgumentException("Base does not exist: " + baseSyncItem.getBase());
        }
        return base;
    }

    @Override
    public Base getBase(SimpleBase simpleBase) {
        return bases.get(simpleBase.getName());
    }


    @Override
    public List<BaseColor> getFreeColors(final int maxCount) {

        return (List<BaseColor>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(org.hibernate.Session session) {
                Criteria criteria = session.createCriteria(BaseColor.class);
                if (!colorsUsed.isEmpty()) {
                    criteria.add(Restrictions.not(Restrictions.in("htmlColor", colorsUsed)));
                }
                criteria.setMaxResults(maxCount);
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
    public List<List<BaseColor>> getFreeColorsMultiColums(int maxCount, int columns) {
        List<List<BaseColor>> result = new ArrayList<List<BaseColor>>();
        int columnIndex = 0;
        List<BaseColor> row = new ArrayList<BaseColor>();
        for (BaseColor baseColor : getFreeColors(maxCount)) {
            if (columnIndex >= columns) {
                columnIndex = 0;
                result.add(row);
                row = new ArrayList<BaseColor>();
            }
            row.add(baseColor);
            columnIndex++;
        }
        if (!row.isEmpty()) {
            result.add(row);
        }
        return result;
    }

    @Override
    public String getFreePlayerName() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            String name = DEFAULT_PLAYER_NAME_PREFIX + Integer.toString(i);
            if (!bases.containsKey(name)) {
                return name;
            }
        }
        throw new IllegalStateException("No free name");
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
            historyService.addHistoryElement(new BaseHasBeenDefeated(base.getSimpleBase()));
            if (!base.getSimpleBase().equals(dummyBase.getSimpleBase())) {
                if (actor != null) {
                    sendDefeatedMessage(syncItem, actor);
                }
                deleteBase(base);
            }
        }
    }

    private void sendDefeatedMessage(SyncBaseItem victim, SyncBaseItem actor) {
        Message message = new Message();
        message.setTitle("Game over!");
        message.setMessage("You have been defeated by " + actor.getBase().getName());
        connectionService.sendPacket(victim.getBase(), message);
        message = new Message();
        message.setTitle("Congratulations!");
        message.setMessage("You defeated " + victim.getBase().getName());
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
        AccountBalancePackt packt = new AccountBalancePackt();
        packt.setAccountBalance(base.getAccountBalance());
        connectionService.sendPacket(base.getSimpleBase(), packt);
    }

    @Override
    public void sendXpUpdate(UserItemTypeAccess userItemTypeAccess, Base base) {
        XpBalancePackt packt = new XpBalancePackt();
        packt.setXp(userItemTypeAccess.getXp());
        connectionService.sendPacket(base.getSimpleBase(), packt);
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
    public SimpleBase getDummyBase() {
        return dummyBase.getSimpleBase();
    }

    @Override
    public void surenderBase(Base base) {
        base.setUser(null);
        base.setAbandoned(true);
    }

    @Override
    public List<Base> getBases() {
        return new ArrayList<Base>(bases.values());
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
            colorsUsed.clear();
            setupDummyBase();
            for (Base newBase : newBases) {
                bases.put(newBase.getName(), newBase);
                colorsUsed.add(newBase.getBaseColor().getHtmlColor());
            }
        }
    }

    @Override
    public void depositResource(int price, SimpleBase simpleBase) {
        getBase(simpleBase).depositMoney(price);
    }

    @Override
    public void withdrawalMoney(int price, SimpleBase simpleBase) throws InsufficientFundsException {
        getBase(simpleBase).withdrawalMoney(price);
    }

}
