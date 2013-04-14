package com.btxtech.game.services.history;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: beat
 * Date: 13.04.13
 * Time: 01:43
 */
public class GameHistoryFilter implements Serializable {
    private boolean showCommands;
    private Set<DbHistoryElement.Type> types = new HashSet<>();

    public void setShowCommands(boolean showCommands) {
        this.showCommands = showCommands;
    }

    public boolean isShowCommands() {
        return showCommands;
    }

    public Collection<DbHistoryElement.Type> getTypes() {
        return types;
    }

    public boolean hasTypes() {
        return !types.isEmpty();
    }

    public boolean isType(DbHistoryElement.Type type) {
        return types.contains(type);
    }

    public void setType(DbHistoryElement.Type type, boolean enabled) {
        if (enabled) {
            types.add(type);
        } else {
            types.remove(type);
        }
    }
}
