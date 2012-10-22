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
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualtsationModel;
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
        QuestVisualtsationModel.getInstance().onLevelChange(levelScope);
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
