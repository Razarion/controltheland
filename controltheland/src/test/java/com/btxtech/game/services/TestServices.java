package com.btxtech.game.services;

import com.btxtech.game.jsre.common.gameengine.services.Services;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.territory.AbstractTerritoryService;
import com.btxtech.game.jsre.common.gameengine.services.utg.CommonUserGuidanceService;
import com.btxtech.game.jsre.common.utg.ConditionService;
import org.junit.Ignore;

/**
 * User: beat
 * Date: 11.10.2011
 * Time: 14:32:37
 */
@Ignore
public class TestServices implements Services {
    private ItemService itemService;
    private AbstractTerrainService terrainService;
    private AbstractBaseService baseService;
    private ConnectionService connectionService;
    private EnergyService energyService;
    private CommonCollisionService collisionService;
    private CommonActionService actionService;
    private AbstractTerritoryService territoryService;
    private CommonBotService botService;
    private CommonUserGuidanceService commonUserGuidanceService;
    private ConditionService conditionService;

    @Override
    public ItemService getItemService() {
        return itemService;
    }

    @Override
    public AbstractTerrainService getTerrainService() {
        return terrainService;
    }

    @Override
    public AbstractBaseService getBaseService() {
        return baseService;
    }

    @Override
    public ConnectionService getConnectionService() {
        return connectionService;
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
    public AbstractTerritoryService getTerritoryService() {
        return territoryService;
    }

    @Override
    public CommonBotService getBotService() {
        return botService;
    }

    @Override
    public CommonUserGuidanceService getCommonUserGuidanceService() {
        return commonUserGuidanceService;
    }

    @Override
    public ConditionService getConditionService() {
        return conditionService;
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

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
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

    public void setTerritoryService(AbstractTerritoryService territoryService) {
        this.territoryService = territoryService;
    }

    public void setBotService(CommonBotService botService) {
        this.botService = botService;
    }

    public void setCommonUserGuidanceService(CommonUserGuidanceService commonUserGuidanceService) {
        this.commonUserGuidanceService = commonUserGuidanceService;
    }

    public void setConditionService(ConditionService conditionService) {
        this.conditionService = conditionService;
    }
}
