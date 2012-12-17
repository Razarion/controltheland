package com.btxtech.game.jsre.client.utg.tip.tiptask;

import com.btxtech.game.jsre.client.utg.tip.GameTipManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: beat
 * Date: 17.12.12
 * Time: 12:23
 */
public class TipTaskContainer {
    private List<AbstractTipTask> abstractTipTasks = new ArrayList<AbstractTipTask>();
    private List<AbstractTipTask> fallbackAbstractTipTasks = new ArrayList<AbstractTipTask>();
    private List<AbstractTipTask> current;
    private GameTipManager gameTipManager;
    private int currentTaskIndex = 0;

    public TipTaskContainer(GameTipManager gameTipManager) {
        this.gameTipManager = gameTipManager;
        current = abstractTipTasks;
    }

    public void add(AbstractTipTask abstractTipTask) {
        abstractTipTasks.add(abstractTipTask);
        abstractTipTask.setGameTipManager(gameTipManager);
    }

    public void addFallback(AbstractTipTask abstractTipTask) {
        fallbackAbstractTipTasks.add(abstractTipTask);
        abstractTipTask.setGameTipManager(gameTipManager);
    }

    public AbstractTipTask getCurrentTask() {
        return current.get(currentTaskIndex);
    }

    public void next() {
        currentTaskIndex++;
        if (hasTip() && getCurrentTask().isFulfilled()) {
            next();
        }
    }

    public boolean hasTip() {
        return current != null && currentTaskIndex < current.size();
    }

    public void backtrackTask() {
        backtrackTask(currentTaskIndex - 1);
    }

    private void backtrackTask(int taskIndex) {
        if (taskIndex < 0) {
            currentTaskIndex = 0;
        } else {
            AbstractTipTask task = current.get(taskIndex);
            if (task.isFulfilled()) {
                currentTaskIndex = taskIndex + 1;
            } else {
                backtrackTask(taskIndex - 1);
            }
        }
    }

    public void cleanup() {
        if (hasTip()) {
            getCurrentTask().cleanup();
        }
    }

    public void activateFallback() {
        if (fallbackAbstractTipTasks.isEmpty()) {
            current = null;
        } else {
            current = fallbackAbstractTipTasks;
            currentTaskIndex = 0;
        }
    }
}
