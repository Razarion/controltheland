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
public class MoveToPosition extends Scenario {
    public static final int RADIUS = 10;
    public static final int SOFT_RADIUS = 70;

    @Override
    public void addItems(List<SyncItem> syncItems, CollisionService collisionService) {
/*        for(int y = 100; y < 500 ; y += 50) {
            for(int x = 100; x < 500 ; x += 50) {
                SyncItem syncItem = new SyncItem(RADIUS, new Index(x, y), "undefined");
                syncItem.setTargetPosition(new Index(600, 600));
                syncItems.add(syncItem);
            }
        }    */
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(List<SyncItem> collisionService) {
    }
}
