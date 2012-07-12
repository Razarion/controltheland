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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.dialogs.UnfrequentDialog;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.item.ItemViewContainer;
import com.btxtech.game.jsre.client.simulation.SimulationConditionServiceImpl;
import com.btxtech.game.jsre.client.utg.ClientLevelHandler;
import com.btxtech.game.jsre.common.packets.BaseChangedPacket;
import com.btxtech.game.jsre.common.InsufficientFundsException;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseAttributes;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.impl.AbstractBaseServiceImpl;
import com.btxtech.game.jsre.common.gameengine.services.bot.BotConfig;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: Sep 9, 2009
 * Time: 5:07:34 PM
 */
public class ClientBase extends AbstractBaseServiceImpl implements AbstractBaseService {
    public static interface OwnBaseDestroyedListener {
        void onOwnBaseDestroyed();
    }

    private final static String OWN_BASE_COLOR = "#00FF00";
    private final static String ALLIANCE_BASE_COLOR = "#ffd800";
    private final static String ENEMY_BASE_COLOR = "#e04c4c";
    private final static String BOT_BASE_COLOR = "#d59c59";
    private final static String UNKNOWN_BASE_COLOR = "#888888";
    private static final ClientBase INSTANCE = new ClientBase();
    private double accountBalance;
    private SimpleBase simpleBase;
    private int houseSpace;
    private boolean connectedToServer = true;
    private OwnBaseDestroyedListener ownBaseDestroyedListener;
    private Map<BaseItemType, Integer> myItemTypeCount = new HashMap<BaseItemType, Integer>();
    private int ownItemCount = 0;
    private Logger log = Logger.getLogger(ClientBase.class.getName());

    /**
     * Singleton
     */
    private ClientBase() {
    }

    public static ClientBase getInstance() {
        return INSTANCE;
    }

    public void createOwnSimulationBaseIfNotExist(String onwBaseName) {
        if (simpleBase == null) {
            int baseId = getFreeBaseId();
            simpleBase = new SimpleBase(baseId);
            createBase(simpleBase, onwBaseName, false);
        }
    }

    public void setBase(SimpleBase simpleBase) {
        this.simpleBase = simpleBase;
    }

    public SimpleBase getSimpleBase() {
        return simpleBase;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
        SideCockpit.getInstance().updateMoney();
    }

    public boolean isMyOwnProperty(SyncBaseItem syncItem) {
        return simpleBase != null && simpleBase.equals(syncItem.getBase());
    }

    public boolean isMyOwnBase(SimpleBase simpleBase) {
        return this.simpleBase != null && this.simpleBase.equals(simpleBase);
    }

    public boolean isEnemy(SyncBaseItem syncItem) {
        return this.simpleBase == null || isEnemy(simpleBase, syncItem.getBase());
    }

    public boolean isEnemy(SimpleBase other) {
        return this.simpleBase == null || isEnemy(simpleBase, other);
    }

    @Override
    public void depositResource(double price, SimpleBase simpleBase) {
        if (this.simpleBase != null && this.simpleBase.equals(simpleBase)) {
            if (Connection.getInstance().getGameInfo() instanceof RealGameInfo) {
                accountBalance += price;
                if (accountBalance > ClientLevelHandler.getInstance().getLevelScope().getMaxMoney()) {
                    accountBalance = ClientLevelHandler.getInstance().getLevelScope().getMaxMoney();
                }
            } else {
                accountBalance += price;
            }
            SideCockpit.getInstance().updateMoney();
            SimulationConditionServiceImpl.getInstance().onMoneyIncrease(simpleBase, price);
        }
    }

    public boolean isDepositResourceAllowed(double amount) {
        if (Connection.getInstance().getGameInfo() instanceof RealGameInfo) {
            return ClientLevelHandler.getInstance().getLevelScope().getMaxMoney() >= accountBalance + amount;
        } else {
            return true;
        }
    }

    @Override
    public void withdrawalMoney(double price, SimpleBase simpleBase) throws InsufficientFundsException {
        if (this.simpleBase == null || !this.simpleBase.equals(simpleBase)) {
            return;
        }
        if (Math.round(price) > Math.round(accountBalance)) {
            if (Connection.getInstance().getGameEngineMode() == GameEngineMode.PLAYBACK) {
                return;
            }
            UnfrequentDialog.open(UnfrequentDialog.Type.NO_MONEY);
            throw new InsufficientFundsException();
        } else {
            accountBalance -= price;
            SideCockpit.getInstance().updateMoney();
        }
    }

