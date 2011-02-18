package com.btxtech.game.services.overall.helpers;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.GameInfo;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.NotYourBaseException;
import com.btxtech.game.jsre.common.Packet;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.Id;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.AttackCommand;
import com.btxtech.game.jsre.common.gameengine.syncObjects.command.MoveCommand;
import com.btxtech.game.services.base.Base;
import com.btxtech.game.services.base.BaseColor;
import com.btxtech.game.services.base.BaseService;
import com.btxtech.game.services.gwt.MovableServiceImpl;
import com.btxtech.game.services.item.ItemService;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.terrain.Tile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: beat
 * Date: 28.10.2009
 * Time: 11:53:34
 */
@Component
public class GameTestHelper {
    public static final int BASE_COUNT = 64;
    public static final int THREAD_SLEEP = 100;
    public static final double ALLOWED_DELTA_HALF = 0.1;
    @Autowired
    private MovableServiceImpl movableService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private TerrainService terrainService;
    private List<BaseColor> baseColors;

    public void emptyGame() {
        baseService.restoreBases(new ArrayList<Base>());
        itemService.restoreItems(new ArrayList<SyncItem>());
        baseColors = baseService.getFreeColors(Integer.MAX_VALUE);
    }

    public void emptyTerrain(int xCount, int yCount) {
        terrainService.clearTerrain();
        Tile tile = terrainService.createTile(new byte[0], ItemType.LAND_ITEM);
        terrainService.createNewTerrain(xCount, yCount, tile);
    }

    public BaseHelper createBase(int number) throws Exception {
        baseService.createNewBase("player" + number, baseColors.get(number));
        GameInfo gameInfo = movableService.getGameInfo();

        // check that this base only has one construction vehicle and this CV is not moving
        int cvCount = 0;
        ConstructionVehicleSyncInfo constructionVehicleSyncInfo = null;
        for (SyncInfo syncInfo : movableService.getAllSyncInfo()) {
            if (syncInfo instanceof BaseSyncInfo) {
                BaseSyncInfo baseSyncInfo = (BaseSyncInfo) syncInfo;
                if (baseSyncInfo.getBase().equals(gameInfo.getBase())) {
                    if (baseSyncInfo.getItemType() == ItemType.CONSTRUCTION_VEHICLE) {
                        cvCount++;
                        constructionVehicleSyncInfo = (ConstructionVehicleSyncInfo) baseSyncInfo;
                    } else {
                        Assert.fail("Only construction vehilces are allowed here: " + baseSyncInfo.getItemType());
                    }
                }
            }
        }
        Assert.assertEquals(1, cvCount);
        //
        return new BaseHelper(gameInfo, constructionVehicleSyncInfo);
    }

    public Id getIdOf(SimpleBase simpleBase, ItemType itemType) {
        Collection<SyncInfo> syncInfos = movableService.getAllSyncInfo();
        for (SyncInfo syncInfo : syncInfos) {
            if (syncInfo instanceof BaseSyncInfo) {
                BaseSyncInfo baseSyncInfo = (BaseSyncInfo) syncInfo;
                if (baseSyncInfo.getBase().equals(simpleBase) && baseSyncInfo.getItemType() == itemType) {
                    return baseSyncInfo.getId();
                }
            }
        }
        throw new IllegalArgumentException("No such SyncInfo found");
    }

    public long calculateTravelTime(MovableSyncInfo movableSyncInfo) {
        double distance = 0;
        Index index = movableSyncInfo.getAbsolutePosition();
        for (Index nextPos : new ArrayList<Index>(movableSyncInfo.getPathToAbsoluteDestination())) {
            int deltaX = Math.abs(index.getX() - nextPos.getX());
            int deltaY = Math.abs(index.getY() - nextPos.getY());
            distance += Math.max(deltaX, deltaY);
            index = nextPos;
        }
        return (long) (distance / (double) movableSyncInfo.getItemType().getSpeed() * 1000.0);
    }

