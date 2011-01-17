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

package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import com.btxtech.game.wicket.uiservices.BaseItemTypePanel;
import com.btxtech.game.wicket.uiservices.RectanglePanel;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 16.01.2011
 * Time: 22:28:24
 */
public class RealGameLevelEditor extends Panel {

    public RealGameLevelEditor(String id) {
        super(id);
        // Condition
        add(new ConditionConfigPanel("dbConditionConfig"));

        // Scope
        add(new TextField("houseSpace"));
        add(new TextField("itemSellFactor"));

        // Reward
        add(new TextField("deltaMoney"));

        // Create Base
        add(new CheckBox("createRealBase"));
        add(new BaseItemTypePanel("startItemType"));
        add(new RectanglePanel("startRectangle"));
        add(new TextField("startItemFreeRange"));
    }
}
