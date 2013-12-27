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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * User: beat
 * Date: Oct 3, 2009
 * Time: 5:46:17 PM
 */
public class ColorField {
    public static Label create(String id, String color) {
        AttributeModifier bgColor = new AttributeModifier("bgcolor", color);
        Label label = new Label(id, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        label.setEscapeModelStrings(false);
        label.add(bgColor);
        return label;
    }

    public static Label createColorLabel(String id, Model model) {
        Label label = new Label(id, model);
        label.setEscapeModelStrings(false);
        return label;
    }

    public static void setColorModifier(Label label, String color) {
        AttributeModifier bgColor = new AttributeModifier("bgcolor", color);
        label.add(bgColor);
    }


}
