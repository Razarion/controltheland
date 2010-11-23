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

package com.btxtech.game.wicket.pages.mgmt.tutorial.hint;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 07.11.2010
 * Time: 21:45:23
 */
public class TerrainPositionSpeechBubbleHintConfigPanel extends Panel {
    public TerrainPositionSpeechBubbleHintConfigPanel(String id) {
        super(id);
        add(new CheckBox("closeOnTaskEnd"));
        add(new TextField<Integer>("position.x"));
        add(new TextField<Integer>("position.y"));
        add(new TextArea("html"));
    }
}
