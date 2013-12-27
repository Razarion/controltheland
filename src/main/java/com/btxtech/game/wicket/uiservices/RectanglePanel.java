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

import com.btxtech.game.jsre.client.common.Rectangle;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * User: beat
 * Date: 28.09.2010
 * Time: 18:44:15
 */
public class RectanglePanel extends Panel implements IFormModelUpdateListener {
    private Integer x;
    private Integer y;
    private Integer endX;
    private Integer endY;

    public RectanglePanel(final String id) {
        super(id);

        add(new TextField<Integer>("x", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                Rectangle rectangle = (Rectangle) getDefaultModelObject();
                if (rectangle != null) {
                    return rectangle.getX();
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
        add(new TextField<Integer>("y", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                Rectangle rectangle = (Rectangle) getDefaultModelObject();
                if (rectangle != null) {
                    return rectangle.getY();
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
        add(new TextField<Integer>("endX", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                Rectangle rectangle = (Rectangle) getDefaultModelObject();
                if (rectangle != null) {
                    return rectangle.getEndX();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                endX = integer;
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
        add(new TextField<Integer>("endY", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                Rectangle rectangle = (Rectangle) getDefaultModelObject();
                if (rectangle != null) {
                    return rectangle.getEndY();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                endY = integer;
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));
    }

    @Override
    public void updateModel() {
        if (x == null && y == null && endX == null && endY == null) {
            return;
        }
        Rectangle rectangle;
        if (getDefaultModelObject() != null) {
            rectangle = ((Rectangle) getDefaultModelObject()).copy();
        } else {
            rectangle = new Rectangle(0, 0, 0, 0);
        }

        if (x != null) {
            rectangle.setX(x);
        }
        if (y != null) {
            rectangle.setY(y);
        }
        if (endX != null) {
            rectangle.setEndX(endX);
        }
        if (endY != null) {
            rectangle.setEndY(endY);
        }

        setDefaultModelObject(rectangle);
    }
}
