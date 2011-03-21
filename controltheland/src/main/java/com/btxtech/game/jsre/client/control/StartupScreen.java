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

package com.btxtech.game.jsre.client.control;

import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 19.06.2010
 * Time: 18:19:28
 */
public class StartupScreen implements StartupProgressListener {
    private static final double FADE_STEP = 0.15;
    private static final int SCHEDULE = 50;
    private static final String WORKING_IMG_SRC = "resources/com.btxtech.game.wicket.pages.Game/working.gif";
    private static final String FINISHED_IMG_SRC = "resources/com.btxtech.game.wicket.pages.Game/finished.png";
    private static final String FAILED_IMG_SRC = "resources/com.btxtech.game.wicket.pages.Game/failed.png";
    private static final String TABLE_ID = "startupTaskTable";
    private static final String MINI_LOADING_TABLE_ID = "miniLoading";
    private final static StartupScreen INSTANCE = new StartupScreen();
    private Timer fadeTimer;
    private double currentFade;
    private Runnable afterFade;
    private Element parent;
    private Element startScreen;
    private ButtonElement closeButton;
    private com.google.gwt.user.client.Element closeButtonElement;
    private StartupSeq startupSeq;

    public static StartupScreen getInstance() {
        return INSTANCE;
    }


    /**
     * Singleton
     */
    private StartupScreen() {
    }

    public void setupScreen(StartupSeq startupSeq) {
        StartupSeq oldStartupSeq = this.startupSeq;
        this.startupSeq = startupSeq;
        if (parent == null) {
            startScreen = DOM.getElementById("startScreen");
            parent = startScreen.getParentElement();
            setupCloseButton();
        }
        hideCloseButton();
        attachStartScreen();
        if (!startupSeq.isCold()) {
            setupTable(startupSeq, oldStartupSeq);
        }
        showMiniLoading();
    }

    private void showMiniLoading() {
        Element miniLoadingTable = DOM.getElementById(MINI_LOADING_TABLE_ID);
        miniLoadingTable.getStyle().setVisibility(Style.Visibility.VISIBLE);
        Element detailTable = DOM.getElementById(TABLE_ID);
        detailTable.getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    private void showTaskTableLoading() {
        Element miniLoadingTable = DOM.getElementById(MINI_LOADING_TABLE_ID);
        miniLoadingTable.getStyle().setVisibility(Style.Visibility.HIDDEN);
        Element detailTable = DOM.getElementById(TABLE_ID);
        detailTable.getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    private void setupTable(StartupSeq newStartupSeq, StartupSeq oldStartupSeq) {
        // Clear table
        Element tableSectionElement = null;
        for (StartupTaskEnum startupTaskEnum : oldStartupSeq.getAbstractStartupTaskEnum()) {
            Element tdElement = DOM.getElementById(startupTaskEnum.getStartupTaskEnumHtmlHelper().getNameId());
            Element trElement = tdElement.getParentElement();
            if (tableSectionElement == null) {
                tableSectionElement = trElement.getParentElement();
            }
            tableSectionElement.removeChild(trElement);
        }

        if (tableSectionElement == null) {
            throw new IllegalStateException("Can not modify startup table. No section element found");
        }

        // Setup table
        for (StartupTaskEnum startupTaskEnum : newStartupSeq.getAbstractStartupTaskEnum()) {
            Element trElement = DOM.createTR();
            tableSectionElement.appendChild(trElement);
            // Finished Image
            Element imageTdElement = DOM.createTD();
            com.google.gwt.user.client.Element finishedImage = DOM.createImg();
            finishedImage.setId(startupTaskEnum.getStartupTaskEnumHtmlHelper().getImgIdFinished());
            DOM.setImgSrc(finishedImage, FINISHED_IMG_SRC);
            finishedImage.getStyle().setHeight(0, Style.Unit.PX);
            finishedImage.getStyle().setWidth(0, Style.Unit.PX);
            imageTdElement.appendChild(finishedImage);
            trElement.appendChild(imageTdElement);
            // Failing Image
            com.google.gwt.user.client.Element failingImage = DOM.createImg();
            failingImage.setId(startupTaskEnum.getStartupTaskEnumHtmlHelper().getImgIdFailed());
            failingImage.getStyle().setHeight(0, Style.Unit.PX);
            failingImage.getStyle().setWidth(0, Style.Unit.PX);
            DOM.setImgSrc(failingImage, FAILED_IMG_SRC);
            imageTdElement.appendChild(failingImage);
            // Working Image
            com.google.gwt.user.client.Element workingImage = DOM.createImg();
            workingImage.setId(startupTaskEnum.getStartupTaskEnumHtmlHelper().getImgIdWorking());
            workingImage.getStyle().setHeight(0, Style.Unit.PX);
            workingImage.getStyle().setWidth(0, Style.Unit.PX);
            DOM.setImgSrc(workingImage, WORKING_IMG_SRC);
            imageTdElement.appendChild(workingImage);
            // Text
            Element textTdElement = DOM.createTD();
            textTdElement.setInnerText(startupTaskEnum.getStartupTaskEnumHtmlHelper().getNiceText());
            textTdElement.setId(startupTaskEnum.getStartupTaskEnumHtmlHelper().getNameId());
            trElement.appendChild(textTdElement);
            // Time
            Element timeTdElement = DOM.createTD();
            timeTdElement.setInnerHTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            timeTdElement.setId(startupTaskEnum.getStartupTaskEnumHtmlHelper().getTimeId());
            trElement.appendChild(timeTdElement);
        }
    }

    private void setupCloseButton() {
        closeButtonElement = DOM.getElementById("startScreenClose");
        closeButton = (ButtonElement) Element.as(closeButtonElement);
        DOM.setEventListener(closeButtonElement, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                hideStartScreen();
            }
        });
        // connect the foreign element to the GWT event dispatcher
        DOM.sinkEvents(closeButtonElement, Event.ONCLICK);
    }

