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

package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.dialogs.RegisterDialog;
import com.btxtech.game.jsre.client.simulation.Simulation;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.utg.MissionTarget;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import java.util.Date;
import java.util.HashMap;

/**
 * User: beat
 * Date: 19.06.2010
 * Time: 18:19:28
 */
public class StartupProbe {
    private final static StartupProbe INSTANCE = new StartupProbe();
    private long startTimeStamp;
    private long runningTimeStamp = 0;

    public static StartupProbe getInstance() {
        return INSTANCE;
    }

    private class Task {
        private StartupTask currentTask;
        private long startTime;
        private long endTime;
        private String failureText;

        public Task(StartupTask startupTask) {
            currentTask = startupTask;
            startTime = System.currentTimeMillis();
            endTime = 0;
        }

        public StartupTask getTask() {
            return currentTask;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public Date getStartTime() {
            return new Date(startTime);
        }

        public void finished() {
            endTime = System.currentTimeMillis();
        }

        public boolean isFinished() {
            return endTime != 0;
        }

        public int getDuration() {
            if (endTime == 0) {
                throw new IllegalStateException("Task not finished: " + currentTask);
            }
            return (int) (endTime - startTime);
        }

        public void failed(String text) {
            finished();
            failureText = text;
        }

        public String getFailureText() {
            return failureText;
        }

        public boolean isFailed() {
            return failureText != null;
        }

        public String toString() {
            if (endTime == 0) {
                return "Task ongoing: " + currentTask + " startTime: " + startTime;
            } else {
                if (isFailed()) {
                    return "Task failed: " + currentTask + " used time: " + getDuration() + " failure text: " + failureText;
                } else {
                    return "Task finished: " + currentTask + " used time: " + getDuration();
                }
            }
        }
    }

    private HashMap<StartupTask, Task> tasks = new HashMap<StartupTask, Task>();
    private Element parent;
    private Element startScreen;

    /**
     * Singleton
     */
    private StartupProbe() {
    }

    public void init() {
        startScreen = DOM.getElementById("startScreen");
        parent = startScreen.getParentElement();

        // Add first task
        Task nativeTask = new Task(StartupTask.getFirstTask());
        startTimeStamp = (long) getNativeCtlStartTime();
        nativeTask.setStartTime(startTimeStamp);
        tasks.put(StartupTask.getFirstTask(), nativeTask);
    }

    public void taskSwitch(StartupTask finishedTask, StartupTask newTask) {
        if (startScreen == null) {
            return;
        }
        taskFinished(finishedTask);
        newTask(newTask);
    }

    public void newTask(StartupTask startupTask) {
        if (startScreen == null) {
            return;
        }
        Task task = tasks.get(startupTask);
        if (task != null) {
            throw new IllegalStateException("Task has already been started: " + startupTask);
        }
        tasks.put(startupTask, new Task(startupTask));
        displayTaskRunning(startupTask);
    }

    public void taskFinished(StartupTask startupTask) {
        if (startScreen == null) {
            return;
        }
        Task task = tasks.get(startupTask);
        if (task == null) {
            throw new IllegalStateException("Task has never been started: " + task);
        }
        task.finished();
        displayTaskFinished(task);
        Connection.getInstance().sandStartUpTaskFinished(task.getTask(), task.getStartTime(), task.getDuration());
        checkForCompletion();
    }

    public void taskFailed(StartupTask startupTask, String text) {
        if (startScreen == null) {
            return;
        }
        Task task = tasks.remove(startupTask);
        if (task == null) {
            throw new IllegalStateException("Task failed: " + task);
        }
        task.failed(text);
        displayTaskFailed(task);

        Connection.getInstance().sandStartUpTaskFailed(task.getTask(), task.getStartTime(), task.getDuration(), task.getFailureText());
        checkForCompletion();
    }

    public void taskFailed(StartupTask startupTask, Throwable throwable) {
        taskFailed(startupTask, throwable.toString());
    }

    private void checkForCompletion() {
        for (StartupTask startupTask : StartupTask.values()) {
            Task task = tasks.get(startupTask);
            if (task == null || !task.isFinished()) {
                return;
            }
        }
        runningTimeStamp = System.currentTimeMillis();
        Connection.getInstance().sendTotalStartupTime(runningTimeStamp - startTimeStamp, runningTimeStamp);
        enableCloseButton();
        hideStartScreen();
        startGame();
    }

