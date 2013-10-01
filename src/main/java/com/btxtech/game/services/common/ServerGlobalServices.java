package com.btxtech.game.services.common;

import com.btxtech.game.jsre.common.gameengine.services.GlobalServices;
import com.btxtech.game.services.connection.ServerGlobalConnectionService;
import com.btxtech.game.services.history.HistoryService;
import com.btxtech.game.services.inventory.GlobalInventoryService;
import com.btxtech.game.services.item.ServerItemTypeService;
import com.btxtech.game.services.mgmt.ServerI18nHelper;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.statistics.StatisticsService;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.utg.UserGuidanceService;
import com.btxtech.game.services.utg.UserTrackingService;
import com.btxtech.game.services.utg.XpService;
import com.btxtech.game.services.utg.condition.ServerConditionService;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 00:12
 */
public interface ServerGlobalServices extends GlobalServices {
    HistoryService getHistoryService();

    StatisticsService getStatisticsService();

    UserService getUserService();

    UserTrackingService getUserTrackingService();

    GuildService getGuildService();

    GlobalInventoryService getGlobalInventoryService();

    PlanetSystemService getPlanetSystemService();

    TerrainImageService getTerrainImageService();

    XpService getXpService();

    ServerGlobalConnectionService getServerGlobalConnectionService();

    ServerI18nHelper getServerI18nHelper();

    @Override
    ServerConditionService getConditionService();

    @Override
    ServerItemTypeService getItemTypeService();

    @Override
    UserGuidanceService getCommonUserGuidanceService();
}
