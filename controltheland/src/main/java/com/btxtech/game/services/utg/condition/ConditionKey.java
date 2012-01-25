package com.btxtech.game.services.utg.condition;

import com.btxtech.game.services.user.UserState;

/**
 * User: beat
 * Date: 23.01.2012
 * Time: 12:50:07
 */
@Deprecated
public class ConditionKey {
    private UserState userState;
    private Integer levelTaskId;

    public ConditionKey(UserState userState) {
        this.userState = userState;
    }

    public ConditionKey(UserState userState, int levelTaskId) {
        this.userState = userState;
        this.levelTaskId = levelTaskId;
    }

    public UserState getUserState() {
        return userState;
    }

    public boolean isLevelTaskId() {
        return levelTaskId != null;
    }

    public int getLevelTaskId() {
        return levelTaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConditionKey)) return false;

        ConditionKey that = (ConditionKey) o;

        if (levelTaskId == null && that.levelTaskId == null) {
            return userState.equals(that.userState);
        } else
            return levelTaskId != null && that.levelTaskId != null && levelTaskId.equals(that.levelTaskId) && userState.equals(that.userState);
    }

    @Override
    public int hashCode() {
        int result = userState.hashCode();
        result = 31 * result + (levelTaskId != null ? levelTaskId.hashCode() : 0);
        return result;
    }
}
