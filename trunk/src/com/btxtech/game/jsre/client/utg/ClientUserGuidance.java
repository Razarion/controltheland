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

package com.btxtech.game.jsre.client.utg;

import com.btxtech.game.jsre.client.ClientSyncBaseItemView;
import com.btxtech.game.jsre.client.ClientSyncItemView;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.Group;
import com.btxtech.game.jsre.client.cockpit.SelectionHandler;
import com.btxtech.game.jsre.client.cockpit.SelectionListener;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.missions.AttackMission;
import com.btxtech.game.jsre.client.utg.missions.BuildFactoryMission;
import com.btxtech.game.jsre.client.utg.missions.CollectMission;
import com.btxtech.game.jsre.client.utg.missions.CreateJeepMission;
import com.btxtech.game.jsre.client.utg.missions.FinishedMission;
import com.btxtech.game.jsre.client.utg.missions.Mission;
import com.btxtech.game.jsre.client.utg.missions.MissionAportedException;
import com.btxtech.game.jsre.client.utg.missions.MoveMission;
import com.btxtech.game.jsre.client.utg.missions.ScrollMission;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.utg.MissionAction;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.user.client.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 25.01.2010
 * Time: 21:29:16
 */
public class ClientUserGuidance implements SelectionListener {
    private static final ClientUserGuidance INSTANCE = new ClientUserGuidance();
    private Mission currentMission;
    private static final int TICK_INTERVALL = 500;
    private static final int APPLAUSE_TIME = 2000;
    private List<Mission> missions;
    private Timer timer;
    private boolean isRunning = false;

    public static ClientUserGuidance getInstance() {
        return INSTANCE;
    }

    /**
     * Singletion
     */
    private ClientUserGuidance() {
    }

    public void start() {
        try {
            setupMissions();
            startTimer();
            Collection<ClientSyncBaseItemView> items = ItemContainer.getInstance().getOwnItems();
            if (items.isEmpty()) {
                return;
            }
            startNextMission();
            SelectionHandler.getInstance().addSelectionListener(this);
            isRunning = true;
        } catch (NoSuchItemTypeException e) {
            GwtCommon.handleException(e);
        }
    }

    private void startTimer() {
        timer = new Timer() {
            @Override
            public void run() {
                if (currentMission != null) {
                    if (currentMission.isAccomplished()) {
                        if (System.currentTimeMillis() > APPLAUSE_TIME + currentMission.getLastTaskChangeTime()) {
                            currentMission.close();
                            ClientUserTracker.getInstance().onMissionAction(MissionAction.MISSION_COMPLETED, currentMission);
                            currentMission = null;
                        }
                    } else {
                        currentMission.blink();
                    }
                } else {
                    startNextMission();
                    if (currentMission == null) {
                        // no more missions
                        timer.cancel();
                    }
                }
            }
        };
        timer.scheduleRepeating(TICK_INTERVALL);
    }

    private void setupMissions() throws NoSuchItemTypeException {
        missions = new ArrayList<Mission>();
        missions.add(new MoveMission());
        missions.add(new BuildFactoryMission());
        missions.add(new CreateJeepMission());
        missions.add(new AttackMission());
        missions.add(new CollectMission());
        missions.add(new ScrollMission());
        missions.add(new FinishedMission());
    }

    @Override
    public void onTargetSelectionChanged(ClientSyncItemView selection) {
        // Ignore
    }

    @Override
    public void onSelectionCleared() {
        // Ignore
    }

    @Override
    public void onOwnSelectionChanged(Group selectedGroup) {
        if (currentMission != null && isRunning) {
            try {
                currentMission.onOwnSelectionChanged(selectedGroup);
            } catch (MissionAportedException e) {
                GwtCommon.handleException(e);
                currentMission = null;
            }
        }
    }

    public void onSyncItemDeactivated(SyncBaseItem syncBaseItem) {
        if (currentMission != null && isRunning) {
            currentMission.onSyncItemDeactivated(syncBaseItem);
        }
    }

    public void onExecuteCommand(SyncBaseItem syncItem, BaseCommand baseCommand) {
        if (currentMission != null && isRunning) {
            currentMission.onExecuteCommand(syncItem, baseCommand);
        }
    }

    public void onItemCreated(ClientSyncItemView item) {
        if (currentMission != null && isRunning) {
            currentMission.onItemCreated(item);
        }
    }

    public void onItemDeleted(ClientSyncItemView item) {
        if (currentMission != null && isRunning) {
            currentMission.onItemDeleted(item);
        }
    }

    public void onItemBuilt(ClientSyncBaseItemView clientSyncBaseItemView) {
        if (currentMission != null && isRunning) {
            currentMission.onItemBuilt(clientSyncBaseItemView);
        }
    }

    private void startNextMission() {
        do {
            if (missions.isEmpty()) {
                currentMission = null;
                return;
            }
            currentMission = missions.remove(0);
        } while (!currentMission.init());

        try {
            ClientUserTracker.getInstance().onMissionAction(MissionAction.MISSION_START, currentMission);
            currentMission.start();
        } catch (MissionAportedException e) {
            GwtCommon.handleException(e);
            startNextMission();
        }
    }
}