    public BaseSyncInfo getSyncInfo(SimpleBase simpleBase, Id id) throws NotYourBaseException {
        BaseSyncInfo resultSaseSyncInfo = null;
        for (Packet packet : movableService.getSyncInfo(simpleBase)) {
            if (packet instanceof BaseSyncInfo) {
                BaseSyncInfo baseSyncInfo = (BaseSyncInfo) packet;
                if (baseSyncInfo.getId().equals(id)) {
                    Assert.assertNull(resultSaseSyncInfo);
                    resultSaseSyncInfo = baseSyncInfo;
                }
            }
        }
        return resultSaseSyncInfo;
    }

    public Index getRandomPassableAbsolutePosition() {
        int xSize = terrainService.getTerrainField().length;
        int ySize = terrainService.getTerrainField()[0].length;

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            int x = (int) (Math.random() * (double) xSize);
            int y = (int) (Math.random() * (double) ySize);

            int tileId = terrainService.getTerrainField()[x][y];
            Tile tile = terrainService.getTile(tileId);
            if (tile.checkAllowedItem(ItemType.LAND_ITEM)) {
                Index absolueIndex = TerrainUtil.getAbsolutIndexForTerrainTileIndex(x, y);
                // Add random offset in tile
                absolueIndex.setX(absolueIndex.getX() + (int) (Math.random() * (double) Constants.TILE_WIDTH));
                absolueIndex.setY(absolueIndex.getY() + (int) (Math.random() * (double) Constants.TILE_HEIGHT));
                return absolueIndex;
            }
        }
        throw new IllegalArgumentException("No passable position on terrain found");
    }

    public void sendMoveCommand(Id id, Index destination) {
        MoveCommand moveCommand = new MoveCommand();
        moveCommand.setDestination(destination);
        moveCommand.setId(id);
        movableService.sendCommand(moveCommand);
    }

    public void sendAttackCommand(TankSyncItem attacker, TankSyncItem target) {
        AttackCommand attackCommand = new AttackCommand();
        attackCommand.setId(attacker.getId());
        attackCommand.setFollowTarget(true);
        attackCommand.setTarget(target.getId());
        movableService.sendCommand(attackCommand);
    }


    public ResultObject waitToReachTarget(SimpleBase myBase, Id cvId, Index start, Index destination) throws Exception {
        ConstructionVehicleSyncInfo constructionVehicleSyncInfo = (ConstructionVehicleSyncInfo) getSyncInfo(myBase, cvId);
        Assert.assertNotNull(constructionVehicleSyncInfo);
        Assert.assertNotNull(constructionVehicleSyncInfo.getPathToAbsoluteDestination());
        long time = System.currentTimeMillis();
        long travelTime = calculateTravelTime(constructionVehicleSyncInfo);
        System.out.println("Way takes: " + travelTime + "ms | SyncInfo: " + constructionVehicleSyncInfo);
        while (true) {
            long elapsed = System.currentTimeMillis() - time;
            constructionVehicleSyncInfo = (ConstructionVehicleSyncInfo) getSyncInfo(myBase, cvId);

            // Measuring time exeeded
            if (elapsed > 2 * travelTime && constructionVehicleSyncInfo == null) {
                return new ResultObject(start, destination, travelTime, -1, false);
            }

            if (constructionVehicleSyncInfo == null) {
                Thread.sleep(THREAD_SLEEP);
                continue;
            }

            boolean isOk = constructionVehicleSyncInfo.getPathToAbsoluteDestination() == null && constructionVehicleSyncInfo.getAbsolutePosition().equals(destination);

            // Elapsed time too small
            if (elapsed < travelTime * (1.0 - ALLOWED_DELTA_HALF)) {
                return new ResultObject(start, destination, travelTime, elapsed, isOk);
            }
            // Elapsed time too big
            if (elapsed > travelTime * (1.0 + ALLOWED_DELTA_HALF)) {
                return new ResultObject(start, destination, travelTime, elapsed, isOk);
            }
            // Elapsed time in range
            if (isOk) {
                return null;
            } else {
                return new ResultObject(start, destination, travelTime, elapsed, isOk);
            }
        }
    }


    public List<BaseColor> getBaseColors() {
        return baseColors;
    }
}
