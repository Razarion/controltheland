package com.btxtech.game.services.utg;

import com.btxtech.game.services.common.SimpleCrudChild;

import java.io.Serializable;

/**
 * User: beat
 * Date: 29.01.2012
 * Time: 17:10:51
 */
public class LevelQuest extends SimpleCrudChild {
    private DbLevelTask dbLevelTask;
    private boolean done;
    private boolean active;
    private boolean blocked;

    public LevelQuest(DbLevelTask dbLevelTask, boolean done, boolean active, boolean blocked) {
        this.dbLevelTask = dbLevelTask;
        this.done = done;
        this.active = active;
        this.blocked = blocked;
    }

    @Override
    public Serializable getId() {
        return dbLevelTask.getId();
    }

    public DbLevelTask getDbLevelTask() {
        return dbLevelTask;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isBlocked() {
        return blocked;
    }

}
