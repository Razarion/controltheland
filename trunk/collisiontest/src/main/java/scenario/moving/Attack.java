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
public class Attack extends Scenario {

    @Override
    public void addItems(List<SyncItem> syncItems, CollisionService collisionService) {
 /*       SyncItem target = new SyncItem(RADIUS, new Index(300, 300), "undefined");
        syncItems.add(target);
        SyncItem attacker = new SyncItem(RADIUS, new Index(100, 200), "undefined");
        // attacker.setTarget(target);
        syncItems.add(attacker);  */
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(List<SyncItem> collisionService) {
    }
}
