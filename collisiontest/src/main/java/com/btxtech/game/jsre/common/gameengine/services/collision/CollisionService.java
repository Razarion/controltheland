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
    public static final double DENSITY_OF_ITEM = 0.2;
    public static final double MAX_DISTANCE = 100;

    public CollisionService(MovingModel movingModel) {
        this.movingModel = movingModel;
    }

    public void init(Terrain terrain) {
        int xTiles = (int) Math.ceil(terrain.getXCount() * Constants.TERRAIN_TILE_WIDTH / Constants.COLLISION_TILE_WIDTH);
        int yTiles = (int) Math.ceil(terrain.getYCount() * Constants.TERRAIN_TILE_HEIGHT / Constants.COLLISION_TILE_HEIGHT);
    }

    public void moveItem(Terrain terrain, MovingModel movingModel, final SyncItem syncItem, double factor) {
        // DecimalPosition steering = syncItem.getSteering().add(doSeek(syncItem));
        //ForceField forceField = new ForceField();
        //forceField.init(terrain);
        //forceField.calculateForce(syncItem, movingModel);
        //syncItem.setDecimalPosition(syncItem.getDecimalPosition().getPointFromAngelToNord(forceField.getAngel(syncItem), factor * SyncItem.SPEED));
        final VelocityObstacleManager velocityObstacleManager = new VelocityObstacleManager(syncItem);
        movingModel.iterateOverSyncItems(new MovingModel.SyncItemCallback() {
            @Override
            public void onSyncItem(SyncItem other) {
                velocityObstacleManager.inspect(other);
            }
        });

        syncItem.setSpeed(SyncItem.SPEED);
        if (velocityObstacleManager.isEmpty()) {
            syncItem.setAngel(syncItem.getDecimalPosition().getAngleToNord(syncItem.getTargetPosition()));
        } else {
            syncItem.setAngel(velocityObstacleManager.getBestAngel());
        }
        syncItem.setDecimalPosition(syncItem.getDecimalPosition().getPointFromAngelToNord(syncItem.getAngel(), factor * SyncItem.SPEED));


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
