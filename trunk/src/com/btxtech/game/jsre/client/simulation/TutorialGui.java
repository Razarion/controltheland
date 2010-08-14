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

package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * User: beat
 * Date: 21.07.2010
 * Time: 20:26:29
 */
public class TutorialGui {
    private HTML html;
    private String taskText;
    private String stepText;

    public TutorialGui() {
        AbsolutePanel absolutePanel = new AbsolutePanel();
        absolutePanel.setSize("350px", "100%");
        absolutePanel.getElement().getStyle().setProperty("right", "0");
        absolutePanel.getElement().getStyle().setProperty("top", "0");
        MapWindow.getAbsolutePanel().add(absolutePanel);
        absolutePanel.getElement().getStyle().setProperty("position", "absolute");
        absolutePanel.getElement().getStyle().setProperty("background", "url(images/tutorial.jpg) no-repeat");

        html = new HTML();
        absolutePanel.add(html, 60, 150);
        html.setPixelSize(250, 220);
    }

    public void setTaskText(String text) {
        taskText = text;
        setupHtmlText();
    }

    public void setStepText(String text) {
        stepText = text;
        setupHtmlText();
    }

    public void showFinishedText(String text) {
        taskText = text;
        stepText = null;
        setupHtmlText();
    }

    private void setupHtmlText() {
        StringBuilder builder = new StringBuilder();
        builder.append("<br>");
        builder.append("<font size='+1'>");
        builder.append(taskText);
        builder.append("</font>");
        if(stepText != null) {
            builder.append("<br>");
            builder.append("<br>");            
            builder.append(stepText);
        }
        html.setHTML(builder.toString());
        html.getElement().getStyle().setProperty("fontFamily", "Impact,Charcoal,sans-serif");
        html.getElement().getStyle().setColor("#FFFFFF");
    }

}
