/*
 * Copyright (c) 2011.
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

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.SplashManager;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.client.dialogs.quest.QuestInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.packets.LevelTaskPacket;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 09.01.2011
 * Time: 14:20:37
 */
public class ClientLevelHandler implements CommonUserGuidanceService {
    private static final ClientLevelHandler INSTANCE = new ClientLevelHandler();
    private LevelScope levelScope;
    private Integer nextTaskId;
    private QuestInfo currentQuest;
    private static Logger log = Logger.getLogger(ClientLevelHandler.class.getName());

    /**
     * Singleton
     */
    private ClientLevelHandler() {
    }

    public static ClientLevelHandler getInstance() {
        return INSTANCE;
    }

    public void setLevel(LevelScope levelScope) {
        // Setup values
        this.levelScope = levelScope;
        if (levelScope.getPlanetId() != null && levelScope.getPlanetId() != ClientPlanetServices.getInstance().getPlanetInfo().getPlanetId()) {
            SideCockpit.getInstance().setWrongPlanet(true);
            moveToNextPlanet();
        } else {
            SideCockpit.getInstance().setWrongPlanet(false);
        }
        // Setup GUI
        SideCockpit.getInstance().setLevel(levelScope);
        SideCockpit.getInstance().updateItemLimit();
    }

    public void setLevelTask(LevelTaskPacket levelTaskPacket) {
        if (levelTaskPacket == null) {
            currentQuest = null;
            SideCockpit.getInstance().setNoActiveQuest();
        } else if (levelTaskPacket.isCompleted()) {
            SplashManager.getInstance().onLevelTaskCone();
            currentQuest = null;
            SideCockpit.getInstance().setNoActiveQuest();
        } else {
            if (levelTaskPacket.getQuestInfo() != null) {
                currentQuest = levelTaskPacket.getQuestInfo();
                if (currentQuest.getType() == QuestInfo.Type.MISSION) {
                    startMission();
                }
            }
            SideCockpit.getInstance().setActiveQuest(currentQuest, levelTaskPacket.getActiveQuestProgress());
        }
    }

    @Override
    public LevelScope getLevelScope() {
        return levelScope;
    }

    @Override
    public LevelScope getLevelScope(SimpleBase simpleBase) {
        if (!ClientBase.getInstance().getSimpleBase().equals(simpleBase)) {
            throw new IllegalArgumentException("ClientLevelHandler.getLevelScope() is only allowed for own base " + simpleBase);
        }
        return getLevelScope();
    }

    public int getLevelTaskId() {
        if (nextTaskId != null) {
            return nextTaskId;
        } else {
            RootPanel div = Game.getStartupInformation();
            String taskIdString = div.getElement().getAttribute(Game.LEVEL_TASK_ID);
            if (taskIdString == null || taskIdString.trim().isEmpty()) {
                throw new IllegalStateException("Unable to find LevelTakId in Game.html");
            } else {
                return Integer.parseInt(taskIdString);
            }
        }
    }

    public void onTutorialFlow(GameFlow gameFlow) {
        nextTaskId = null;
        switch (gameFlow.getType()) {
            case START_NEXT_LEVEL_TASK_TUTORIAL:
                nextTaskId = gameFlow.getNextTutorialLevelTaskId();
                StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_SIMULATED);
                return;
            case START_REAL_GAME:
                StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_REAL);
                return;
            default:
                throw new IllegalArgumentException("Unknown GameFlow Type: " + gameFlow.getType());
        }
    }

    public void abortMission() {
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.MASTER) {
            log.warning("Attempt to abort the real game");
            return;
        }
        StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_REAL);
    }

    public void startMission() {
        if (currentQuest == null || currentQuest.getType() != QuestInfo.Type.MISSION) {
            log.warning("ClientLevelHandler.startMission() currentQuest == null");
            return;
        }
        if (currentQuest.getType() != QuestInfo.Type.MISSION) {
            log.warning("ClientLevelHandler.startMission() currentQuest.getType() != QuestInfo.Type.MISSION");
            return;
        }
        if (Connection.getInstance().getGameEngineMode() != GameEngineMode.SLAVE) {
            log.warning("Attempt to start a mission inside another mission.");
            return;
        }

        YesNoDialog yesNoDialog = new YesNoDialog("Start Mission", "Compete in a single player mission on a different planet. You can return to this base at any time.", "Start", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                nextTaskId = currentQuest.getId();
                StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_SIMULATED);
            }
        }, "Cancel", null);
        DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
    }

    public boolean hasActiveQuest() {
        return currentQuest != null;
    }

    public void moveToNextPlanet() {
        YesNoDialog yesNoDialog = new YesNoDialog("Next Planet", "Leave your base and move to the next planet?", "GO!", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Connection.getInstance().surrenderBase();
            }
        }, "Cancel", null);
        DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
    }
}
