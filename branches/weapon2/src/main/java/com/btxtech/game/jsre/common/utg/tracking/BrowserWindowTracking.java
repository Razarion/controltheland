package com.btxtech.game.jsre.common.utg.tracking;

import java.io.Serializable;

/**
 * User: beat
 * Date: 14.04.2011
 * Time: 11:45:27
 */
public class BrowserWindowTracking implements Serializable {
    private int clientWidth;
    private int clientHeight;
    private int scrollLeft;
    private int scrollTop;
    private int scrollWidth;
    private int scrollHeight;
    private long clientTimeStamp;
    private String startUuid;

    /**
     * Used by GWT
     */
    protected BrowserWindowTracking() {
    }

    public BrowserWindowTracking(String startUuid, int clientWidth, int clientHeight, int scrollLeft, int scrollTop, int scrollWidth, int scrollHeight) {
        this.startUuid = startUuid;
        clientTimeStamp = System.currentTimeMillis();
        this.clientWidth = clientWidth;
        this.clientHeight = clientHeight;
        this.scrollLeft = scrollLeft;
        this.scrollTop = scrollTop;
        this.scrollWidth = scrollWidth;
        this.scrollHeight = scrollHeight;
    }

    public BrowserWindowTracking(String startUuid, int clientWidth, int clientHeight, int scrollLeft, int scrollTop, int scrollWidth, int scrollHeight, long clientTimeStamp) {
        this.startUuid = startUuid;
        this.clientTimeStamp = clientTimeStamp;
        this.clientWidth = clientWidth;
        this.clientHeight = clientHeight;
        this.scrollLeft = scrollLeft;
        this.scrollTop = scrollTop;
        this.scrollWidth = scrollWidth;
        this.scrollHeight = scrollHeight;
    }

    public int getClientWidth() {
        return clientWidth;
    }

    public int getClientHeight() {
        return clientHeight;
    }

    public int getScrollLeft() {
        return scrollLeft;
    }

    public int getScrollTop() {
        return scrollTop;
    }

    public int getScrollWidth() {
        return scrollWidth;
    }

    public int getScrollHeight() {
        return scrollHeight;
    }

    public long getClientTimeStamp() {
        return clientTimeStamp;
    }

    public String getStartUuid() {
        return startUuid;
    }

    @Override
    public String toString() {
        return "BrowserWindowTracking: clientWidth: " + clientWidth
                + " clientHeight: " + clientHeight
                + " scrollLeft: " + scrollLeft
                + " scrollTop: " + scrollTop
                + " scrollWidth: " + scrollWidth
                + " scrollHeight: " + scrollHeight
                + " clientTimeStamp: " + clientTimeStamp;
    }
}
