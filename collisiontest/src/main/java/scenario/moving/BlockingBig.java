package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import scenario.Scenario;

import java.util.List;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 13:25
 */
public class BlockingBig extends Scenario {
    @Override
    public void addItems(List<SyncItem> syncItems, CollisionService collisionService) {
 /*       syncItems.add(new SyncItem(RADIUS, new Index(200, 130), "undefined"));
        syncItems.add(new SyncItem(RADIUS, new Index(200, 150), "undefined"));
        syncItems.add(new SyncItem(RADIUS, new Index(200, 170), "undefined"));
        syncItems.add(new SyncItem(RADIUS, new Index(200, 190), "undefined"));
        syncItems.add(new SyncItem(RADIUS, new Index(200, 210), "undefined"));
        syncItems.add(new SyncItem(RADIUS, new Index(200, 230), "undefined"));
        syncItems.add(new SyncItem(RADIUS, new Index(200, 250), "undefined"));
        syncItems.add(new SyncItem(RADIUS, new Index(200, 270), "undefined"));
        SyncItem syncItem = new SyncItem(RADIUS, new Index(100, 200), "undefined");
        syncItem.setTargetPosition(new Index(300,200));
        syncItems.add(syncItem);  */
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(List<SyncItem> collisionService) {
    }
}
