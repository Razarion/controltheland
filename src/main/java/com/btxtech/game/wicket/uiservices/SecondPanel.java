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

import com.btxtech.game.jsre.common.ClientDateUtil;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class SecondPanel extends Panel {
    public SecondPanel(String id) {
        super(id);
        add(new TextField<>("seconds", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                Long timeMs = (Long) getDefaultModelObject();
                if (timeMs != null) {
                    return (int) (timeMs / ClientDateUtil.MILLIS_IN_SECOND);
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer minutes) {
                if(minutes != null) {
                    setDefaultModelObject(minutes * ClientDateUtil.MILLIS_IN_SECOND);
                }  else {
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
