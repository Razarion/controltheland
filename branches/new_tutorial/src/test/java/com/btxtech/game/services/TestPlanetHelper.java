package com.btxtech.game.services;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.planet.db.DbPlanet;
import org.junit.Ignore;

import java.util.Collection;

/**
 * User: beat
 * Date: 03.09.12
 * Time: 18:04
 */
@Ignore
public class TestPlanetHelper implements Planet {
    private ServerPlanetServices serverPlanetServices;

    public TestPlanetHelper() {
    }

    @Override
    public ServerPlanetServices getPlanetServices() {
        return serverPlanetServices;
    }

    public void setServerPlanetServices(ServerPlanetServices serverPlanetServices) {
        this.serverPlanetServices = serverPlanetServices;
    }

    @Override
    public void activate(DbPlanet dbPlanet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deactivate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeRestore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void restore(Collection<Base> bases, Collection<SyncBaseObject> syncBaseObjects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterRestore(DbPlanet dbPlanet) {
        throw new UnsupportedOperationException();
    }
}
