package com.btxtech.game.services.planet.impl;

import com.btxtech.game.jsre.client.NotAGuildMemberException;
import com.btxtech.game.jsre.client.PositionInBotException;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.LevelScope;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.StartPointInfo;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapInfo;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapPlanetInfo;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.SimpleEntry;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.jsre.common.packets.StorablePacket;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ExceptionHandler;
import com.btxtech.game.services.common.HibernateUtil;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.common.ServerGlobalServices;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.OnlineUserDTO;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.mgmt.MgmtService;
import com.btxtech.game.services.planet.Base;
import com.btxtech.game.services.planet.NoSuchPlanetException;
import com.btxtech.game.services.planet.Planet;
import com.btxtech.game.services.planet.PlanetSystemService;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.terrain.DbTerrainSetting;
import com.btxtech.game.services.terrain.TerrainDbUtil;
import com.btxtech.game.services.terrain.TerrainImageService;
import com.btxtech.game.services.unlock.ServerUnlockService;
import com.btxtech.game.services.user.GuildService;
import com.btxtech.game.services.user.SecurityRoles;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.services.user.UserState;
import com.btxtech.game.services.utg.DbLevel;
import com.btxtech.game.services.utg.UserGuidanceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 02:30
 */
@Component("planetService")
public class PlanetSystemServiceImpl implements PlanetSystemService {
    private Log log = LogFactory.getLog(PlanetSystemServiceImpl.class);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private UserGuidanceService userGuidanceService;
    @Autowired
    private ServerGlobalServices serverGlobalServices;
    @Autowired
    private CrudRootServiceHelper<DbPlanet> dbPlanetCrud;
    @Autowired
    private TerrainImageService terrainImageService;
    @Autowired
    private ServerUnlockService serverUnlockService;
    @Autowired
    private MgmtService mgmtService;
    @Autowired
    private GuildService guildService;
    private final Map<Integer, PlanetImpl> planetImpls = new HashMap<>();

    @PostConstruct
    public void init() {
        dbPlanetCrud.init(DbPlanet.class);
    }

    @PreDestroy
    public void destroy() {
        while (!planetImpls.isEmpty()) {
            Planet planet = CommonJava.getFirst(planetImpls.values());
            destroyPlanet(planet);
        }
    }

