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

import com.btxtech.game.jsre.client.ClientGlobalServices;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.jsre.common.perfmon.TimerPerfmon;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * User: beat
 * Date: 19.06.2010
 * Time: 18:19:28
 */
public class StartupScreen implements StartupProgressListener {
    private final static StartupScreen INSTANCE = new StartupScreen();
    private static final double FADE_STEP = 0.15;
    private static final int SCHEDULE = 50;
    private static final String PROGRESS_TABLE_ID = "progressTable";
    private static final String PROGRESS_BAR_ID = "progressBar";
    public static final String PROGRESS_TEXT_ID = "progressText";
    private static final int MAX_PROGRESS = 30;
    private Timer fadeTimer;
    private double currentFade;
    private Runnable afterFade;
    private Element parent;
    private Element startScreen;
    private StartupSeq startupSeq;
    private Collection<StartupTaskEnum> remainingTasks;

    public static StartupScreen getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private StartupScreen() {
    }

    public void setupScreen(StartupSeq startupSeq) {
        this.startupSeq = startupSeq;
        remainingTasks = new TreeSet<StartupTaskEnum>(Arrays.asList(startupSeq.getAbstractStartupTaskEnum()));
        if (parent == null) {
            startScreen = DOM.getElementById("startScreen");
            parent = startScreen.getParentElement();
        }
        attachStartScreen();
    }

    private void detachStartScreen() {
        parent.removeChild(startScreen);
    }

    private void attachStartScreen() {
        if (!parent.getFirstChild().equals(startScreen)) {
            parent.insertFirst(startScreen);
        }
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
        fadeTimer = new TimerPerfmon(PerfmonEnum.STARTUP_FADE_OUT) {
            @Override
            public void runPerfmon() {
                AnimationScheduler.get().requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
                    @Override
                    public void execute(double timestamp) {
                        if (currentFade >= 1.0) {
                            // Timer my be faster than AnimationScheduler can execute
                            return;
                        }
                        currentFade += FADE_STEP;
                        if (currentFade >= 1.0) {
                            stopFade();
                            setOpacity(1.0);
                            if (afterFade != null) {
                                afterFade.run();
                                afterFade = null;
                            }
                        } else {
                            setOpacity(currentFade);
                        }
                    }
                }, startScreen);
            }
        };
        fadeTimer.scheduleRepeating(SCHEDULE);
    }

    private void startFadeIn() {
        stopFade();
        currentFade = 1.0;
        fadeTimer = new TimerPerfmon(PerfmonEnum.STARTUP_FADE_IN) {
            @Override
            public void runPerfmon() {
                AnimationScheduler.get().requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
                    @Override
                    public void execute(double timestamp) {
                        if (currentFade <= 0.0) {
                            // Timer my be faster than AnimationScheduler can execute
                            return;
                        }
                        currentFade -= FADE_STEP;
                        if (currentFade <= 0.0) {
                            stopFade();
                            setOpacity(0.0);
                            detachStartScreen();
                        } else {
                            setOpacity(currentFade);
                        }
                    }
                }, startScreen);
            }
        };
        fadeTimer.scheduleRepeating(SCHEDULE);
    }

    private void setOpacity(double opacity) {
        startScreen.getStyle().setProperty("filter", "alpha(opacity=" + (int) (opacity * 100.0) + ")");
        startScreen.getStyle().setProperty("opacity", Double.toString(opacity));
    }


    public void fadeOutAndStart(final GameStartupSeq startupSeq) {
        this.afterFade = new Runnable() {
            @Override
            public void run() {
                ClientGlobalServices.getInstance().getClientRunner().start(startupSeq);
            }
        };
        attachStartScreen();
        startFadeOut();
    }

    @Override
    public void onStart(StartupSeq startupSeq) {
        setupScreen(startupSeq);
    }

    @Override
    public void onNextTask(StartupTaskEnum taskEnum) {
        remainingTasks.remove(taskEnum);
        double progress = 1 - (double) remainingTasks.size() / (double) startupSeq.getAbstractStartupTaskEnum().length;

        Element progressBar = DOM.getElementById(PROGRESS_BAR_ID);
        progressBar.getStyle().setWidth(progress * MAX_PROGRESS, Style.Unit.EM);

        Element progressText = DOM.getElementById(PROGRESS_TEXT_ID);
        progressText.setInnerText(taskEnum.getStartupTaskEnumHtmlHelper().getI18nText());
    }

    @Override
    public void onTaskFinished(AbstractStartupTask task) {
    }

    @Override
    public void onTaskFailed(AbstractStartupTask task, String error, Throwable t) {
    }

    @Override
    public void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime) {
        startFadeIn();
    }

    @Override
    public void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime) {
        Element progressTable = DOM.getElementById(PROGRESS_TABLE_ID);
        progressTable.getStyle().setBackgroundColor("#FF0000");
        progressTable.getStyle().setColor("#FFFFFF");
        progressTable.getStyle().setFontWeight(Style.FontWeight.BOLD);
        StringBuilder builder = new StringBuilder();
        builder.append("<h1 style='color: #FFFFFF;'>Start Game failed!</h1>");
        for (StartupTaskInfo startupTaskInfo : taskInfo) {
            if (startupTaskInfo.getError() != null) {
                builder.append("<p>");
                builder.append("Task: ");
                builder.append(startupTaskInfo.getTaskEnum().getStartupTaskEnumHtmlHelper().getNiceText());
                builder.append("</p>");
                builder.append("<p>");
                builder.append(startupTaskInfo.getError());
                builder.append("</p>");
            }
        }
        progressTable.setInnerHTML("<span>" + builder.toString() + "</span>");
    }
}
