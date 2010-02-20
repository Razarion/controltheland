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

package com.btxtech.game.jsre.client.utg.missions.tasks;

import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.utg.SpeechBubble;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;
import com.google.gwt.user.client.ui.Widget;
import java.util.Map;

/**
 * User: beat
 * Date: 17.02.2010
 * Time: 20:03:23
 */
public class CreateCommandTask extends Task {
    private ItemType itemType;
    private Class<? extends BaseCommand> commandClass;

    public CreateCommandTask(String html, String itemTypeName, Class<? extends BaseCommand> commandClass) throws NoSuchItemTypeException {
        super(html);
        this.commandClass = commandClass;
        itemType = ItemContainer.getInstance().getItemType(itemTypeName);
    }

    @Override
    public String getName() {
        return "Create command: " + itemType.getName();
    }

    @Override
    public void run() {
        Widget widget = null;
        for (Map.Entry<ItemType, Widget> entry : Game.cockpitPanel.getBuildupItemPanel().getItemTypesToBuild().entrySet()) {
            if(entry.getKey().equals(itemType)) {
                widget = entry.getValue();
                break;
            }
        }

        if(widget == null) {
            throw new IllegalArgumentException(this + " no buildup button find for " + itemType);
        }

        int x = widget.getAbsoluteLeft() + widget.getOffsetWidth() / 2;
        int y = widget.getAbsoluteTop();
        setSpeechBubble(new SpeechBubble(x, y, getHtml(), true));
    }

    public Class<? extends BaseCommand> getCommandClass() {
        return commandClass;
    }

    public ItemType getItemType() {
        return itemType;
    }
}