    public String getOwnBaseName() {
        return getBaseName(simpleBase);
    }


    public String getBaseHtmlColor(SimpleBase base) {
        if (isMyOwnBase(base)) {
            return getOwnBaseHtmlColor();
        } else if (!isEnemy(base)) {
            return getAllianceBaseHtmlColor();
        }
        BaseAttributes baseAttributes = getBaseAttributes(base);
        if (baseAttributes == null) {
            return UNKNOWN_BASE_COLOR;
        }

        if (baseAttributes.isBot()) {
            return BOT_BASE_COLOR;
        }
        return ENEMY_BASE_COLOR;
    }


    public String getOwnBaseHtmlColor() {
        return OWN_BASE_COLOR;
    }

    public String getAllianceBaseHtmlColor() {
        return ALLIANCE_BASE_COLOR;
    }

    public void onBaseChangedPacket(BaseChangedPacket baseChangedPacket) {
        switch (baseChangedPacket.getType()) {
            case CHANGED:
                baseChangedPacket.getBaseAttributes().resetAlliancesDueToStrangeGwtBehavior();
                updateBase(baseChangedPacket.getBaseAttributes());
                ItemViewContainer.getInstance().updateMarker();
                break;
            case CREATED:
                baseChangedPacket.getBaseAttributes().resetAlliancesDueToStrangeGwtBehavior();
                createBase(baseChangedPacket.getBaseAttributes());
                break;
            case REMOVED:
                removeBase(baseChangedPacket.getBaseAttributes().getSimpleBase());
                break;
            default:
                throw new IllegalArgumentException(this + " unknown type: " + baseChangedPacket.getType());
        }
    }

    public int getHouseSpace() {
        return houseSpace;
    }

    public void setHouseSpace(int houseSpace) {
        this.houseSpace = houseSpace;
    }

    @Override
    public int getHouseSpace(SimpleBase simpleBase) {
        check4OwnBase(simpleBase);
        return getHouseSpace();
    }

    @Override
    public boolean isItemLimit4ItemAddingAllowed(BaseItemType toBeBuiltType, SimpleBase simpleBase) throws NoSuchItemTypeException {
        if (ClientBase.getInstance().isMyOwnBase(simpleBase)
                && !ClientBase.getInstance().isBot(simpleBase)
                && (Connection.getInstance().getGameEngineMode() == GameEngineMode.SLAVE || Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER)) {
            if (isHouseSpaceExceeded(simpleBase)) {
                UnfrequentDialog.open(UnfrequentDialog.Type.SPACE_LIMIT);
                return false;
            }
            if (isLevelLimitation4ItemTypeExceeded(toBeBuiltType, simpleBase)) {
                UnfrequentDialog.open(UnfrequentDialog.Type.ITEM_LIMIT);
                return false;
            }
        }
        return true;
    }

