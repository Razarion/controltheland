/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.common.Region;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.services.bot.BotService;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.ServerConnectionService;
import com.btxtech.game.services.planet.ActionService;
import com.btxtech.game.services.planet.BaseService;
import com.btxtech.game.services.planet.CollisionService;
import com.btxtech.game.services.planet.InventoryService;
import com.btxtech.game.services.planet.ResourceService;
import com.btxtech.game.services.planet.ServerEnergyService;
import com.btxtech.game.services.planet.ServerItemService;
import com.btxtech.game.services.planet.ServerTerrainService;

/**
 * User: beat
 * Date: 01.12.2009
 * Time: 23:05:54
 */
public class ServerPlanetServicesImpl implements ServerPlanetServices {
    private PlanetInfo planetInfo;
    private CollisionService collisionService;
    private ServerTerrainService terrainService;
    private BaseService baseService;
    private InventoryService inventoryService;
    private ServerItemService serverItemService;
    private ActionService actionService;
    private ServerEnergyService serverEnergyService;
    private BotService botService;
    private ResourceService resourceService;
    private ServerConnectionService serverConnectionService;
    private Region startRegion;

    @Override
    public PlanetInfo getPlanetInfo() {
        return planetInfo;
    }

    public void setPlanetInfo(PlanetInfo planetInfo) {
        this.planetInfo = planetInfo;
    }

    @Override
    public ServerItemService getItemService() {
        return serverItemService;
    }

    public void setServerItemService(ServerItemService serverItemService) {
        this.serverItemService = serverItemService;
    }

    public void setTerrainService(ServerTerrainService terrainService) {
        this.terrainService = terrainService;
    }

    @Override
    public ServerTerrainService getTerrainService() {
        return terrainService;
    }

    @Override
    public BaseService getBaseService() {
        return baseService;
    }

    public void setBaseService(BaseService baseService) {
        this.baseService = baseService;
    }

    @Override
    public ServerEnergyService getEnergyService() {
        return serverEnergyService;
    }

    public void setServerEnergyService(ServerEnergyService serverEnergyService) {
        this.serverEnergyService = serverEnergyService;
    }

    @Override
    public CollisionService getCollisionService() {
        return collisionService;
    }

    public void setCollisionService(CollisionService collisionService) {
        this.collisionService = collisionService;
    }

    @Override
    public ActionService getActionService() {
        return actionService;
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public BotService getBotService() {
        return botService;
    }

    public void setBotService(BotService botService) {
        this.botService = botService;
    }

    @Override
    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public ServerConnectionService getConnectionService() {
        return serverConnectionService;
    }

    public void setServerConnectionService(ServerConnectionService serverConnectionService) {
        this.serverConnectionService = serverConnectionService;
    }

    @Override
    public Region getStartRegion() {
        return startRegion;
    }

    public void setStartRegion(Region startRegion) {
        this.startRegion = startRegion;
    }
}
