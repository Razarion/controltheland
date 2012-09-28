package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.services.bot.impl.BotServiceImpl;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.impl.ServerConnectionServiceImpl;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.planet.db.DbPlanet;

import java.util.Collection;

/**
 * User: beat
 * Date: 26.08.12
 * Time: 18:36
 */
public class PlanetImpl implements Planet {
    private ServerPlanetServicesImpl planetServices;
    private boolean active;

    public PlanetImpl() {
        planetServices = new ServerPlanetServicesImpl();
        planetServices.setBaseService(new BaseServiceImpl(this));
        planetServices.setTerrainService(new ServerTerrainServiceImpl());
        planetServices.setCollisionService(new CollisionServiceImpl());
        planetServices.setInventoryService(new InventoryServiceImpl());
        planetServices.setServerItemService(new ServerItemServiceImpl());
        planetServices.setActionService(new ActionServiceImpl());
        planetServices.setServerEnergyService(new ServerEnergyServiceImpl());
        planetServices.setBotService(new BotServiceImpl());
        planetServices.setResourceService(new ResourceServiceImpl());
        planetServices.setServerConnectionService(new ServerConnectionServiceImpl());
    }

    public void init(ServerGlobalServices serverGlobalServices) {
        ((ServerTerrainServiceImpl) planetServices.getTerrainService()).init(serverGlobalServices);
        ((BaseServiceImpl) planetServices.getBaseService()).init(planetServices, serverGlobalServices);
        ((CollisionServiceImpl) planetServices.getCollisionService()).init(planetServices);
        ((InventoryServiceImpl) planetServices.getInventoryService()).init(planetServices, serverGlobalServices);
        ((ServerItemServiceImpl) planetServices.getItemService()).init(planetServices, serverGlobalServices);
        ((ActionServiceImpl) planetServices.getActionService()).init(planetServices, serverGlobalServices);
        ((ServerEnergyServiceImpl) planetServices.getEnergyService()).init(planetServices);
        ((BotServiceImpl) planetServices.getBotService()).init(planetServices, serverGlobalServices);
        ((ResourceServiceImpl) planetServices.getResourceService()).init(planetServices);
        ((ServerConnectionServiceImpl) planetServices.getConnectionService()).init(planetServices, serverGlobalServices);
    }

    @Override
    public void activate(DbPlanet dbPlanet) {
        if (active) {
            throw new IllegalStateException("Planet is active: " + planetServices.getPlanetInfo().getName());
        }
        planetServices.setPlanetInfo(dbPlanet.createPlanetInfo());
        ((ServerTerrainServiceImpl) planetServices.getTerrainService()).activate(dbPlanet);
        planetServices.getInventoryService().activate(dbPlanet);
        (planetServices.getBotService()).activate(dbPlanet);
        planetServices.getResourceService().activate(dbPlanet);
        planetServices.getActionService().activate();
        planetServices.getConnectionService().activate();
        planetServices.setStartRegion(dbPlanet.getStartRegion().createRegion());
        active = true;
    }

    @Override
    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("Planet is not active: " + planetServices.getPlanetInfo().getName());
        }
        active = false;
        planetServices.getInventoryService().deactivate();
        planetServices.getBotService().deactivate();
        planetServices.getActionService().deactivate();
        planetServices.getResourceService().deactivate();
        planetServices.getConnectionService().deactivate();
    }

    @Override
    public ServerPlanetServices getPlanetServices() {
        return planetServices;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void beforeRestore() {
        active = false;
        planetServices.getActionService().pause(true);
        planetServices.getEnergyService().pause(true);
        planetServices.getBotService().deactivate();
        planetServices.getInventoryService().deactivate();
        planetServices.getResourceService().deactivate();
    }

    @Override
    public void afterRestore(DbPlanet dbPlanet) {
        planetServices.getActionService().pause(false);
        planetServices.getEnergyService().pause(false);
        planetServices.getBotService().activate(dbPlanet);
        planetServices.getInventoryService().activate(dbPlanet);
        planetServices.getActionService().reload();
        planetServices.getResourceService().activate(dbPlanet);
        active = true;
    }

    @Override
    public void restore(Collection<Base> bases, Collection<SyncBaseObject> syncBaseObjects) {
        planetServices.getBaseService().restore(bases);
        planetServices.getItemService().restore(syncBaseObjects);
        planetServices.getEnergyService().restore(syncBaseObjects);
    }
}
