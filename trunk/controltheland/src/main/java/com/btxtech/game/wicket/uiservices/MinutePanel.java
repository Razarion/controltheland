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
import com.btxtech.game.services.item.ItemService;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 17.01.2011
 * Time: 18:44:15
 */
public class MinutePanel extends Panel {
    @SpringBean
    private ItemService itemService;

    public MinutePanel(String id) {
        super(id);
        add(new TextField<>("minutes", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                long timeMs = (Long) getDefaultModelObject();
                return (int) (timeMs / ClientDateUtil.MILLIS_IN_MINUTE);
            }

            @Override
            public void setObject(Integer minutes) {
                setDefaultModelObject(minutes * ClientDateUtil.MILLIS_IN_MINUTE);
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }
}
