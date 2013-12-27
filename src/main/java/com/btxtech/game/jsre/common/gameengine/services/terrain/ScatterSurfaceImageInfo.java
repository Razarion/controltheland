package com.btxtech.game.jsre.common.gameengine.services.terrain;

import java.io.Serializable;

/**
 * User: beat
 * Date: 24.09.13
 * Time: 21:11
 */
public class ScatterSurfaceImageInfo implements Serializable {
    private double uncommon;
    private double rare;
    private int commonImageCount;
    private int uncommonImageCount;
    private int rareImageCount;

    public void setRare(double rare) {
        this.rare = rare;
    }

    public void setUncommon(double uncommon) {
        this.uncommon = uncommon;
    }

    public void setCommonImageCount(int commonImageCount) {
        this.commonImageCount = commonImageCount;
    }

    public void setRareImageCount(int rareImageCount) {
        this.rareImageCount = rareImageCount;
    }

    public void setUncommonImageCount(int uncommonImageCount) {
        this.uncommonImageCount = uncommonImageCount;
    }

    public int getImageOffset(double frequency, double imageIndex) {
        double common = 1.0 - uncommon - rare;
        if (frequency < common) {
            return (int) (imageIndex * (double) commonImageCount);
        } else if (frequency < common + uncommon) {
            return (int) (imageIndex * (double) uncommonImageCount) + commonImageCount;
        } else {
            return (int) (imageIndex * (double) rareImageCount) + uncommonImageCount + commonImageCount;
        }
    }
}
