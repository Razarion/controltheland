package com.btxtech.game.jsre.common.gameengine.services;

import com.btxtech.game.jsre.client.GameEngineMode;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainListener;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.gameengine.ItemDoesNotExistException;
import com.btxtech.game.jsre.common.gameengine.itemType.BaseItemType;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.jsre.common.gameengine.itemType.ItemType;
import com.btxtech.game.jsre.common.gameengine.services.action.CommonActionService;
import com.btxtech.game.jsre.common.gameengine.services.base.AbstractBaseService;
import com.btxtech.game.jsre.common.gameengine.services.base.HouseSpaceExceededException;
import com.btxtech.game.jsre.common.gameengine.services.base.ItemLimitExceededException;
import com.btxtech.game.jsre.common.gameengine.services.bot.CommonBotService;
import com.btxtech.game.jsre.common.gameengine.services.collision.CommonCollisionService;
import com.btxtech.game.jsre.common.gameengine.services.connection.ConnectionService;
import com.btxtech.game.jsre.common.gameengine.services.energy.EnergyService;
import com.btxtech.game.jsre.common.gameengine.services.itemTypeAccess.ItemTypeAccess;
import com.btxtech.game.jsre.common.gameengine.services.items.ItemService;
import com.btxtech.game.jsre.common.gameengine.services.items.NoSuchItemTypeException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.AbstractTerrainService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceRect;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImage;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainImagePosition;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainSettings;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.jsre.common.gameengine.services.territory.AbstractTerritoryService;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 25.11.2011
 * Time: 01:14:54
 */
public class MinimalService implements Services {
    private static final Services INSTANCE = new MinimalService();

    public static Services getInstance() {
        return INSTANCE;
    }

    @Override
    public ItemService getItemService() {
        return new ItemService() {
            @Override
            public SyncItem getItem(Id id) throws ItemDoesNotExistException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public List<SyncBaseItem> getBaseItems(List<Id> baseItemsIds) throws ItemDoesNotExistException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public List<Id> getBaseItemIds(List<SyncBaseItem> baseItems) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void killSyncItem(SyncItem killedItem, SimpleBase actor, boolean force, boolean explode) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SyncItem createSyncObject(ItemType toBeBuilt, Index position, SyncBaseItem creator, SimpleBase base, int createdChildCount) throws NoSuchItemTypeException, ItemLimitExceededException, HouseSpaceExceededException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean baseObjectExists(SyncItem currentBuildup) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public ItemType getItemType(int itemTypeId) throws NoSuchItemTypeException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public ItemType getItemType(String name) throws NoSuchItemTypeException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public List<ItemType> getItemTypes() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean areItemTypesLoaded() {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SyncItem newSyncItem(Id id, Index position, int itemTypeId, SimpleBase base, Services services) throws NoSuchItemTypeException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public List<BaseItemType> ableToBuild(BaseItemType toBeBuilt) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<SyncBaseItem> getItems4Base(SimpleBase simpleBase) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public List<? extends SyncItem> getItems(ItemType itemType, SimpleBase simpleBase) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public List<? extends SyncItem> getItems(String itemTypeName, SimpleBase simpleBase) throws NoSuchItemTypeException {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<SyncBaseItem> getEnemyItems(SimpleBase base, Rectangle region, boolean ignoreBot) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean hasStandingItemsInRect(Rectangle rectangle, SyncItem exceptThat) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isSyncItemOverlapping(SyncItem syncItem) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isSyncItemOverlapping(SyncItem syncItem, Index positionToCheck, Double angelToCheck, Collection<SyncItem> exceptionThem) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isUnmovableSyncItemOverlapping(BoundingBox boundingBox, Index positionToCheck) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void checkBuildingsInRect(BaseItemType toBeBuiltType, Index toBeBuildPosition) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<SyncBaseItem> getBaseItemsInRadius(Index position, int radius, SimpleBase simpleBase, Collection<BaseItemType> baseItemTypeFilter) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<SyncBaseItem> getBaseItemsInRectangle(Rectangle rectangle, SimpleBase simpleBase, Collection<BaseItemType> baseItemTypeFilter) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean hasItemsInRectangle(Rectangle rectangle) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void killSyncItems(Collection<SyncItem> syncItems) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SyncBaseItem getFirstEnemyItemInRange(SyncBaseItem baseSyncItem) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @Override
    public AbstractTerrainService getTerrainService() {
        return new AbstractTerrainService() {
            @Override
            public Collection<TerrainImagePosition> getTerrainImagePositions() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<SurfaceRect> getSurfaceRects() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public TerrainSettings getTerrainSettings() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void addTerrainListener(TerrainListener terrainListener) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public List<TerrainImagePosition> getTerrainImagesInRegion(Rectangle absolutePxRectangle) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SurfaceImage getSurfaceImage(SurfaceRect surfaceRect) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<TerrainImage> getTerrainImages() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Collection<SurfaceImage> getSurfaceImages() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Rectangle getTerrainImagePositionRectangle(TerrainImagePosition terrainImagePosition) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public TerrainImage getTerrainImage(TerrainImagePosition terrainImagePosition) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public TerrainImagePosition getTerrainImagePosition(int absoluteX, int absoluteY) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public TerrainImagePosition getTerrainImagePosition(Index tileIndex) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SurfaceRect getSurfaceRect(int absoluteX, int absoluteY) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SurfaceRect getSurfaceRect(Index tileIndex) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Index getTerrainTileIndexForAbsPosition(int x, int y) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getTerrainTileIndexForAbsXPosition(int x) {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getTerrainTileIndexForAbsYPosition(int y) {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Index getTerrainTileIndexForAbsPosition(Index absolutePos) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Index getAbsolutIndexForTerrainTileIndex(Index tileIndex) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Index getTerrainTileIndexForAbsPositionRoundUp(Index absolutePos) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Index getAbsolutIndexForTerrainTileIndex(int xTile, int yTile) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getAbsolutXForTerrainTile(int xTile) {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getAbsolutYForTerrainTile(int yTile) {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Rectangle convertToTilePosition(Rectangle rectangle) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Rectangle convertToTilePositionRoundUp(Rectangle rectangle) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Rectangle convertToAbsolutePosition(Rectangle rectangle) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isFreeZeroSize(Index point, ItemType itemType) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isFree(Index middlePoint, int itemFreeWidth, int itemFreeHeight, Collection<SurfaceType> allowedSurfaces) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isFree(Index middlePoint, ItemType itemType) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isTerrainPassable(Index posititon) {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SurfaceType getSurfaceType(Index tileIndex) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public SurfaceType getSurfaceTypeAbsolute(Index absoluteIndex) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Index getNearestPoint(TerrainType allowedTerrainType, Index absoluteDestination, int maxRadius) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Index correctPosition(SyncItem syncItem, Index position) {
                return position;
            }

            @Override
            public Map<TerrainType, boolean[][]> createSurfaceTypeField() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @Override
    public AbstractBaseService getBaseService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ConnectionService getConnectionService() {
        return new ConnectionService() {

            @Override
            public void sendSyncInfo(SyncItem syncItem) {
            }

            @Override
            public GameEngineMode getGameEngineMode() {
                return GameEngineMode.MASTER;
            }
        };
    }

    @Override
    public ItemTypeAccess getItemTypeAccess() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EnergyService getEnergyService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CommonCollisionService getCollisionService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CommonActionService getActionService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AbstractTerritoryService getTerritoryService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CommonBotService getBotService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
