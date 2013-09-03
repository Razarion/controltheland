package com.btxtech.game.services.planet;

import com.btxtech.game.jsre.client.NotAGuildMemberException;
import com.btxtech.game.jsre.client.PositionInBotException;
import com.btxtech.game.jsre.client.cockpit.chat.ChatMessageFilter;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.info.InvalidLevelStateException;
import com.btxtech.game.jsre.client.common.info.StartPointInfo;
import com.btxtech.game.jsre.client.dialogs.starmap.StarMapInfo;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.services.PlanetInfo;
import com.btxtech.game.jsre.common.gameengine.services.PlanetLiteInfo;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseObject;
import com.btxtech.game.jsre.common.packets.Packet;
import com.btxtech.game.services.common.CrudRootServiceHelper;
import com.btxtech.game.services.common.ImageHolder;
import com.btxtech.game.services.common.ServerPlanetServices;
import com.btxtech.game.services.connection.OnlineUserDTO;
import com.btxtech.game.services.inventory.DbInventoryItem;
import com.btxtech.game.services.planet.db.DbPlanet;
import com.btxtech.game.services.user.User;
import com.btxtech.game.services.user.UserState;

import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 27.08.12
 * Time: 02:29
 */
public interface PlanetSystemService {
    void createBase(ServerPlanetServices serverPlanetServices, UserState userState, Index position) throws InvalidLevelStateException, PositionInBotException, NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException;

    boolean hasPlanet(UserState userState);

    boolean hasPlanet();

    boolean isUserOnCorrectPlanet(UserState userState);

    Planet getPlanet(SimpleBase simpleBase) throws NoSuchPlanetException;

    Planet getPlanet(UserState userState) throws NoSuchPlanetException;

    Planet getPlanet(int planetId) throws NoSuchPlanetException;

    Planet getPlanet(DbPlanet dbPlanet) throws NoSuchPlanetException;

    void useInventoryItem(UserState userState, SimpleBase simpleBase, DbInventoryItem dbInventoryItem, Collection<Index> positionToBePlaced) throws ItemLimitExceededException, HouseSpaceExceededException, NoSuchItemTypeException;

    DbPlanet openDbSession(PlanetInfo planetInfo);

    void closeDbSession();

    void onUserStateRemoved(UserState userState);

    void onUserRegistered();

    CrudRootServiceHelper<DbPlanet> getDbPlanetCrud();

    Collection<Planet> getRunningPlanets();

    ServerPlanetServices getServerPlanetServices(int planetId) throws NoSuchPlanetException;

    ServerPlanetServices getServerPlanetServices(SimpleBase simpleBase) throws NoSuchPlanetException;

    ServerPlanetServices getServerPlanetServices() throws NoSuchPlanetException;

    ServerPlanetServices getServerPlanetServices(User user) throws NoSuchPlanetException;

    ServerPlanetServices getServerPlanetServices(UserState userState) throws NoSuchPlanetException;

    ServerPlanetServices getPlanetSystemService(UserState userState, Integer planetId) throws InvalidLevelStateException;

    void activate();

    void activatePlanet(int planetId);

    void deactivatePlanet(int planetId);

    void beforeRestore();

    void restore(Collection<Base> bases, Collection<SyncBaseObject> syncBaseObjects);

    void afterRestore();

    List<SimpleBase> getAllSimpleBases();

    void saveTerrain(Collection<TerrainImagePosition> terrainImagePositions, Collection<SurfaceRect> surfaceRects, int planetId);

    Collection<Planet> getAllPlanets();

    List<PlanetLiteInfo> getAllPlanetLiteInfos();

    List<OnlineUserDTO> getAllOnlineUsers();

    StartPointInfo createStartPoint(PlanetInfo planetInfo, boolean setStartPosition);

    void sendPacket(UserState userState, Packet packet);

    void sendPacket(Packet packet);

    ServerPlanetServices getPlanet4BaselessConnection(UserState userState);

    void sendMessage(UserState userState, String key, Object[] args, boolean showRegisterDialog);

    void setChatMessageFilter(UserState userState, ChatMessageFilter chatMessageFilter) throws NotAGuildMemberException;

    ImageHolder getStarMapImage(int planetId);

    StarMapInfo getStarMapInfo();

    boolean hasMinimalLevel(Integer planetId);
}