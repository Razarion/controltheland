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

package com.btxtech.game.wicket.uiservices;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class PercentPanel extends Panel {
    public PercentPanel(String id) {
        super(id);
        add(new TextField<>("percent", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                double doubleValue = (Double) getDefaultModelObject();
                return (int) (doubleValue * 100.0);
            }

            @Override
            public void setObject(Integer integer) {
                setDefaultModelObject((double) integer / 100.0);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }
}
