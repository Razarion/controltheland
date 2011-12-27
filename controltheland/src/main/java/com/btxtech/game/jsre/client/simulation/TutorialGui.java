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

import com.btxtech.game.jsre.client.ImageHandler;
import com.btxtech.game.jsre.client.cockpit.Cockpit;
import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.client.utg.ImageSizeCallback;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * User: beat
 * Date: 21.07.2010
 * Time: 20:26:29
 */
public class TutorialGui {
    private HTML taskText;
    private Image finishImage;
    private Timer timer;

    public TutorialGui() {
        taskText = new HTML();
        taskText.getElement().getStyle().setProperty("left", "10px");
        taskText.getElement().getStyle().setProperty("top", "10px");
        MapWindow.getAbsolutePanel().add(taskText);
        taskText.getElement().getStyle().setProperty("position", "absolute");
        taskText.getElement().getStyle().setFontSize(2.5, Style.Unit.EM);
        taskText.getElement().getStyle().setColor("#CCCCCC");
    }

    public void showFinishImage(int imageId, int duration) {
        removeFinishImage();
        finishImage = ImageHandler.getTutorialImage(imageId, new ImageSizeCallback() {
            @Override
            public void onImageSize(Image image, int width, int height) {
                centerImage(image, width, height);
            }
        });
        MapWindow.getAbsolutePanel().add(finishImage, 0, 0);
        finishImage.getElement().getStyle().setZIndex(Constants.Z_INDEX_SPEECH_BUBBLE);
        if (finishImage.getWidth() > 0 && finishImage.getHeight() > 0) {
            centerImage(finishImage, finishImage.getWidth(), finishImage.getHeight());
        }

        flashFinishImage(duration);
    }

    private void centerImage(Image image, int width, int height) {
        int left = (TerrainView.getInstance().getViewWidth() - width) / 2;
        int top = (TerrainView.getInstance().getViewHeight() - height - Cockpit.HEIGHT) / 2;
        if(left < 0) {
            left = 0;
        }
        if(top < 0) {
            top = 0;
        }
        MapWindow.getAbsolutePanel().setWidgetPosition(image, left, top);
    }

    private void removeFinishImage() {
        if (finishImage != null) {
            MapWindow.getAbsolutePanel().remove(finishImage);
            finishImage = null;
        }
    }

    private void flashFinishImage(int delayMillis) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer() {
            @Override
            public void run() {
                if (timer != null) {
                    timer.cancel();
                }
                timer = null;
                removeFinishImage();
            }
        };
        timer.schedule(delayMillis);
    }


    public void setTaskText(String tutorialText) {
        this.taskText.setText(tutorialText);
    }

    public void cleanup() {
        if (taskText != null) {
            MapWindow.getAbsolutePanel().remove(taskText);
            taskText = null;
        }
        removeFinishImage();
    }
}