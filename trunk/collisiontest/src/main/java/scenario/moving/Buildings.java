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
public class Buildings extends Scenario {
    @Override
    public void addItems(List<SyncItem> syncItems, CollisionService collisionService) {
/*        syncItems.add(new SyncItem(50, new Index(200, 200), "undefined"));
        syncItems.add(new SyncItem(50, new Index(300, 200), "undefined"));
        syncItems.add(new SyncItem(50, new Index(400, 200), "undefined"));
        SyncItem syncItem = new SyncItem(10, new Index(100, 200), "undefined");
        syncItem.setTargetPosition(new Index(600,200));
        syncItems.add(syncItem); */
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(List<SyncItem> collisionService) {
    }
}
