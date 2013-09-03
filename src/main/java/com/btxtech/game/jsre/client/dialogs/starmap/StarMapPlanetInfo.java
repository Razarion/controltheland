package com.btxtech.game.jsre.client.dialogs.starmap;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;

import java.io.Serializable;

/**
 * User: beat
 * Date: 14.08.13
 * Time: 22:03
 */
public class StarMapPlanetInfo implements Serializable {
    private PlanetLiteInfo planetLiteInfo;
    private int minLevel;
    private int size;
    private Index position;
    private int bases;
    private int bots;

    public PlanetLiteInfo getPlanetLiteInfo() {
        return planetLiteInfo;
    }

    public Index getPosition() {
        return position;
    }

    public void setPlanetLiteInfo(PlanetLiteInfo planetLiteInfo) {
        this.planetLiteInfo = planetLiteInfo;
    }

    public void setPosition(Index position) {
        this.position = position;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setBases(int bases) {
        this.bases = bases;
    }

    public int getBases() {
        return bases;
    }

    public int getBots() {
        return bots;
    }

    public void setBots(int bots) {
        this.bots = bots;
    }
}
