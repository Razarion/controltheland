package com.btxtech.game.jsre.common.gameengine.services.collision;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Vector;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import model.MovingModel;

import javax.xml.ws.Holder;

/**
 * Created by beat
 * on 08.06.2014.
 */
public class ForceField {
    public static final int FIELD_SIZE = 20;
    private static final int FORCE_FIELD_DISTANCE = 3;
    private int xCount;
    private int yCount;
    private double[][] filed;

    public void init(Terrain terrain) {
        xCount = (int) Math.floor(TerrainUtil.getAbsolutXForTerrainTile(terrain.getXCount()) / FIELD_SIZE);
        yCount = (int) Math.floor(TerrainUtil.getAbsolutXForTerrainTile(terrain.getYCount()) / FIELD_SIZE);
        filed = new double[xCount][yCount];
    }

    public void calculateForce(SyncItem protagonist, MovingModel movingModel) {
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                filed[x][y] = sumForce(movingModel, protagonist, x, y);
            }
        }
    }

    private double sumForce(MovingModel movingModel, final SyncItem protagonist, int x, int y) {
        final DecimalPosition fieldPosition = new DecimalPosition(x * FIELD_SIZE + FIELD_SIZE / 2, y * FIELD_SIZE + FIELD_SIZE / 2);
        DecimalPosition targetForce = protagonist.getTargetPosition().sub(fieldPosition).normalize();

        final Holder<DecimalPosition> fieldForce = new Holder<>(Vector.NULL_POSITION);
        final Holder<Double> insideAngel = new Holder<>();
        movingModel.iterateOverSyncItems(new MovingModel.SyncItemCallback() {
            @Override
            public void onSyncItem(SyncItem other) {
                if (other == protagonist) {
                    return;
                }
                double distance = other.getDecimalPosition().getDistance(fieldPosition) - other.getRadius();
                if (distance < 0) {
                    // inside
                    insideAngel.value = other.getDecimalPosition().getAngleToNord(fieldPosition);
                } else if (distance <= FIELD_SIZE * FORCE_FIELD_DISTANCE) {
                    fieldForce.value = fieldForce.value.add(fieldPosition.sub(other.getDecimalPosition()).normalize(1.0 - distance / (FIELD_SIZE * FORCE_FIELD_DISTANCE)));
                }
            }
        });
        if (insideAngel.value != null) {
            return insideAngel.value;
        }
        return fieldForce.value.add(targetForce).getAngleToNorth();
    }

    public int getXCount() {
        return xCount;
    }

    public int getYCount() {
        return yCount;
    }

    public double getAngel(int x, int y) {
        return filed[x][y];
    }

    public double getAngel(SyncItem syncItem) {
        Index position = syncItem.getDecimalPosition().divide(FIELD_SIZE).getPositionFloor();
        return filed[position.getX()][position.getY()];
    }
}
