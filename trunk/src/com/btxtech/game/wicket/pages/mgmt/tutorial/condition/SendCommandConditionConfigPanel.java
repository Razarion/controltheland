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

package com.btxtech.game.wicket.pages.mgmt.tutorial.condition;

import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.BuilderCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.FactoryCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.LoadContainCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoneyCollectCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UnloadContainerCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.UpgradeCommand;
import com.btxtech.game.services.tutorial.condition.DbSendCommandConditionConfig;
import java.util.Arrays;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 28.07.2010
 * Time: 14:37:45
 */
public class SendCommandConditionConfigPanel extends Panel {
    public static final String[] ALL_COMMANDS = {
            AttackCommand.class.toString(),
            BuilderCommand.class.toString(),
            FactoryCommand.class.toString(),
            LoadContainCommand.class.toString(),
            MoneyCollectCommand.class.toString(),
            MoveCommand.class.toString(),
            UnloadContainerCommand.class.toString(),
            UpgradeCommand.class.toString(),
    };
    private ConditionWrapperPanel conditionWrapperPanel;

    public SendCommandConditionConfigPanel(String id, final ConditionWrapperPanel conditionWrapperPanel) {
        super(id);
        this.conditionWrapperPanel = conditionWrapperPanel;
        add(new DropDownChoice<String>("command", new IModel<String>() {
            @Override
            public String getObject() {
                return ((DbSendCommandConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).getCommandClass();
            }

            @Override
            public void setObject(String value) {
                ((DbSendCommandConditionConfig) conditionWrapperPanel.getDbAbstractConditionConfig()).setCommandClass(value);
            }

            @Override
            public void detach() {
                // Ignored
            }
        }, Arrays.asList(ALL_COMMANDS)));
    }

    @Override
    public boolean isVisible() {
        return conditionWrapperPanel.getDbAbstractConditionConfig() instanceof DbSendCommandConditionConfig;
    }

}