    public void checkItemLimit4ItemAdding(BaseItemType itemType) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        try {
            checkItemLimit4ItemAdding(itemType, simpleBase);
        } catch (ItemLimitExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.ITEM_LIMIT);
            throw new ItemLimitExceededException();
        } catch (HouseSpaceExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.SPACE_LIMIT);
            throw new HouseSpaceExceededException();
        }
    }

    public boolean checkItemLimit4ItemAddingDialog(BaseItemType itemType) throws NoSuchItemTypeException {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.MASTER) {
            return true;
        }
        try {
            checkItemLimit4ItemAdding(itemType, simpleBase);
        } catch (ItemLimitExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.ITEM_LIMIT);
            return false;
        } catch (HouseSpaceExceededException e) {
            UnfrequentDialog.open(UnfrequentDialog.Type.SPACE_LIMIT);
            return false;
        }
        return true;
    }

    private void check4OwnBase(SimpleBase simpleBase) {
        if (!isMyOwnBase(simpleBase)) {
            throw new IllegalArgumentException("Wrong base given: " + simpleBase + " expected base: " + this.simpleBase);
        }
    }

    @Override
    public int getItemCount(SimpleBase simpleBase) {
        check4OwnBase(simpleBase);
        return ownItemCount;
    }

    @Override
    public int getItemCount(SimpleBase simpleBase, int itemTypeId) throws NoSuchItemTypeException {
        check4OwnBase(simpleBase);
        BaseItemType baseItemType = (BaseItemType) ItemContainer.getInstance().getItemType(itemTypeId);
        Integer count = myItemTypeCount.get(baseItemType);
        if (count != null) {
            return count;
        } else {
            return 0;
        }
    }

    @Override
    public LevelScope getLevel(SimpleBase simpleBase) {
        check4OwnBase(simpleBase);
        return ClientLevelHandler.getInstance().getLevelScope();
    }

    public void recalculate4FakedHouseSpace(SyncBaseItem affectedSyncItem) {
        if (connectedToServer || !isMyOwnProperty(affectedSyncItem)) {
            return;
        }
        houseSpace = 0;
        for (ClientSyncItem clientSyncItem : ItemContainer.getInstance().getOwnItems()) {
            SyncBaseItem syncBaseItem = clientSyncItem.getSyncBaseItem();
            if (syncBaseItem.hasSyncHouse()) {
                houseSpace += syncBaseItem.getSyncHouse().getSpace();
            }
        }
        SideCockpit.getInstance().updateItemLimit();
    }

    public void setConnectedToServer4FakedHouseSpace(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }

    @Override
    public Collection<SyncBaseItem> getItems(SimpleBase simpleBase) {
        return ItemContainer.getInstance().getItems4Base(simpleBase);
    }

    @Override
    public SimpleBase createBotBase(BotConfig botConfig) {
        int baseId = getFreeBaseId();
        SimpleBase simpleBase = new SimpleBase(baseId);
        createBase(simpleBase, botConfig.getName(), false);
        setBot(simpleBase, true);
        return simpleBase;
    }

    private int getFreeBaseId() {
        int maxId = 0;
        for (SimpleBase simpleBase : getAllSimpleBases()) {
            if (simpleBase.getId() > maxId) {
                maxId = simpleBase.getId();
            }
        }
        maxId++;
        return maxId;
    }

    public void cleanup() {
        clear();
        simpleBase = null;
        ownBaseDestroyedListener = null;
        myItemTypeCount.clear();
        ownItemCount = 0;
    }

    public void setOwnBaseDestroyedListener(OwnBaseDestroyedListener ownBaseDestroyedListener) {
        this.ownBaseDestroyedListener = ownBaseDestroyedListener;
    }

    @Override
    public void checkBaseAccess(SyncBaseItem syncBaseItem) throws NotYourBaseException {
        if (!isMyOwnProperty(syncBaseItem)) {
            throw new NotYourBaseException(getBaseName(simpleBase), getBaseName(syncBaseItem.getBase()));
        }
    }

    @Override
    public void sendAccountBaseUpdate(SimpleBase simpleBase) {
        // Do nothing here
    }

    @Override
    public void onItemCreated(SyncBaseItem syncBaseItem) {
        if (isMyOwnProperty(syncBaseItem)) {
            ownItemCount++;
            Integer count = myItemTypeCount.get(syncBaseItem.getBaseItemType());
            if (count == null) {
                count = 0;
            }
            count++;
            myItemTypeCount.put(syncBaseItem.getBaseItemType(), count);

            SideCockpit.getInstance().updateItemLimit();
        }
    }

    @Override
    public void onItemDeleted(SyncBaseItem syncBaseItem, SimpleBase actor) {
        if (isMyOwnProperty(syncBaseItem)) {
            ownItemCount--;
            Integer count = myItemTypeCount.get(syncBaseItem.getBaseItemType());
            if (count == null) {
                log.warning("ClientBase: onItemDeleted() called but no such SyncBaseItem was added before: " + syncBaseItem + " actor: " + actor);
            } else {
                count--;
                if (count < 0) {
                    log.warning("ClientBase: count < 0: " + count + " syncBaseItem: " + syncBaseItem + " actor: " + actor);
                }
                myItemTypeCount.put(syncBaseItem.getBaseItemType(), count);
            }

            if (ownItemCount < 0) {
                log.warning("ClientBase: ownItemCount < 0: " + ownItemCount + " syncBaseItem: " + syncBaseItem + " actor: " + actor);
                ownItemCount = 0;
            }

            if (ownItemCount == 0) {
                if (ownBaseDestroyedListener != null) {
                    ownBaseDestroyedListener.onOwnBaseDestroyed();
                }
            }

            SideCockpit.getInstance().updateItemLimit();
        } else {
            if (Connection.getInstance().getGameEngineMode() == GameEngineMode.MASTER && ItemContainer.getInstance().getItems4Base(syncBaseItem.getBase()).isEmpty()) {
                SimulationConditionServiceImpl.getInstance().onBaseDeleted(actor);
            }
        }
    }

    public int getOwnItemCount() {
        return ownItemCount;
    }
}