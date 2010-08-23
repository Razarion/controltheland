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

import com.btxtech.game.jsre.client.ExtendedAbsolutePanel;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * User: beat
 * Date: 21.07.2010
 * Time: 20:26:29
 */
public class TutorialGui {
    private static final int LETTER_DELAY = 70;
    private static final int FLASH_COUNT = 7;
    private HTML html;
    private String taskText;
    private String stepText;
    private AbsolutePanel image;
    private Timer timer;
    private int taskIndex;
    private int stepIndex;
    private int flashCount;

    public TutorialGui() {
        ExtendedAbsolutePanel absolutePanel = new ExtendedAbsolutePanel();
        absolutePanel.getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        absolutePanel.setSize("360px", "100%");
        absolutePanel.getElement().getStyle().setProperty("right", "0");
        absolutePanel.getElement().getStyle().setProperty("top", "0");
        MapWindow.getAbsolutePanel().add(absolutePanel);
        absolutePanel.getElement().getStyle().setProperty("position", "absolute");
        absolutePanel.getElement().getStyle().setProperty("background", "url(images/tutorial.jpg) no-repeat");
        GwtCommon.stopPropagation(absolutePanel);

        image = new AbsolutePanel();
        absolutePanel.add(image, 54, 95);
        image.setPixelSize(250, 200);
        html = new HTML();
        absolutePanel.add(html, 60, 330);
        html.setPixelSize(250, 200);
    }

    public void setTaskText(String text) {
        taskText = text;
        taskIndex = 0;
        displayAnimatedText();
    }

    public void setStepText(String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        stepText = text;
        stepIndex = 0;
        displayAnimatedText();
    }

    public void showFinishedText(String text) {
        taskText = text;
        stepText = null;
        flashCount = 0;
        displayFlashingText();
    }

    public void setImage(Integer imageId) {
        image.clear();
        if (imageId != null) {
            image.add(ImageHandler.getTutorialImage(imageId));
        }
    }

    private void displayAnimatedText() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer() {
            @Override
            public void run() {
                StringBuilder builder = new StringBuilder();
                builder.append("<br>");
                builder.append("<font size='+1'>");
                builder.append(taskText.substring(0, taskIndex));
                builder.append("</font>");
                if (stepText != null) {
                    builder.append("<br>");
                    builder.append("<br>");
                    builder.append(stepText.substring(0, stepIndex));
                }
                html.setHTML(builder.toString());
                html.getElement().getStyle().setProperty("fontFamily", "Impact,Charcoal,sans-serif");
                html.getElement().getStyle().setColor("#00EE00");
                if (taskIndex >= taskText.length()) {
                    stepIndex++;
                    if (stepText == null) {
                        timer.cancel();
                        timer = null;
                        return;
                    }
                } else {
                    taskIndex++;
                }
                if (stepText != null && stepIndex > stepText.length()) {
                    timer.cancel();
                    timer = null;
                }
            }
        };
        timer.scheduleRepeating(LETTER_DELAY);
    }

    private void displayFlashingText() {
        if (timer != null) {
            timer.cancel();
        }
        flashCount = 0;
        timer = new Timer() {
            @Override
            public void run() {
                StringBuilder builder = new StringBuilder();
                if (flashCount % 2 == 0) {
                    builder.append("<br>");
                    builder.append("<font size='+1'>");
                    builder.append(taskText);
                    builder.append("</font>");
                }
                html.setHTML(builder.toString());
                html.getElement().getStyle().setProperty("fontFamily", "Impact,Charcoal,sans-serif");
                html.getElement().getStyle().setColor("#00EE00");
                flashCount++;
                if (flashCount >= FLASH_COUNT) {
                    timer.cancel();
                    timer = null;
                }
            }
        };
        timer.scheduleRepeating(LETTER_DELAY);
    }


}
