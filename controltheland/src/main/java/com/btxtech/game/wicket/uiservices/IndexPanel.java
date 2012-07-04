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

import com.btxtech.game.jsre.client.common.Index;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 08.03.2011
 * Time: 18:44:15
 */
public class IndexPanel extends Panel implements IFormModelUpdateListener {
    private Integer x;
    private Integer y;

    public IndexPanel(final String id) {
        super(id);

        add(new TextField<>("x", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                Index index = (Index) getDefaultModelObject();
                if (index != null) {
                    return index.getX();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                x = integer;
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new TextField<>("y", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                Index index = (Index) getDefaultModelObject();
                if (index != null) {
                    return index.getY();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                y = integer;
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }

    @Override
    public void updateModel() {
        if (x == null && y == null) {
            return;
        }
        Index index;
        if (getDefaultModelObject() != null) {
            index = ((Index) getDefaultModelObject()).getCopy();
        } else {
            index = new Index(0, 0);
        }

        if (x != null) {
            index.setX(x);
        }
        if (y != null) {
            index.setY(y);
        }

        setDefaultModelObject(index);
    }
}
