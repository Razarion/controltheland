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
import com.btxtech.game.jsre.client.common.Rectangle;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * User: beat
 * Date: 28.09.2010
 * Time: 18:44:15
 */
public class RectanglePanel extends Panel implements IFormModelUpdateListener {
    private Model<Integer> startX = new Model<Integer>();
    private Model<Integer> startY = new Model<Integer>();
    private Model<Integer> endX = new Model<Integer>();
    private Model<Integer> endY = new Model<Integer>();

    public RectanglePanel(String id) {
        super(id);
        add(new TextField<Integer>("startX", startX, Integer.class));
        add(new TextField<Integer>("startY", startY, Integer.class));
        add(new TextField<Integer>("endX", endX, Integer.class));
        add(new TextField<Integer>("endY", endY, Integer.class));
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        Rectangle rectangle = (Rectangle) getDefaultModelObject();
        if (rectangle != null) {
            startX.setObject(rectangle.getX());
            startY.setObject(rectangle.getY());
            endX.setObject(rectangle.getEndX());
            endY.setObject(rectangle.getEndY());
        }
        super.onComponentTag(tag);
    }

    @Override
    public void updateModel() {
        Rectangle rectangle = (Rectangle) getDefaultModelObject();
        if (startX.getObject() != null && startY.getObject() != null && endX.getObject() != null && endY.getObject() != null) {
            if (rectangle == null) {
                rectangle = new Rectangle(new Index(startX.getObject(), startY.getObject()), new Index(endX.getObject(), endY.getObject()));
            } else {
                rectangle.setX(startX.getObject());
                rectangle.setY(startY.getObject());
                rectangle.setEndX(endX.getObject());
                rectangle.setEndY(endY.getObject());
            }
            setDefaultModelObject(rectangle);
        }
    }
}
