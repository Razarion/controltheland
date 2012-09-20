package com.btxtech.game.jsre.client.common.info;

/**
 * User: beat
 * Date: 14.02.2012
 * Time: 11:23:28
 */
public class InvalidLevelStateException extends Exception{
    private Integer levelTaskId;

    /**
     * Used by GWT
     */
    InvalidLevelStateException() {
    }

    public InvalidLevelStateException(Integer levelTaskId) {
        this.levelTaskId = levelTaskId;
    }

    public Integer getLevelTaskId() {
        return levelTaskId;
    }
}
