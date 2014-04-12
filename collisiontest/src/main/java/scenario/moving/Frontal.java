package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import scenario.Scenario;

import java.util.List;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 13:25
 */
public class Frontal extends Scenario {
    public static final int RADIUS = 10;
    public static final int SOFT_RADIUS = 70;
    private SyncItem syncItem1;
    private SyncItem syncItem2;

    @Override
    public void addItems(List<SyncItem> syncItems, CollisionService collisionService) {
        syncItem1 = createSyncItem(collisionService, syncItems, RADIUS, new Index(100, 100), "SyncItem 1");
        syncItem2 = createSyncItem(collisionService, syncItems, RADIUS, new Index(400, 100), "SyncItem 2");
    }

    @Override
    public void start(List<SyncItem> syncItems, CollisionService collisionService) throws NoBetterPathFoundException {
        collisionService.findPath(syncItem1, new Index(400, 100));
        collisionService.findPath(syncItem2, new Index(100, 100));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick(List<SyncItem> collisionService) {
    }
}