    private void startGame() {
        if (Connection.getInstance().getGameInfo().showMissionTargetDialog()) {
            MissionTarget.getInstance().showMissionTargetDialog();
        }
        RegisterDialog.showDialogRepeating();
        Simulation.getInstance().start();
        MapWindow.getInstance().displayVisibleItems();
        InfoPanel.getInstance().updateItemLimit();        
    }

    private void enableCloseButton() {
        com.google.gwt.user.client.Element closeButtonElement = DOM.getElementById("startScreenClose");
        ButtonElement closeButton = (ButtonElement) Element.as(closeButtonElement);
        closeButton.setDisabled(false);
        DOM.setEventListener(closeButtonElement, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                hideStartScreen();
            }
        });
        // connect the foreign element to the GWT event dispatcher
        DOM.sinkEvents(closeButtonElement, Event.ONCLICK);
        closeButtonElement.getStyle().setVisibility(Style.Visibility.VISIBLE);
    }

    private void displayTaskRunning(StartupTask startupTask) {
        // Set bold font for name
        Element nameElement = DOM.getElementById(startupTask.getNameId());
        nameElement.getStyle().setFontWeight(Style.FontWeight.BOLD);
        // Show working image
        Element imageElement = DOM.getElementById(startupTask.getImgIdWorking());
        imageElement.getStyle().setHeight(24, Style.Unit.PX);
        imageElement.getStyle().setWidth(24, Style.Unit.PX);
    }

    private void displayTaskFinished(Task task) {
        // Set normal font for name
        Element nameElement = DOM.getElementById(task.getTask().getNameId());
        nameElement.getStyle().setFontWeight(Style.FontWeight.NORMAL);
        // Hide working image
        Element workingIMage = DOM.getElementById(task.getTask().getImgIdWorking());
        workingIMage.getStyle().setHeight(0, Style.Unit.PX);
        workingIMage.getStyle().setWidth(0, Style.Unit.PX);
        // Show finished image
        Element finishedImage = DOM.getElementById(task.getTask().getImgIdFinished());
        finishedImage.getStyle().setHeight(24, Style.Unit.PX);
        finishedImage.getStyle().setWidth(24, Style.Unit.PX);
        // Display duration
        Element timeElement = DOM.getElementById(task.getTask().getTimeId());
        double duration = task.getDuration() / 1000.0;
        String value = NumberFormat.getFormat("####0.00").format(duration) + "s";
        timeElement.setInnerText(value);
    }

    private void displayTaskFailed(Task task) {
        // Set normal font for name
        Element nameElement = DOM.getElementById(task.getTask().getNameId());
        nameElement.getStyle().setFontWeight(Style.FontWeight.NORMAL);
        nameElement.setInnerHTML(nameElement.getInnerText() + "<br>" + task.getFailureText());
        // Hide working image
        Element workingIMage = DOM.getElementById(task.getTask().getImgIdWorking());
        workingIMage.getStyle().setHeight(0, Style.Unit.PX);
        workingIMage.getStyle().setWidth(0, Style.Unit.PX);
        // Show finished image
        Element finishedImage = DOM.getElementById(task.getTask().getImgIdFailed());
        finishedImage.getStyle().setHeight(24, Style.Unit.PX);
        finishedImage.getStyle().setWidth(24, Style.Unit.PX);
        // Display duration
        Element timeElement = DOM.getElementById(task.getTask().getTimeId());
        double duration = task.getDuration() / 1000.0;
        String value = NumberFormat.getFormat("####0.00").format(duration) + "s";
        timeElement.setInnerText(value);
    }


    private void hideStartScreen() {
        parent.removeChild(startScreen);
    }

    public void showStartScreen() {
        parent.insertFirst(startScreen);
    }

    public long getRunningTimeStamp() {
        return runningTimeStamp;
    }

    public boolean isRunning() {
        return runningTimeStamp != 0;
    }

    private native double getNativeCtlStartTime() /*-{
      return $wnd.ctlStartTime;
    }-*/;
}
