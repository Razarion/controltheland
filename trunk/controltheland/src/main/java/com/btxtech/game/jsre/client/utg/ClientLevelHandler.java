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

import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.SplashManager;
import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.jsre.common.LevelStatePacket;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.tutorial.GameFlow;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 09.01.2011
 * Time: 14:20:37
 */
public class ClientLevelHandler implements CommonUserGuidanceService {
    private static final ClientLevelHandler INSTANCE = new ClientLevelHandler();
    private LevelScope levelScope;
    private Integer nextTaskId;
    private Integer xp2LevelUp;
    private String activeQuestTitle;
    private String activeQuestProgress;
    private Integer activeQuestLevelTaskId;

    /**
     * Singleton
     */
    private ClientLevelHandler() {
    }

    public static ClientLevelHandler getInstance() {
        return INSTANCE;
    }

    public void setLevelScope(LevelStatePacket levelStatePacket) {
        // Setup values
        levelScope = levelStatePacket.getLevel();
        xp2LevelUp = levelStatePacket.getXp2LevelUp();
        activeQuestTitle = levelStatePacket.getActiveQuestTitle();
        activeQuestProgress = levelStatePacket.getActiveQuestProgress();
        activeQuestLevelTaskId = levelStatePacket.getActiveQuestLevelTaskId();
        // Setup GUI
        SideCockpit.getInstance().setLevel(levelScope);
        RadarPanel.getInstance().setLevelRadarMode(levelScope.getRadarMode());
        if (levelStatePacket.getXp() != null && xp2LevelUp != null) {
            SideCockpit.getInstance().setXp(levelStatePacket.getXp(), xp2LevelUp);
        } else {
            SideCockpit.getInstance().hideXp();
        }
        if (activeQuestTitle != null || activeQuestProgress != null || activeQuestLevelTaskId != null) {
            SideCockpit.getInstance().setActiveQuest(activeQuestTitle, activeQuestProgress, activeQuestLevelTaskId);
        } else {
            SideCockpit.getInstance().setNoActiveQuest();
        }
        if (levelStatePacket.getQuestsDone() != null && levelStatePacket.getTotalQuests() != null) {
            SideCockpit.getInstance().setQuestOverview(levelStatePacket.getQuestsDone(), levelStatePacket.getTotalQuests());
        } else {
            SideCockpit.getInstance().hideQuestOverview();
        }
        if (levelStatePacket.getMissionsDone() != null && levelStatePacket.getTotalMissions() != null) {
            SideCockpit.getInstance().setMissionOverview(levelStatePacket.getMissionsDone(), levelStatePacket.getTotalMissions());
        } else {
            SideCockpit.getInstance().hideMissionOverview();
        }
        SideCockpit.getInstance().updateItemLimit();
    }

    public void onLevelChanged(LevelStatePacket levelStatePacket) {
        if (levelStatePacket.isMissionQuestCompleted()) {
            SplashManager.getInstance().onLevelTaskCone();
            SideCockpit.getInstance().setNoActiveQuest();
            activeQuestTitle = null;
            activeQuestProgress = null;
            activeQuestLevelTaskId = null;
        }
        if (levelStatePacket.getXp() != null || levelStatePacket.getXp2LevelUp() != null) {
            if (levelStatePacket.getXp2LevelUp() != null) {
                xp2LevelUp = levelStatePacket.getXp2LevelUp();
            }
            if (levelStatePacket.getXp() != null) {
                SideCockpit.getInstance().setXp(levelStatePacket.getXp(), xp2LevelUp);
            }
        }

        if (levelStatePacket.getXp() != null) {
            SideCockpit.getInstance().setXp(levelStatePacket.getXp(), xp2LevelUp);
        }

        if (levelStatePacket.isQuestDeactivated()) {
            SideCockpit.getInstance().setNoActiveQuest();
            activeQuestTitle = null;
            activeQuestProgress = null;
            activeQuestLevelTaskId = null;
        }
        if (!levelStatePacket.isMissionQuestCompleted() &&
                (levelStatePacket.getActiveQuestTitle() != null || levelStatePacket.getActiveQuestProgress() != null || levelStatePacket.getActiveQuestLevelTaskId() != null)) {
            if (levelStatePacket.getActiveQuestTitle() != null) {
                activeQuestTitle = levelStatePacket.getActiveQuestTitle();
            }
            if (levelStatePacket.getActiveQuestProgress() != null) {
                activeQuestProgress = levelStatePacket.getActiveQuestProgress();
            }
            if (levelStatePacket.getActiveQuestLevelTaskId() != null) {
                activeQuestLevelTaskId = levelStatePacket.getActiveQuestLevelTaskId();
            }
            SideCockpit.getInstance().setActiveQuest(activeQuestTitle, activeQuestProgress, activeQuestLevelTaskId);
        }

        if (levelStatePacket.getQuestsDone() != null && levelStatePacket.getTotalQuests() != null) {
            SideCockpit.getInstance().setQuestOverview(levelStatePacket.getQuestsDone(), levelStatePacket.getTotalQuests());
        }
        if (levelStatePacket.getMissionsDone() != null && levelStatePacket.getTotalMissions() != null) {
            SideCockpit.getInstance().setMissionOverview(levelStatePacket.getMissionsDone(), levelStatePacket.getTotalMissions());
        }
        if (levelStatePacket.getLevel() != null) {
            levelScope = levelStatePacket.getLevel();
            SplashManager.getInstance().onLevelUp();
            RadarPanel.getInstance().setLevelRadarMode(levelScope.getRadarMode());
            SideCockpit.getInstance().setLevel(levelScope);
            SideCockpit.getInstance().updateItemLimit();
            if (activeQuestTitle != null || activeQuestProgress != null || activeQuestLevelTaskId != null) {
                SideCockpit.getInstance().setNoActiveQuest();
                activeQuestTitle = null;
                activeQuestProgress = null;
                activeQuestLevelTaskId = null;
            }
        }
    }

    @Override
    public LevelScope getLevelScope() {
        return levelScope;
    }

    public boolean isItemTypeAllowed(BaseItemType baseItemType) {
        return isItemTypeAllowed(baseItemType.getId());
    }

    public boolean isItemTypeAllowed(int baseItemTypeId) {
        return levelScope.getLimitation4ItemType(baseItemTypeId) > 0;
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
            case SHOW_LEVEL_TASK_DONE_PAGE:
                Window.open(Connection.getInstance().getGameInfo().getPredefinedUrls().get(CmsUtil.CmsPredefinedPage.LEVEL_TASK_DONE), CmsUtil.TARGET_SELF, "");
                return;
            default:
                throw new IllegalArgumentException("Unknown GameFlow Type: " + gameFlow.getType());
        }
    }
}
