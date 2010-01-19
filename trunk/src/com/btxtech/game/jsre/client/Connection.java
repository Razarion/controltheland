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

import com.btxtech.game.jsre.client.cockpit.BuildupItemPanel;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.GameInfo;
import com.btxtech.game.jsre.client.common.Message;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.client.dialogs.MessageDialog;
import com.btxtech.game.jsre.client.item.ClientItemTypeAccess;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientUserTracker;
import com.btxtech.game.jsre.common.AccountBalancePackt;
import com.btxtech.game.jsre.common.EnergyPacket;
import com.btxtech.game.jsre.common.NoConnectionException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.XpBalancePackt;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccessSyncInfo;
import com.btxtech.game.jsre.common.gameengine.services.utg.GameStartupState;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.syncInfos.SyncItemInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import java.util.Date;

/**
 * User: beat
 * Date: Jul 4, 2009
 * Time: 11:23:52 AM
 */
public class Connection implements AsyncCallback<Void> {
    public static final int MIN_DELAY_BETWEEN_TICKS = 200;
    public static final int STATISTIC_DELAY = 10000;
    public static final Connection INSTANCE = new Connection();

    private MovableServiceAsync movableServiceAsync = GWT.create(MovableService.class);
    private Timer timer;

    static public Connection getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private Connection() {

    }

    public void start() {
        movableServiceAsync.getGameInfo(new AsyncCallback<GameInfo>() {

            @Override
            public void onFailure(Throwable caught) {
                hanldeDisconnection(caught);
            }

            @Override
            public void onSuccess(GameInfo gameInfo) {
                try {
                    setupGameStructure(gameInfo);
                } catch (Throwable throwable) {
                    GwtCommon.handleException(throwable);
                }
            }
        });
    }

    private void setupGameStructure(final GameInfo gameInfo) {
        ClientBase.getInstance().setBase(gameInfo.getBase());
        ClientBase.getInstance().setAccountBalance(gameInfo.getAccountBalance());
        TerrainView.getInstance().setupTerrain(gameInfo.getTerrainField(), gameInfo.getPassableTerrainTileIds());
        TerrainView.getInstance().moveToMiddle(gameInfo.getStartPoint());
        InfoPanel.getInstance().setGameInfo(gameInfo);
        ClientItemTypeAccess.getInstance().setAllowedItemTypes(gameInfo.getAllowedItemTypes());
        RadarPanel.getInstance().updateEnergy(gameInfo.getEnergyGenerating(), gameInfo.getEnergyConsuming());
        movableServiceAsync.getItemTypes(new AsyncCallback<Collection<ItemType>>() {
            @Override
            public void onFailure(Throwable throwable) {
                hanldeDisconnection(throwable);
            }

            @Override
            public void onSuccess(Collection<ItemType> itemTypes) {
                ItemContainer.getInstance().setItemTypes(itemTypes);
                movableServiceAsync.getAllSyncInfo(new AsyncCallback<Collection<SyncItemInfo>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        hanldeDisconnection(throwable);
                    }

                    @Override
                    public void onSuccess(Collection<SyncItemInfo> syncInfos) {
                        if (syncInfos == null) {
                            return;
                        }
                        for (SyncItemInfo syncInfo : syncInfos) {
                            try {
                                ItemContainer.getInstance().sychronize(syncInfo);
                            } catch (Throwable t) {
                                GwtCommon.handleException(t);
                            }
                        }
                        ClientUserTracker.getInstance().sandGameStartupState(GameStartupState.CLIENT_RUNNING, new Date());                        
                    }
                });
            }
        });

        timer = new Timer() {
            @Override
            public void run() {
                tick();
            }
        };
        tick();
    }

    private void tick() {
        if (movableServiceAsync == null) {
            return;
        }
        movableServiceAsync.getSyncInfo(ClientBase.getInstance().getSimpleBase(), new AsyncCallback<Collection<Packet>>() {
            @Override
            public void onFailure(Throwable throwable) {
                hanldeDisconnection(throwable);
            }

            @Override
            public void onSuccess(Collection<Packet> packets) {
                try {
                    handlePackets(packets);
                } finally {
                    timer.schedule(MIN_DELAY_BETWEEN_TICKS);
                }
            }
        });
    }

    private void handlePackets(Collection<Packet> packets) {
        if (packets == null) {
            return;
        }
        for (Packet packet : packets) {
            try {
                if (packet instanceof SyncItemInfo) {
                    ItemContainer.getInstance().sychronize((SyncItemInfo) packet);
                } else if (packet instanceof Message) {
                    Message message = (Message) packet;
                    MessageDialog.show(message.getTitle(), "<h1>" + message.getMessage() + "</h1>");
                } else if (packet instanceof AccountBalancePackt) {
                    AccountBalancePackt balancePackt = (AccountBalancePackt) packet;
                    ClientBase.getInstance().setAccountBalance(balancePackt.getAccountBalance());
                } else if (packet instanceof XpBalancePackt) {
                    XpBalancePackt xpBalancePackt = (XpBalancePackt) packet;
                    InfoPanel.getInstance().updateXp(xpBalancePackt.getXp());
                } else if (packet instanceof ItemTypeAccessSyncInfo) {
                    ItemTypeAccessSyncInfo itemTypeAccessSyncInfo = (ItemTypeAccessSyncInfo) packet;
                    ClientItemTypeAccess.getInstance().setAllowedItemTypes(itemTypeAccessSyncInfo.getAllowedItemTypes());
                    // Ugly way to refresh the GUI
                    Group group = SelectionHandler.getInstance().getOwnSelection();
                    if (group != null && BuildupItemPanel.uglyWayToRefreshGui != null) {
                        BuildupItemPanel.uglyWayToRefreshGui.onOwnSelectionChanged(group);
                    }
                } else if (packet instanceof EnergyPacket) {
                    EnergyPacket energyPacket = (EnergyPacket) packet;
                    InfoPanel.getInstance().updateEnergy(energyPacket.getGenerating(), energyPacket.getConsuming());
                    RadarPanel.getInstance().updateEnergy(energyPacket.getGenerating(), energyPacket.getConsuming());
                } else {
                    throw new IllegalArgumentException(this + " unknwon packet: " + packet);
                }
            } catch (Throwable t) {
                GwtCommon.handleException(t);
            }
        }
    }


    public void sendCommand(BaseCommand baseCommand) {
        if (movableServiceAsync != null) {
            movableServiceAsync.sendCommand(baseCommand, this);
        }
    }

    public static MovableServiceAsync getMovableServiceAsync() {
        return INSTANCE.movableServiceAsync;
    }

    @Override
    public void onFailure(Throwable caught) {
        hanldeDisconnection(caught);
    }

    @Override
    public void onSuccess(Void result) {
        // Ignore
    }

    private void hanldeDisconnection(Throwable throwable) {
        movableServiceAsync = null;
        if (throwable instanceof NotYourBaseException) {
            MessageDialog.show("Not your Base", "Most likely you start another<br />base in another browser window");
        } else if (throwable instanceof NoConnectionException) {
            MessageDialog.show("No Connection", "Most likely you start another<br />base in another browser window: " + throwable.getMessage());
        } else {
            GwtCommon.handleException(throwable);
        }
    }

    public static boolean isConnected() {
        return INSTANCE.movableServiceAsync != null;
    }

}
