package com.btxtech.game.services;

import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetServices;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.connection.CommonConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.inventory.CommonInventoryService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemTypeService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import org.junit.Ignore;

/**
 * User: beat
 * Date: 11.10.2011
 * Time: 14:32:37
 */
@Ignore
public class TestPlanetServices implements PlanetServices {
    private PlanetInfo planetInfo;
    private ItemService itemService;
    private ItemTypeService itemTypeService;
    private AbstractTerrainService terrainService;
    private AbstractBaseService baseService;
    private EnergyService energyService;
    private CommonCollisionService collisionService;
    private CommonActionService actionService;
    private CommonBotService botService;
    private CommonInventoryService inventoryService;
    private CommonConnectionService connectionService;

    @Override
    public PlanetInfo getPlanetInfo() {
        return planetInfo;
    }

    public void setPlanetInfo(PlanetInfo planetInfo) {
        this.planetInfo = planetInfo;
    }

    @Override
    public ItemService getItemService() {
        return itemService;
    }

    @Override
    public AbstractTerrainService getTerrainService() {
        return terrainService;
    }

    public ItemTypeService getItemTypeService() {
        return itemTypeService;
    }

    public void setItemTypeService(ItemTypeService itemTypeService) {
        this.itemTypeService = itemTypeService;
    }

    @Override
    public AbstractBaseService getBaseService() {
        return baseService;
    }

    @Override
    public EnergyService getEnergyService() {
        return energyService;
    }

    @Override
    public CommonCollisionService getCollisionService() {
        return collisionService;
    }

    @Override
    public CommonActionService getActionService() {
        return actionService;
    }

    @Override
    public CommonBotService getBotService() {
        return botService;
    }

    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    public void setTerrainService(AbstractTerrainService terrainService) {
        this.terrainService = terrainService;
    }

    public void setBaseService(AbstractBaseService baseService) {
        this.baseService = baseService;
    }

    public void setEnergyService(EnergyService energyService) {
        this.energyService = energyService;
    }

    public void setCollisionService(CommonCollisionService collisionService) {
        this.collisionService = collisionService;
    }

    public void setActionService(CommonActionService actionService) {
        this.actionService = actionService;
    }

    public void setBotService(CommonBotService botService) {
        this.botService = botService;
    }

    @Override
    public CommonInventoryService getInventoryService() {
        return inventoryService;
    }

    public void setInventoryService(CommonInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public CommonConnectionService getConnectionService() {
        return connectionService;
    }

    public void setConnectionService(CommonConnectionService connectionService) {
        this.connectionService = connectionService;
    }
}
