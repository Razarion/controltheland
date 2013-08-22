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
import com.btxtech.game.jsre.client.ClientI18nHelper;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.StartPointMode;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.dialogs.DialogManager;
import com.btxtech.game.jsre.client.dialogs.YesNoDialog;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 09.01.2011
 * Time: 14:20:37
 */
public class ClientUserGuidanceService implements CommonUserGuidanceService {
    private static final ClientUserGuidanceService INSTANCE = new ClientUserGuidanceService();
    private LevelScope levelScope;
    private Integer nextTaskId;
    private Integer nextPlanetId;

    /**
     * Singleton
     */
    private ClientUserGuidanceService() {
    }

    public static ClientUserGuidanceService getInstance() {
        return INSTANCE;
    }

    public static void moveToNextPlanet() {
        if (StartPointMode.getInstance().isActive()) {
            YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.nextPlanet(), ClientI18nHelper.CONSTANTS.goNextPlanet(), ClientI18nHelper.CONSTANTS.go(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.Location.reload();
                }
            }, ClientI18nHelper.CONSTANTS.cancel(), null);
            DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
        } else {
            YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.nextPlanet(), ClientI18nHelper.CONSTANTS.leaveBaseNextPlanet(), ClientI18nHelper.CONSTANTS.go(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Connection.getInstance().surrenderBase(new Runnable() {
                        @Override
                        public void run() {
                            Window.Location.reload();
                        }
                    });
                }
            }, ClientI18nHelper.CONSTANTS.cancel(), null);
            DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
        }
    }

    public void moveToPlanet(final PlanetLiteInfo planetLiteInfo) {
        if (StartPointMode.getInstance().isActive()) {
            YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.moveToPlanetTitle(), ClientI18nHelper.CONSTANTS.moveToPlanet(planetLiteInfo.getName()), ClientI18nHelper.CONSTANTS.go(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    moveToPlanetPrivate(planetLiteInfo);
                }
            }, ClientI18nHelper.CONSTANTS.cancel(), null);
            DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
        } else {
            YesNoDialog yesNoDialog = new YesNoDialog(ClientI18nHelper.CONSTANTS.moveToPlanetTitle(), ClientI18nHelper.CONSTANTS.leaveBaseMoveToPlanet(planetLiteInfo.getName()), ClientI18nHelper.CONSTANTS.go(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Connection.getInstance().surrenderBase(new Runnable() {
                        @Override
                        public void run() {
                            moveToPlanetPrivate(planetLiteInfo);
                        }
                    });
                }
            }, ClientI18nHelper.CONSTANTS.cancel(), null);
            DialogManager.showDialog(yesNoDialog, DialogManager.Type.QUEUE_ABLE);
        }
    }

    private void moveToPlanetPrivate(PlanetLiteInfo planetLiteInfo) {
        this.nextPlanetId = planetLiteInfo.getPlanetId();
        StartupScreen.getInstance().fadeOutAndStart(GameStartupSeq.WARM_REAL);
    }

    public Integer getAndClearNextPlanetId() {
        Integer tmp = nextPlanetId;
        nextPlanetId = null;
        return tmp;
    }

    public void setLevel(LevelScope levelScope) {
        // Setup values
        this.levelScope = levelScope;
        QuestVisualisationModel.getInstance().onLevelChange(levelScope);
        // Setup GUI
        SideCockpit.getInstance().setLevel(levelScope);
        SideCockpit.getInstance().updateItemLimit();
    }

    @Override
    public LevelScope getLevelScope() {
        return levelScope;
    }

    @Override
    public LevelScope getLevelScope(SimpleBase simpleBase) {
        if (!ClientBase.getInstance().getSimpleBase().equals(simpleBase)) {
            throw new IllegalArgumentException("ClientUserGuidanceService.getLevelScope() is only allowed for own base " + simpleBase);
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
                throw new IllegalStateException("Unable to find LevelTaskId in Game.html");
            } else {
                return Integer.parseInt(taskIdString);
            }
        }
    }

    public void setNextTaskId(int nextTaskId) {
        this.nextTaskId = nextTaskId;
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
}
