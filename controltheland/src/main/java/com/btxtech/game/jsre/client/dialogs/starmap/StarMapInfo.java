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

    public Collection<StarMapPlanetInfo> getStarMapPlanetInfos() {
        return starMapPlanetInfos;
    }

    public void setStarMapPlanetInfos(Collection<StarMapPlanetInfo> starMapPlanetInfos) {
        this.starMapPlanetInfos = starMapPlanetInfos;
    }
}
