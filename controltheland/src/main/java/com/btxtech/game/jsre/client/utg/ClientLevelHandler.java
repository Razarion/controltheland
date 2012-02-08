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

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.common.CmsUtil;
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

    /**
     * Singleton
     */
    private ClientLevelHandler() {
    }

    public static ClientLevelHandler getInstance() {
        return INSTANCE;
    }

    public void setLevelScope(LevelScope levelScope) {
        this.levelScope = levelScope;
        SideCockpit.getInstance().setLevel(levelScope);
    }

    public void onLevelChanged(LevelScope levelScope) {
        if (this.levelScope == null) {
            throw new IllegalStateException("ClientLevelHandler: level has not been set before.");
        }
        this.levelScope = levelScope;
        SideCockpit.getInstance().setLevel(levelScope);
        SideCockpit.getInstance().updateItemLimit();
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
                ClientServices.getInstance().getClientRunner().start(GameStartupSeq.WARM_SIMULATED);
                return;
            case START_REAL_GAME:
                ClientServices.getInstance().getClientRunner().start(GameStartupSeq.WARM_REAL);
                return;
            case SHOW_LEVEL_TASK_DONE_PAGE:
                Window.open(Connection.getInstance().getGameInfo().getPredefinedUrls().get(CmsUtil.CmsPredefinedPage.LEVEL_TASK_DONE), CmsUtil.TARGET_SELF, "");
                return;
            default:
                throw new IllegalArgumentException("Unknown GameFlow Type: " + gameFlow.getType());
        }
    }
}
