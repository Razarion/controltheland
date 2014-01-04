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

package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.03.2011
 * Time: 18:44:15
 */
public class LevelReadonlyPanel extends Panel {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public LevelReadonlyPanel(String id) {
        this(id, null);
    }

    public LevelReadonlyPanel(String id, IModel<Integer> model) {
        super(id, model);
        add(new Label("levelName", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if(getDefaultModelObject() == null) {
                    return "-";
                }
                DbLevel dbLevel = userGuidanceService.getDbLevel((Integer) getDefaultModelObject());
                if (dbLevel != null) {
                    return dbLevel.getName();
                } else {
                    return "-";
                }
            }
        }));
    }
}
