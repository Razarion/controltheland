package com.btxtech.game.services.common;

/**
 * User: beat
 * Date: 21.09.2011
 * Time: 20:54:21
 */
public class ContentSort {
    private String propertyName;
    private boolean asc;

    public ContentSort(String propertyName, boolean asc) {
        this.propertyName = propertyName;
        this.asc = asc;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public boolean isAsc() {
        return asc;
    }
}
