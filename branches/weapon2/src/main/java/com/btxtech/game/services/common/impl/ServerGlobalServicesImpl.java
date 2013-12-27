package com.btxtech.game.services.common.impl;

import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 17:03
 */
@Component("globalServices")
public class ServerGlobalServicesImpl implements ServerGlobalServices {
    @Autowired
    private HistoryService historyService;
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTrackingService userTrackingService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private GlobalInventoryService globalInventoryService;
    @Autowired
    private PlanetSystemService planetSystemService;
    @Autowired
    private TerrainImageService terrainImageService;
    @Autowired
    private XpService xpService;
    @Autowired
    private ServerConditionService serverConditionService;
    @Autowired
    private ServerItemTypeService serverItemTypeService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerGlobalConnectionService serverGlobalConnectionService;
    @Autowired
    private ServerI18nHelper serverI18nHelper;
    @Autowired
    private ServerUnlockService serverUnlockService;

    @Override
    public HistoryService getHistoryService() {
        return historyService;
    }

    @Override
    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    @Override
    public UserTrackingService getUserTrackingService() {
        return userTrackingService;
    }

    @Override
    public GuildService getGuildService() {
        return guildService;
    }

    @Override
    public GlobalInventoryService getGlobalInventoryService() {
        return globalInventoryService;
    }

    @Override
    public PlanetSystemService getPlanetSystemService() {
        return planetSystemService;
    }

    @Override
    public TerrainImageService getTerrainImageService() {
        return terrainImageService;
    }

    @Override
    public XpService getXpService() {
        return xpService;
    }

    @Override
    public ServerConditionService getConditionService() {
        return serverConditionService;
    }

    @Override
    public ServerItemTypeService getItemTypeService() {
        return serverItemTypeService;
    }

    @Override
    public UserGuidanceService getCommonUserGuidanceService() {
        return userGuidanceService;
    }

    @Override
    public ServerGlobalConnectionService getServerGlobalConnectionService() {
        return serverGlobalConnectionService;
    }

    @Override
    public ServerI18nHelper getServerI18nHelper() {
        return serverI18nHelper;
    }

    @Override
    public ServerUnlockService getUnlockService() {
        return serverUnlockService;
    }
}