    @Override
    public void createBase(ServerPlanetServices serverPlanetServices, UserState userState, Index position) throws InvalidLevelStateException, PositionInBotException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
        if (!hasMinimalLevel(serverPlanetServices.getPlanetInfo().getPlanetId())) {
            throw new IllegalStateException("User does not have minimal Level for planet: " + serverPlanetServices.getPlanetInfo().getPlanetLiteInfo() + " user: " + userState);
        }
        DbPlanet dbPlanet = getDbPlanetCrud().readDbChild(serverPlanetServices.getPlanetInfo().getPlanetId());
        serverPlanetServices.getBaseService().createNewBase(userState, dbPlanet.getStartItemType(), dbPlanet.getStartMoney(), position, dbPlanet.getStartItemFreeRange());
        log.debug("Base for user '" + userState + "' created on planet: " + dbPlanet);
    }

    private DbPlanet getUnlockedPlanet(DbLevel dbLevel, UserState userState) throws InvalidLevelStateException {
        try {
            DbPlanet dbPlanet = dbLevel.getDbPlanet();
            if (dbPlanet == null) {
                return getUnlockedPlanet(userGuidanceService.getPreviousDbLevel(dbLevel), userState);
            }
            Planet planet = getPlanet(dbPlanet);

            if (serverUnlockService.isPlanetLocked(planet.getPlanetServices().getPlanetInfo().getPlanetLiteInfo(), userState)) {
                return getUnlockedPlanet(userGuidanceService.getPreviousDbLevel(dbLevel), userState);
            } else {
                return dbPlanet;
            }
        } catch (Exception e) {
            mgmtService.saveServerDebug(MgmtService.SERVER_DEBUG_GET_UNLOCKED_PLANET, e);
            throw userGuidanceService.createInvalidLevelState();
        }
    }

    @Override
    public Planet getPlanet(DbPlanet dbPlanet) throws NoSuchPlanetException {
        Planet planet = planetImpls.get(dbPlanet.getId());
        if (planet == null) {
            throw new NoSuchPlanetException(dbPlanet);
        }
        return planet;
    }

    @Override
    public Planet getPlanet(int planetId) throws NoSuchPlanetException {
        Planet planet = planetImpls.get(planetId);
        if (planet == null) {
            throw new NoSuchPlanetException(planetId);
        }
        return planet;
    }

    @Override
    public Planet getPlanet(SimpleBase simpleBase) throws NoSuchPlanetException {
        return getPlanet(simpleBase.getPlanetId());
    }

    @Override
    public Planet getPlanet(UserState userState) throws NoSuchPlanetException {
        Planet planet = getPlanetNoThrow(userState);
        if (planet != null) {
            return planet;
        } else {
            LevelScope levelScope = userGuidanceService.getLevelScope(userState);
            throw new NoSuchPlanetException("UserStat '" + userState + "' does not have a level with a planet configured: " + levelScope);
        }
    }

    private Planet getPlanetNoThrow(UserState userState) {
        Base base = userState.getBase();
        if (base != null) {
            return base.getPlanet();
        }
        LevelScope levelScope = userGuidanceService.getLevelScope(userState);
        if (!levelScope.hasPlanet()) {
            return null;
        }
        return getPlanet(levelScope.getPlanetLiteInfo().getPlanetId());
    }

    @Override
    public boolean hasPlanet(UserState userState) {
        return getPlanetNoThrow(userState) != null;
    }

    @Override
    public boolean hasPlanet() {
        return hasPlanet(userService.getUserState());
    }

    @Override
    public boolean isUserOnCorrectPlanet(UserState userState) {
        Base base = userState.getBase();
        if (base == null) {
            return false;
        }
        LevelScope levelScope = userGuidanceService.getLevelScope(userState);
        return levelScope.hasPlanet() && levelScope.getPlanetLiteInfo().getPlanetId() == base.getPlanet().getPlanetServices().getPlanetInfo().getPlanetId();
    }

    @Override
    public ServerPlanetServices getServerPlanetServices(int planetId) throws NoSuchPlanetException {
        return getPlanet(planetId).getPlanetServices();
    }

    @Override
    public ServerPlanetServices getServerPlanetServices(SimpleBase simpleBase) throws NoSuchPlanetException {
        return getPlanet(simpleBase.getPlanetId()).getPlanetServices();
    }

    @Override
    public ServerPlanetServices getServerPlanetServices() throws NoSuchPlanetException {
        UserState userState = userService.getUserState();
        return getServerPlanetServices(userState);
    }

    @Override
    public ServerPlanetServices getServerPlanetServices(UserState userState) {
        return getPlanet(userState).getPlanetServices();
    }

    @Override
    public ServerPlanetServices getServerPlanetServices(User user) {
        UserState userState = userService.getUserState(user);
        if (userState == null) {
            return null;
        }
        if (userState.getBase() == null) {
            return null;
        }
        return getServerPlanetServices(userState);
    }

    @Override
    public ServerPlanetServices getPlanetSystemService(UserState userState, Integer planetId) throws InvalidLevelStateException {
        if (planetId == null) {
            return getUnlockedServerPlanetServices(userState);
        }
        try {
            ServerPlanetServices serverPlanetServices = getServerPlanetServices(planetId);
            if (serverUnlockService.isPlanetLocked(serverPlanetServices.getPlanetInfo().getPlanetLiteInfo(), userState)) {
                throw new IllegalStateException("Planet is locked: " + serverPlanetServices.getPlanetInfo().getPlanetLiteInfo() + " user: " + userState);
            }
            if (!hasMinimalLevel(planetId)) {
                throw new IllegalStateException("User does not have minimal Level for planet: " + serverPlanetServices.getPlanetInfo().getPlanetLiteInfo() + " user: " + userState);
            }
            return serverPlanetServices;
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            return getUnlockedServerPlanetServices(userState);
        }
    }

    private ServerPlanetServices getUnlockedServerPlanetServices(UserState userState) throws InvalidLevelStateException {
        if (userState.getBase() != null) {
            return getServerPlanetServices(userState.getBase().getSimpleBase());
        }
        DbLevel dbLevel = userGuidanceService.getDbLevel(userState);
        DbPlanet dbPlanet = getUnlockedPlanet(dbLevel, userState);
        Planet planet = getPlanet(dbPlanet);
        return planet.getPlanetServices();
    }

    @Override
    public void useInventoryItem(UserState userState, SimpleBase simpleBase, DbInventoryItem dbInventoryItem, Collection<Index> positionToBePlaced) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException {
        Planet planet = getPlanet(simpleBase);
        planet.getPlanetServices().getInventoryService().useInventoryItem(userState, simpleBase, dbInventoryItem, positionToBePlaced);
    }

    @Override
    public DbPlanet openDbSession(PlanetInfo planetInfo) {
        HibernateUtil.openSession4InternalCall(sessionFactory);
        return dbPlanetCrud.readDbChild(planetInfo.getPlanetId());
    }

    @Override
    public void closeDbSession() {
        HibernateUtil.closeSession4InternalCall(sessionFactory);
    }

    @Override
    public void onUserStateRemoved(UserState userState) {
        if (userState.getBase() != null) {
            getPlanet(userState).getPlanetServices().getBaseService().onUserStateRemoved(userState);
        }
    }

    @Override
    public void onUserRegistered() {
        if (userService.getUserState().getBase() != null) {
            getPlanet(userService.getUserState()).getPlanetServices().getBaseService().onUserRegistered();
        }
    }

    @Override
    public CrudRootServiceHelper<DbPlanet> getDbPlanetCrud() {
        return dbPlanetCrud;
    }

    @Override
    public Collection<Planet> getRunningPlanets() {
        synchronized (planetImpls) {
            return new ArrayList<Planet>(planetImpls.values());
        }
    }

    @Override
    public void activate() {
        if (!planetImpls.isEmpty()) {
            throw new IllegalStateException("Start can only be called if no planet has been started before");
        }
        for (DbPlanet dbPlanet : dbPlanetCrud.readDbChildren()) {
            try {
                createPlanet(dbPlanet);
            } catch (Exception e) {
                ExceptionHandler.handleException(e, "Error during planet startup");
            }
        }
    }

    @Override
    public void activatePlanet(int planetId) {
        getPlanet(planetId).activate(dbPlanetCrud.readDbChild(planetId));
    }

    @Override
    public void deactivatePlanet(int planetId) {
        getPlanet(planetId).deactivate();
    }

    @Override
    public void beforeRestore() {
        synchronized (planetImpls) {
            for (PlanetImpl planet : planetImpls.values()) {
                planet.beforeRestore();
            }
        }
    }

    @Override
    public void afterRestore() {
        synchronized (planetImpls) {
            for (PlanetImpl planet : planetImpls.values()) {
                planet.afterRestore(dbPlanetCrud.readDbChild(planet.getPlanetServices().getPlanetInfo().getPlanetId()));
            }
        }
    }

    @Override
    public void restore(Collection<Base> bases, Collection<SyncBaseObject> syncBaseObjects) {
        Set<Planet> allPlanets = new HashSet<>();
        Map<Planet, Collection<Base>> planetBases = new HashMap<>();
        for (Base base : bases) {
            Collection<Base> tmpBases = planetBases.get(base.getPlanet());
            if (tmpBases == null) {
                tmpBases = new ArrayList<>();
                planetBases.put(base.getPlanet(), tmpBases);
                allPlanets.add(base.getPlanet());
            }
            tmpBases.add(base);
        }
        Map<Planet, Collection<SyncBaseObject>> planetSyncItems = new HashMap<>();
        for (SyncBaseObject syncBaseObject : syncBaseObjects) {
            Planet planet = getPlanet(syncBaseObject.getBase().getPlanetId());
            Collection<SyncBaseObject> tmpSyncItem = planetSyncItems.get(planet);
            if (tmpSyncItem == null) {
                tmpSyncItem = new ArrayList<>();
                planetSyncItems.put(planet, tmpSyncItem);
                allPlanets.add(planet);
            }
            tmpSyncItem.add(syncBaseObject);
        }

        for (Planet planet : allPlanets) {
            planet.restore(planetBases.get(planet), planetSyncItems.get(planet));
        }
    }

    private void createPlanet(DbPlanet dbPlanet) {
        PlanetImpl planet = new PlanetImpl();
        planet.init(serverGlobalServices);
        synchronized (planetImpls) {
            planetImpls.put(dbPlanet.getId(), planet);
        }
        planet.activate(dbPlanet);
    }

    private void destroyPlanet(Planet planet) {
        planet.deactivate();
        synchronized (planetImpls) {
            planetImpls.remove(planet.getPlanetServices().getPlanetInfo().getPlanetId());
        }
    }

    @Override
    public List<Map.Entry<PlanetInfo, List<SimpleBase>>> getAllSimpleBases() {
        List<Map.Entry<PlanetInfo, List<SimpleBase>>> entries = new ArrayList<>();
        for (Planet planet : planetImpls.values()) {
            entries.add(new SimpleEntry<>(planet.getPlanetServices().getPlanetInfo(), planet.getPlanetServices().getBaseService().getSimpleBases()));
        }
        return entries;
    }

    @Transactional
    @Override
    @Secured(SecurityRoles.ROLE_ADMINISTRATOR)
    public void saveTerrain(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int planetId) {
        DbPlanet dbPlanet = dbPlanetCrud.readDbChild(planetId);
        DbTerrainSetting dbTerrainSetting = dbPlanet.getDbTerrainSetting();
        TerrainDbUtil.modifyTerrainSetting(dbTerrainSetting, terrainImagePositions, surfaceRects, terrainImageService);
        dbPlanetCrud.updateDbChild(dbPlanet);
    }

    @Override
    public Collection<Planet> getAllPlanets() {
        return new ArrayList<Planet>(planetImpls.values());
    }

    @Override
    public List<PlanetLiteInfo> getAllPlanetLiteInfos() {
        List<PlanetLiteInfo> planetLiteInfos = new ArrayList<>();
        for (PlanetImpl planet : planetImpls.values()) {
            planetLiteInfos.add(planet.getPlanetServices().getPlanetInfo().getPlanetLiteInfo());
        }
        return planetLiteInfos;
    }

    @Override
    public List<OnlineUserDTO> getAllOnlineUsers() {
        List<OnlineUserDTO> onlineBases = new ArrayList<>();
        for (PlanetImpl planet : planetImpls.values()) {
            onlineBases.addAll(planet.getPlanetServices().getConnectionService().getOnlineConnections());
        }
        return onlineBases;
    }

    @Override
    @Transactional(readOnly = true)
    public StartPointInfo createStartPoint(PlanetInfo planetInfo, boolean setStartPosition) {
        DbPlanet dbPlanet = getDbPlanetCrud().readDbChild(planetInfo.getPlanetId());
        return dbPlanet.createStartPoint(setStartPosition);
    }

    @Override
    public void sendPacket(UserState userState, Packet packet) {
        try {
            boolean sent = false;
            for (PlanetImpl planet : planetImpls.values()) {
                if (planet.getPlanetServices().getConnectionService().sendPacket(userState, packet)) {
                    sent = true;
                    break;
                }
            }
            if (!sent && packet instanceof StorablePacket) {
                userState.saveStorablePackage((StorablePacket) packet);
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        try {
            for (PlanetImpl planet : planetImpls.values()) {
                planet.getPlanetServices().getConnectionService().sendPacket(packet);
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    @Override
    public void sendMessage(UserState userState, String key, Object[] args, boolean showRegisterDialog) {
        try {
            for (PlanetImpl planet : planetImpls.values()) {
                planet.getPlanetServices().getConnectionService().sendMessage(userState, key, args, showRegisterDialog);
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    @Override
    public void setChatMessageFilter(UserState userState, ChatMessageFilter chatMessageFilter) throws NotAGuildMemberException {
        if (chatMessageFilter == ChatMessageFilter.GUILD && guildService.getGuildId(userState) == null) {
            throw new NotAGuildMemberException();
        }
        try {
            for (PlanetImpl planet : planetImpls.values()) {
                planet.getPlanetServices().getConnectionService().setChatMessageFilter(userState, chatMessageFilter);
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    @Override
    public ServerPlanetServices getPlanet4BaselessConnection(UserState userState) {
        try {
            for (PlanetImpl planet : planetImpls.values()) {
                if (planet.getPlanetServices().getConnectionService().hasConnection(userState)) {
                    return planet.getPlanetServices();
                }
            }
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
        return null;
    }

    @Override
    public ImageHolder getStarMapImage(int planetId) {
        DbPlanet dbPlanet = getDbPlanetCrud().readDbChild(planetId);
        return dbPlanet.getStarMapImage();
    }

    @Override
    public StarMapInfo getStarMapInfo() {
        StarMapInfo starMapInfo = new StarMapInfo();
        Collection<StarMapPlanetInfo> starMapPlanetInfos = new ArrayList<>();
        synchronized (planetImpls) {
            for (PlanetImpl planet : planetImpls.values()) {
                try {
                    DbPlanet dbPlanet = getDbPlanetCrud().readDbChild(planet.getPlanetServices().getPlanetInfo().getPlanetLiteInfo().getPlanetId());
                    if(dbPlanet.getStarMapImagePosition() == null || dbPlanet.getMinLevel()== null) {
                        continue;
                    }
                    StarMapPlanetInfo starMapPlanetInfo = new StarMapPlanetInfo();
                    starMapPlanetInfo.setPlanetLiteInfo(planet.getPlanetServices().getPlanetInfo().getPlanetLiteInfo());
                    starMapPlanetInfo.setPosition(dbPlanet.getStarMapImagePosition());
                    starMapPlanetInfo.setMinLevel(dbPlanet.getMinLevel().getNumber());
                    starMapPlanetInfo.setSize(dbPlanet.getSize());
                    planet.getPlanetServices().getBaseService().fillBaseStatistics(starMapPlanetInfo);
                    starMapPlanetInfos.add(starMapPlanetInfo);
                } catch (Exception e) {
                    ExceptionHandler.handleException(e, "Unable setting up planet '" + planet.getPlanetServices().getPlanetInfo().getName() + "' for star map");
                }
            }
        }
        starMapInfo.setStarMapPlanetInfos(starMapPlanetInfos);
        return starMapInfo;
    }

    @Override
    public boolean hasMinimalLevel(Integer planetId) {
        DbPlanet dbPlanet = getDbPlanetCrud().readDbChild(planetId);
        return userGuidanceService.getLevelScope().getNumber() >= dbPlanet.getMinLevel().getNumber();
    }
}
