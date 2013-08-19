package com.btxtech.game.jsre.client.dialogs.starmap;

import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;

import java.io.Serializable;
import java.util.Collection;

/**
 * User: beat
 * Date: 12.08.13
 * Time: 16:09
 */
public class StarMapInfo implements Serializable {
    private Collection<StarMapPlanetInfo> starMapPlanetInfos;
    private int width;
    private int height;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Collection<StarMapPlanetInfo> getStarMapPlanetInfos() {
        return starMapPlanetInfos;
    }

    public void setStarMapPlanetInfos(Collection<StarMapPlanetInfo> starMapPlanetInfos) {
        this.starMapPlanetInfos = starMapPlanetInfos;
    }
}
