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

import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.chat.ChatCockpit;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.cockpit.menu.MenuBarCockpit;
import com.btxtech.game.jsre.client.cockpit.quest.QuestVisualisationCockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 04.12.2010
 * Time: 12:50:50
 */
public class GuiStartupTask extends AbstractStartupTask {

    public GuiStartupTask(StartupTaskEnum taskEnum) {
        super(taskEnum);
    }

    @Override
    protected void privateStart(DeferredStartup deferredStartup) {
        GwtCommon.disableBrowserContextMenuJSNI();
        SideCockpit.getInstance().addToParent(MapWindow.getAbsolutePanel());
        QuestVisualisationCockpit.getInstance().addToParent(MapWindow.getAbsolutePanel());
        ItemCockpit.getInstance().addToParentAndRegister(MapWindow.getAbsolutePanel());
        RootPanel.get().add(MapWindow.getAbsolutePanel(), 0, 0);
        ChatCockpit.getInstance().addToParent(MapWindow.getAbsolutePanel());
        MenuBarCockpit.getInstance().addToParent(MapWindow.getAbsolutePanel());

        TerrainView.getInstance().addToParent(MapWindow.getAbsolutePanel());
        TerrainView.getInstance().getCanvas().getElement().getStyle().setZIndex(Constants.Z_INDEX_TERRAIN);
    }
}
