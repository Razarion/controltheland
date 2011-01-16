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

import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.utg.DbRealGameLevel;
import com.btxtech.game.wicket.pages.mgmt.condition.ConditionConfigPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 16.01.2011
 * Time: 22:28:24
 */
public class RealGameLevelEditor extends Panel {
    @SpringBean
    private ItemService itemService;
    private Log log = LogFactory.getLog(DbLevelEditor.class);

    public RealGameLevelEditor(String id, DbRealGameLevel dbRealGameLevel) {
        super(id);
        add(new ConditionConfigPanel("dbConditionConfig"));
    }
}
