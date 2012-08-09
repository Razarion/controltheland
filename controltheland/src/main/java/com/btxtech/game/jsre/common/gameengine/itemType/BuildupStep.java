package com.btxtech.game.jsre.common.gameengine.itemType;

import java.io.Serializable;

/**
 * User: beat
 * Date: 08.01.2012
 * Time: 23:35:16
 */
@Deprecated
public class BuildupStep implements Serializable {
    private double from;
    private double toExclusive;
    //private String base64ImageData;
    private Integer imageId;
    private int animationSteps;
    /**
     * Used by GWT
     */
    BuildupStep() {
    }

    public BuildupStep(Integer imageId, double from, double toExclusive) {
        this.imageId = imageId;
        this.toExclusive = toExclusive;
        this.from = from;
    }

    public Integer getImageId() {
        return imageId;
    }

/*    public BuildupStep(String base64ImageData) {
        this.base64ImageData = base64ImageData;
    } */

    public double getDelta() {
        return toExclusive - from;
    }

    public double getFrom() {
        return from;
    }

    public void setFrom(double from) {
        this.from = from;
    }

    public double getToExclusive() {
        return toExclusive;
    }

    public void setToExclusive(double toExclusive) {
        this.toExclusive = toExclusive;
    }

/*    public String getBase64ImageData() {
        return base64ImageData;
    }  */

    public boolean isInRange(double value) {
        return value >= from && value < toExclusive;
    }
}
