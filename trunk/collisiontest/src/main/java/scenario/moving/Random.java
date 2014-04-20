package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.BlockingStateException;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import gui.MovingGui;
import scenario.Scenario;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 12:46
 */
public class Random extends Scenario {
    public static final int RADIUS = 10;
    private static final int ITEM_CREATION_DISTANCE = 20;
    private static final int SYNC_ITEM_COUNT = 50;

    @Override
    public void addItems() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void start() throws NoBetterPathFoundException {

    }

    @Override
    public void tick() {
        int activeItems = 0;
        Collection<SyncItem> deadItems = new ArrayList<SyncItem>();
        for (SyncItem syncItem : getMovingModel().getSyncItems()) {
            if (syncItem.getState() == SyncItem.MoveState.STOPPED) {
                deadItems.add(syncItem);
            } else {
                activeItems++;
            }
        }
        for (SyncItem deadItem : deadItems) {
            deleteSyncItem(deadItem);
        }
        while (activeItems < SYNC_ITEM_COUNT) {
            try {
                SyncItem syncItem = createSyncItem(RADIUS, createRandomPosition(), "undefined");
                if (getMovingModel().getCollisionService().isOverlapping(syncItem, syncItem.getDecimalPosition(), 0) == null) {
                    getMovingModel().getCollisionService().findPath(syncItem, createRandomPosition());
                    activeItems++;
                }
            } catch (BlockingStateException e) {
                // Ignore
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Index createRandomPosition() {
        return new Index((int) (Math.random() * (MovingGui.WIDTH - 2 * ITEM_CREATION_DISTANCE)) + ITEM_CREATION_DISTANCE,
                (int) (Math.random() * (MovingGui.HEIGHT - 2 * ITEM_CREATION_DISTANCE)) + ITEM_CREATION_DISTANCE);
    }
}
