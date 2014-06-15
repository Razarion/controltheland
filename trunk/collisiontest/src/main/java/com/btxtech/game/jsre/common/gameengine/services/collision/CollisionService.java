package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import model.MovingModel;

/**
 * User: beat
 * Date: 21.03.13
 * Time: 02:00
 */
public class CollisionService {
    private final MovingModel movingModel;
    public static final double DENSITY_OF_ITEM = 0.5;
    public static final double MAX_DISTANCE = 100;

    public CollisionService(MovingModel movingModel) {
        this.movingModel = movingModel;
    }

    public void init(Terrain terrain) {
        int xTiles = (int) Math.ceil(terrain.getXCount() * Constants.TERRAIN_TILE_WIDTH / Constants.COLLISION_TILE_WIDTH);
        int yTiles = (int) Math.ceil(terrain.getYCount() * Constants.TERRAIN_TILE_HEIGHT / Constants.COLLISION_TILE_HEIGHT);
    }

    public void moveItem(Terrain terrain, MovingModel movingModel, final SyncItem syncItem, double factor) {
        final VelocityObstacleManager velocityObstacleManager = new VelocityObstacleManager(syncItem);
        movingModel.iterateOverSyncItems(new MovingModel.SyncItemCallback() {
            @Override
            public void onSyncItem(SyncItem other) {
                velocityObstacleManager.inspect(other);
            }
        });

        Double bestAngel = velocityObstacleManager.getBestAngel();
        if(bestAngel != null) {
            syncItem.setSpeed(SyncItem.SPEED);
            syncItem.setAimAngel(bestAngel);
        }else{
            syncItem.setSpeed(0);
        }
        syncItem.executeMove(factor);

        if (!isBetterPositionAvailable(syncItem)) {
            syncItem.stop();
        }
    }

    private boolean isBetterPositionAvailable(SyncItem syncItem) {
        return !syncItem.getPosition().equals(syncItem.getTargetPosition().getPosition())
                && movingModel.calculateDensityOfItems(syncItem.getTargetPosition().getPosition(), syncItem.getPosition().getDistance(syncItem.getTargetPosition().getPosition())) < DENSITY_OF_ITEM;
    }

    public void findPath(SyncItem syncItem, Index targetPosition) {
        syncItem.moveTo(targetPosition);
    }

}
