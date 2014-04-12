package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import gui.MovingGui;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import scenario.Scenario;

import java.util.Iterator;
import java.util.List;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 12:46
 */
public class Random extends Scenario {
    public static final int RADIUS = 10;
    public static final int SOFT_RADIUS = 70;
    private static final int ITEM_CREATION_DISTANCE = 20;
    private static final int SYNC_ITEM_COUNT = 50;
    private CollisionService collisionService;

    public Random(CollisionService collisionService) {
        this.collisionService = collisionService;
    }

    @Override
    public void addItems(List<SyncItem> syncItems, CollisionService collisionService) {
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(List<SyncItem> syncItemLists) {
 /*       synchronized (syncItemLists) {
            int activeItems = 0;
            for (Iterator<SyncItem> iterator = syncItemLists.iterator(); iterator.hasNext(); ) {
                SyncItem syncItem = iterator.next();
                if (syncItem.isMoving()) {
                    activeItems++;
                } else {
                    iterator.remove();
                }
            }
            while (activeItems < SYNC_ITEM_COUNT) {
                SyncItem syncItem = new SyncItem(RADIUS, createRandomPosition(), "undefined");
                if (!collisionService.isOverlapping(syncItem, syncItem.getDecimalPosition())) {
                    syncItem.setTargetPosition(createRandomPosition());
                    syncItemLists.add(syncItem);
                    activeItems++;
                }
            }
        } */
    }

    private Index createRandomPosition() {
        return new Index((int) (Math.random() * (MovingGui.WIDTH - 2 * ITEM_CREATION_DISTANCE)) + ITEM_CREATION_DISTANCE,
                (int) (Math.random() * (MovingGui.HEIGHT - 2 * ITEM_CREATION_DISTANCE)) + ITEM_CREATION_DISTANCE);
    }
}
