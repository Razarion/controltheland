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

package com.btxtech.game.services.common;

import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccess;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.base.BaseService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainService;
import com.btxtech.game.services.collision.CollisionService;
import com.btxtech.game.services.itemTypeAccess.ServerItemTypeAccessService;
import com.btxtech.game.services.energy.ServerEnergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 01.12.2009
 * Time: 23:05:54
 */
@Component(value = "serverServices")
public class ServerServices implements Services {
    @Autowired
    private com.btxtech.game.services.item.ItemService itemService;
    @Autowired
    private CollisionService collisionService;
    @Autowired
    private com.btxtech.game.services.base.BaseService baseService;
    @Autowired
    private com.btxtech.game.services.connection.ConnectionService connectionService;
    @Autowired
    private ServerItemTypeAccessService serverItemTypeAccessService;
    @Autowired
    private ServerEnergyService serverEnergyService;

    @Override
    public ItemService getItemService() {
        return itemService;
    }

    @Override
    public TerrainService getTerrainService() {
        return collisionService;
    }

    @Override
    public BaseService getBaseService() {
        return baseService;
    }

    @Override
    public ConnectionService getConnectionService() {
        return connectionService;
    }

    @Override
    public ItemTypeAccess getItemTypeAccess() {
        return serverItemTypeAccessService;
    }

    @Override
    public EnergyService getEnergyService() {
        return serverEnergyService;
    }
}
