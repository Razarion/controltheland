package com.btxtech.game.services.utg.tracker;

import com.btxtech.game.jsre.common.utg.tracking.DialogTracking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * User: beat
 * Date: 15.11.2011
 * Time: 21:18:21
 */
@Entity(name = "TRACKER_DIALOG")
public class DbDialogTracking implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date timeStamp;
    private String startUuid;
    private long clientTimeStamp;
    @Column(name = "leftColumn")
    private Integer left;
    private Integer top;
    private Integer width;
    private Integer height;
    private Integer zIndex;
    private String description;
    private boolean appearing;
    private int identityHashCode;

    /**
     * Used by hibernate
     */
    protected DbDialogTracking() {
    }

    public DbDialogTracking(DialogTracking dialogTracking) {
        timeStamp = new Date();
        startUuid = dialogTracking.getStartUuid();
        clientTimeStamp = dialogTracking.getClientTimeStamp();
        left = dialogTracking.getLeft();
        top = dialogTracking.getTop();
        width = dialogTracking.getWidth();
        height = dialogTracking.getHeight();
        zIndex = dialogTracking.getZIndex();
        description = dialogTracking.getDescription();
        appearing = dialogTracking.isAppearing();
        identityHashCode = dialogTracking.getIdentityHashCode();
    }

    public DialogTracking createDialogTracking() {
        return new DialogTracking(startUuid, left, top, width, height, zIndex, description, appearing, identityHashCode, clientTimeStamp);
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbDialogTracking that = (DbDialogTracking) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id : System.identityHashCode(this);
    }
}
