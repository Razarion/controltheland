package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import scenario.Scenario;

import java.util.List;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 13:25
 */
public class Bypass2 extends Scenario {
    private SyncItem syncItem;

    @Override
    public void addItems(List<SyncItem> syncItems, CollisionService collisionService) {
        createSyncItem(collisionService, syncItems, 50, new Index(150, 250), "Blocker");
        syncItem = createSyncItem(collisionService, syncItems, 10, new Index(150, 150), "Mover");
    }

    @Override
    public void start(List<SyncItem> syncItems, CollisionService collisionService) throws NoBetterPathFoundException {
        collisionService.findPath(syncItem, new Index(150, 350));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(List<SyncItem> collisionService) {
    }
}
