package com.btxtech.game.services.utg.tracker;

import com.btxtech.game.jsre.common.utg.tracking.BrowserWindowTracking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
    @Column(nullable = false)
    private String sessionId;
    private int clientWidth;
    private int clientHeight;
    private int scrollLeft;
    private int scrollTop;
    private int scrollWidth;
    private int scrollHeight;
    private long clientTimeStamp;

    /**
     * Used by hibernate
     */
    protected DbBrowserWindowTracking() {
    }

    public DbBrowserWindowTracking(BrowserWindowTracking browserWindowTracking, String sessionId) {
        this.sessionId = sessionId;
        clientWidth = browserWindowTracking.getClientWidth();
        clientHeight = browserWindowTracking.getClientHeight();
        scrollLeft = browserWindowTracking.getScrollLeft();
        scrollTop = browserWindowTracking.getScrollTop();
        scrollWidth = browserWindowTracking.getScrollWidth();
        scrollHeight = browserWindowTracking.getScrollHeight();
        clientTimeStamp = browserWindowTracking.getClientTimeStamp();
    }

    public BrowserWindowTracking createBrowserWindowTracking() {
       return new BrowserWindowTracking(clientWidth,clientHeight,scrollLeft,scrollTop,scrollWidth,scrollHeight,clientTimeStamp);
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
