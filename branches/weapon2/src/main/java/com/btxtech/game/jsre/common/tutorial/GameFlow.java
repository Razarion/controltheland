package com.btxtech.game.jsre.common.tutorial;

import java.io.Serializable;

/**
 * User: beat
 * Date: 17.01.2012
 * Time: 22:00:26
 */
public class GameFlow implements Serializable {
    public enum Type {
        START_NEXT_LEVEL_TASK_TUTORIAL,
        START_REAL_GAME
    }

    private Type type;
    private Integer nextTutorialLevelTaskId;

    /**
     * Used by GWT
     */
    GameFlow() {

    }

    public GameFlow(Type type, Integer nextTutorialLevelTaskId) {
        this.type = type;
        this.nextTutorialLevelTaskId = nextTutorialLevelTaskId;
    }

    public Type getType() {
        return type;
    }

    public int getNextTutorialLevelTaskId() {
        return nextTutorialLevelTaskId;
    }
}
