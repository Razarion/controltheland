package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import scenario.Scenario;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 13:25
 */
public class Bypass extends Scenario {
    private SyncItem syncItem;

    @Override
    public void addItems() {
        createSyncItem(50, new Index(200, 200), "Blocker");
        syncItem = createSyncItem(10, new Index(100, 180), "Mover");
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        getMovingModel().getCollisionService().findPath(syncItem, new Index(300, 180));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
