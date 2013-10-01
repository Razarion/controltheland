package com.btxtech.game.services.common;

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
import org.junit.Ignore;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 17:20
 */
@Ignore
public class TestGlobalServices implements ServerGlobalServices {
    private HistoryService historyService;
    private StatisticsService statisticsService;
    private UserService userService;
    private UserTrackingService userTrackingService;
    private GuildService guildService;
    private GlobalInventoryService globalInventoryService;
    private PlanetSystemService planetSystemService;
    private TerrainImageService terrainImageService;
    private XpService xpService;
    private ServerConditionService serverConditionService;
    private ServerItemTypeService serverItemTypeService;
    private UserGuidanceService userGuidanceService;
    private ServerGlobalConnectionService serverGlobalConnectionService;
    private ServerI18nHelper serverI18nHelper;
    private ServerUnlockService serverUnlockService;

    @Override
    public HistoryService getHistoryService() {
        return historyService;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public StatisticsService getStatisticsService() {
        return statisticsService;
    }

    public void setStatisticsService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserTrackingService getUserTrackingService() {
        return userTrackingService;
    }

    public void setUserTrackingService(UserTrackingService userTrackingService) {
        this.userTrackingService = userTrackingService;
    }

    @Override
    public GuildService getGuildService() {
        return guildService;
    }

    public void setGuildService(GuildService guildService) {
        this.guildService = guildService;
    }

    @Override
    public GlobalInventoryService getGlobalInventoryService() {
        return globalInventoryService;
    }

    public void setGlobalInventoryService(GlobalInventoryService globalInventoryService) {
        this.globalInventoryService = globalInventoryService;
    }

    @Override
    public PlanetSystemService getPlanetSystemService() {
        return planetSystemService;
    }

    public void setPlanetSystemService(PlanetSystemService planetSystemService) {
        this.planetSystemService = planetSystemService;
    }

    @Override
    public TerrainImageService getTerrainImageService() {
        return terrainImageService;
    }

    public void setTerrainImageService(TerrainImageService terrainImageService) {
        this.terrainImageService = terrainImageService;
    }

    @Override
    public XpService getXpService() {
        return xpService;
    }

    public void setXpService(XpService xpService) {
        this.xpService = xpService;
    }

    @Override
    public ServerConditionService getConditionService() {
        return serverConditionService;
    }

    public void setServerConditionService(ServerConditionService serverConditionService) {
        this.serverConditionService = serverConditionService;
    }

    @Override
    public ServerItemTypeService getItemTypeService() {
        return serverItemTypeService;
    }

    public void setServerItemTypeService(ServerItemTypeService serverItemTypeService) {
        this.serverItemTypeService = serverItemTypeService;
    }

    @Override
    public UserGuidanceService getCommonUserGuidanceService() {
        return userGuidanceService;
    }

    public void setUserGuidanceService(UserGuidanceService userGuidanceService) {
        this.userGuidanceService = userGuidanceService;
    }

    @Override
    public ServerGlobalConnectionService getServerGlobalConnectionService() {
        return serverGlobalConnectionService;
    }

    public void setServerGlobalConnectionService(ServerGlobalConnectionService serverGlobalConnectionService) {
        this.serverGlobalConnectionService = serverGlobalConnectionService;
    }

    @Override
    public ServerI18nHelper getServerI18nHelper() {
        return serverI18nHelper;
    }

    public void setServerI18nHelper(ServerI18nHelper serverI18nHelper) {
        this.serverI18nHelper = serverI18nHelper;
    }

    @Override
    public ServerUnlockService getUnlockService() {
        return serverUnlockService;
    }

    public void setServerUnlockService(ServerUnlockService serverUnlockService) {
        this.serverUnlockService = serverUnlockService;
    }
}
