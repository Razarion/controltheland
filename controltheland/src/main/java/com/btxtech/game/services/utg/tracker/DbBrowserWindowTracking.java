package com.btxtech.game.services.utg.tracker;

import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * User: beat
 * Date: 15.04.2011
 * Time: 12:55:50
 */
@Entity(name = "TRACKER_BROWSER_WINDOW")
public class DbBrowserWindowTracking {
    @Id
    @GeneratedValue
    private Integer id;
    private int clientWidth;
    private int clientHeight;
    private int scrollLeft;
    private int scrollTop;
    private int scrollWidth;
    private int scrollHeight;
    private long clientTimeStamp;
    private Date timeStamp;
    private String startUuid;

    /**
     * Used by hibernate
     */
    protected DbBrowserWindowTracking() {
    }

    public DbBrowserWindowTracking(BrowserWindowTracking browserWindowTracking) {
        startUuid = browserWindowTracking.getStartUuid();
        clientWidth = browserWindowTracking.getClientWidth();
        clientHeight = browserWindowTracking.getClientHeight();
        scrollLeft = browserWindowTracking.getScrollLeft();
        scrollTop = browserWindowTracking.getScrollTop();
        scrollWidth = browserWindowTracking.getScrollWidth();
        scrollHeight = browserWindowTracking.getScrollHeight();
        clientTimeStamp = browserWindowTracking.getClientTimeStamp();
        timeStamp = new Date();
    }

    public BrowserWindowTracking createBrowserWindowTracking() {
        return new BrowserWindowTracking(startUuid, clientWidth, clientHeight, scrollLeft, scrollTop, scrollWidth, scrollHeight, clientTimeStamp);
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DbBrowserWindowTracking)) return false;

        DbBrowserWindowTracking that = (DbBrowserWindowTracking) o;

        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
