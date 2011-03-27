package com.btxtech.game.services.utg;

/**
 * User: beat
 * Date: 16.03.2011
 * Time: 11:32:00
 */
public class LevelActivationException extends Exception {
    private StringBuilder hierarchy = new StringBuilder();
    private String text;

    public LevelActivationException(String text) {
        super(text);
        this.text = text;
    }

    public void addParent(String parent) {
        if (hierarchy.length() > 0) {
            hierarchy.append(": ");
        }
        hierarchy.append(parent);
    }

    @Override
    public String getMessage() {
        return "Error activating level: " + hierarchy + " (" + text + ")";
    }
}