    public void hideCloseButton() {
        closeButton.setDisabled(true);
        closeButtonElement.getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    public void showCloseButton() {
        closeButton.setDisabled(false);
        closeButtonElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    public void displayTaskRunning(StartupTaskEnum taskEnum) {
        // Set bold font for name
        Element nameElement = DOM.getElementById(taskEnum.getStartupTaskEnumHtmlHelper().getNameId());
        nameElement.getStyle().setFontWeight(Style.FontWeight.BOLD);
        // Show working image
        Element imageElement = DOM.getElementById(taskEnum.getStartupTaskEnumHtmlHelper().getImgIdWorking());
        imageElement.getStyle().setHeight(24, Style.Unit.PX);
        imageElement.getStyle().setWidth(24, Style.Unit.PX);
    }

    public void displayTaskFinished(AbstractStartupTask task) {
        // Set normal font for name
        Element nameElement = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getNameId());
        nameElement.getStyle().setFontWeight(Style.FontWeight.NORMAL);
        // Hide working image
        Element workingImage = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getImgIdWorking());
        workingImage.getStyle().setHeight(0, Style.Unit.PX);
        workingImage.getStyle().setWidth(0, Style.Unit.PX);
        // Show finished image
        Element finishedImage = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getImgIdFinished());
        finishedImage.getStyle().setHeight(24, Style.Unit.PX);
        finishedImage.getStyle().setWidth(24, Style.Unit.PX);
        // Display duration
        Element timeElement = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getTimeId());
        double duration = task.getDuration() / 1000.0;
        String value = NumberFormat.getFormat("####0.00").format(duration) + "s";
        timeElement.setInnerText(value);
    }

    public void displayTaskFailed(AbstractStartupTask task, String failureText) {
        // Set normal font for name
        Element nameElement = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getNameId());
        nameElement.getStyle().setFontWeight(Style.FontWeight.NORMAL);
        nameElement.setInnerHTML(nameElement.getInnerText() + "<br>" + failureText);
        // Hide working image
        Element workingIMage = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getImgIdWorking());
        workingIMage.getStyle().setHeight(0, Style.Unit.PX);
        workingIMage.getStyle().setWidth(0, Style.Unit.PX);
        // Show finished image
        Element finishedImage = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getImgIdFailed());
        finishedImage.getStyle().setHeight(24, Style.Unit.PX);
        finishedImage.getStyle().setWidth(24, Style.Unit.PX);
        // Display duration
        Element timeElement = DOM.getElementById(task.getTaskEnum().getStartupTaskEnumHtmlHelper().getTimeId());
        double duration = task.getDuration() / 1000.0;
        String value = NumberFormat.getFormat("####0.00").format(duration) + "s";
        timeElement.setInnerText(value);
    }


    private void detachStartScreen() {
        parent.removeChild(startScreen);
    }

    private void attachStartScreen() {
        if (!parent.getFirstChild().equals(startScreen)) {
            parent.insertFirst(startScreen);
        }
    }

    public void showStartScreen() {
        if (!parent.getFirstChild().equals(startScreen)) {
            parent.insertFirst(startScreen);
        }
        showTaskTableLoading();
        setOpacity(1.0);
    }

    private void hideStartScreen() {
        setOpacity(0.0);
        detachStartScreen();
    }


    private void stopFade() {
        if (fadeTimer != null) {
            fadeTimer.cancel();
            fadeTimer = null;
        }
    }

    private void startFadeOut() {
        stopFade();
        currentFade = 0;
        fadeTimer = new Timer() {
            @Override
            public void run() {
                currentFade += FADE_STEP;
                if (currentFade >= 1.0) {
                    stopFade();
                    setOpacity(1.0);
                    if (afterFade != null) {
                        afterFade.run();
                    }
                } else {
                    setOpacity(currentFade);
                }
            }
        };
        fadeTimer.scheduleRepeating(SCHEDULE);
    }

    private void startFadeIn() {
        stopFade();
        currentFade = 1.0;
        fadeTimer = new Timer() {
            @Override
            public void run() {
                currentFade -= FADE_STEP;
                if (currentFade <= 0.0) {
                    stopFade();
                    setOpacity(0.0);
                    detachStartScreen();
                } else {
                    setOpacity(currentFade);
                }
            }
        };
        fadeTimer.scheduleRepeating(SCHEDULE);
    }

    private void setOpacity(double opacity) {
        startScreen.getStyle().setProperty("filter", "alpha(opacity=" + (int) (opacity * 100.0) + ")");
        startScreen.getStyle().setProperty("opacity", Double.toString(opacity));
    }

    public void fadeOut(Runnable afterFade) {
        this.afterFade = afterFade;
        attachStartScreen();
        startFadeOut();
    }

    @Override
    public void onStart(StartupSeq startupSeq) {
        setupScreen(startupSeq);
    }

    @Override
    public void onNextTask(StartupTaskEnum taskEnum) {
        displayTaskRunning(taskEnum);
    }

    @Override
    public void onTaskFinished(AbstractStartupTask task) {
        displayTaskFinished(task);
    }

    @Override
    public void onTaskFailed(AbstractStartupTask task, String error) {
        showTaskTableLoading();
        displayTaskFailed(task, error);
    }

    @Override
    public void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime) {
        startFadeIn();
    }

    @Override
    public void onStartupFailed(Collection<StartupTaskInfo> taskInfo, long totalTime) {
        showCloseButton();
    }
}
