package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.db.DbPlanet;

import java.util.Collection;

/**
 * User: beat
 * Date: 26.08.12
 * Time: 18:33
 */
public interface Planet {
    ServerPlanetServices getPlanetServices();

    void activate(DbPlanet dbPlanet);

    void deactivate();

    void beforeRestore();

    void restore(Collection<Base> bases, Collection<SyncBaseObject> syncBaseObjects);

    void afterRestore(DbPlanet dbPlanet);
}
