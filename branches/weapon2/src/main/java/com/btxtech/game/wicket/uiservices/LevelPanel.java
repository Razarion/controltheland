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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.03.2011
 * Time: 18:44:15
 */
public class LevelPanel extends Panel {
    @SpringBean
    private UserGuidanceService userGuidanceService;

    public LevelPanel(final String id) {
        super(id);
        add(new TextField<>("levelId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                if (getDefaultModelObject() != null) {
                    return ((DbLevel) getDefaultModelObject()).getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                if (integer != null) {
                    setDefaultModelObject(userGuidanceService.getDbLevel(integer));
                } else {
                    setDefaultModelObject(null);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }
}
