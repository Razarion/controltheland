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

package com.btxtech.game.jsre.common.tutorial.condition;

import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BaseCommand;

/**
 * User: beat
 * Date: 18.07.2010
 * Time: 21:06:41
 */
public class SendCommandConditionConfig extends AbstractConditionConfig {
    private String commandClass;

    /**
     * Used by GWT
     */
    public SendCommandConditionConfig() {
    }

    public SendCommandConditionConfig(String commandClass) {
        this.commandClass = commandClass;
    }

    public String getCommandClass() {
        return commandClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SendCommandConditionConfig)) return false;

        SendCommandConditionConfig that = (SendCommandConditionConfig) o;

        return commandClass.equals(that.commandClass);
    }

    @Override
    public int hashCode() {
        return commandClass.hashCode();
    }
}
