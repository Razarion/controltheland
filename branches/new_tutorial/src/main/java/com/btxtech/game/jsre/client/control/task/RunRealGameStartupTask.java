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

package com.btxtech.game.jsre.client.control.task;

import com.btxtech.game.jsre.client.ClientBase;
import com.btxtech.game.jsre.client.ClientMessageIdPacketHandler;
import com.btxtech.game.jsre.client.ClientUserService;
import com.btxtech.game.jsre.client.Connection;
import com.btxtech.game.jsre.client.SoundHandler;
import com.btxtech.game.jsre.client.StartPointMode;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.chat.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.menu.MenuBarCockpit;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationModel;
import com.btxtech.game.jsre.client.common.info.RealGameInfo;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.renderer.Renderer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ClientDeadEndProtection;
import com.btxtech.game.jsre.client.utg.ClientUserGuidanceService;
import com.btxtech.game.jsre.common.FacebookUtils;

/**
 * User: beat
 * Date: 05.12.2010
 * Time: 17:55:03
 */
public class RunRealGameStartupTask extends AbstractStartupTask {

    public RunRealGameStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        SideCockpit.getInstance().initRealGame((RealGameInfo) Connection.getInstance().getGameInfo());
        ClientBase.getInstance().setMySimpleGuild(((RealGameInfo) Connection.getInstance().getGameInfo()).getMySimpleGuild());
        Connection.getInstance().handleStorablePackets(((RealGameInfo) Connection.getInstance().getGameInfo()).getStorablePackets());
        MenuBarCockpit.getInstance().initRealGame((RealGameInfo) Connection.getInstance().getGameInfo());
        Connection.getInstance().startSyncInfoPoll();
        ClientUserService.getInstance().init();
        SideCockpit.getInstance().updateItemLimit();
        ClientMessageIdPacketHandler.getInstance().runRealGame(Connection.getInstance(), ChatCockpit.getInstance(), ClientMessageIdPacketHandler.START_DELAY);
        Renderer.getInstance().start();
        SoundHandler.getInstance().start(Connection.getInstance().getGameInfo().getCommonSoundInfo());
        TerrainView.getInstance().setFocus();
        StartPointMode.getInstance().activateIfNeeded();
        if (!StartPointMode.getInstance().isActive()) {
            ClientDeadEndProtection.getInstance().start();
        }
        ClientUserGuidanceService.getInstance().setLevel(((RealGameInfo) Connection.getInstance().getGameInfo()).getLevelScope());
        QuestVisualisationModel.getInstance().setLevelTask(((RealGameInfo) Connection.getInstance().getGameInfo()).getLevelTaskPacket());
        FacebookUtils.callConversationRealRealGamePixel();
    }
}
