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
public class MoveToOccupiedPosition extends Scenario {
    private static final int RADIUS = 10;
    private SyncItem syncItem;

    @Override
    public void addItems() {
        createSyncItem(RADIUS, new Index(200, 200), "blocker");
        syncItem = createSyncItem(RADIUS, new Index(100, 100), "mover");
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        getMovingModel().getCollisionService().findPath(syncItem, new Index(200, 200));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
