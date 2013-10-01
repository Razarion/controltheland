package com.btxtech.game.services.planet;

import com.btxtech.game.services.planet.db.DbPlanet;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 17:11
 */
public class NoSuchPlanetException extends RuntimeException {
    public NoSuchPlanetException(DbPlanet dbPlanet) {
        super("No such planet: " + dbPlanet.getName() + " id:" + dbPlanet.getId());
    }

    public NoSuchPlanetException(int planetId) {
        super("No such planet with id: " + planetId);
    }

    public NoSuchPlanetException(String s) {
        super(s);
    }
}
