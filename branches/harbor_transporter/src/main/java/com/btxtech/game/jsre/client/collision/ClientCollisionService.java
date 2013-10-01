package com.btxtech.game.jsre.client.collision;

import com.btxtech.game.jsre.client.ClientPlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.CommonCollisionServiceImpl;

/**
 * User: beat
 * Date: 05.10.2011
 * Time: 00:13:18
 */
public class ClientCollisionService extends CommonCollisionServiceImpl {
    private static final ClientCollisionService INSTANCE = new ClientCollisionService();

    public static ClientCollisionService getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton
     */
    private ClientCollisionService() {
    }

    @Override
    protected PlanetServices getServices() {
        return ClientPlanetServices.getInstance();
    }
}
