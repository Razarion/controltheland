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
public class Blocking extends Scenario {
    public static final int RADIUS = 10;
    private SyncItem syncItem1;

    @Override
    public void addItems() {
        syncItem1 = createSyncItem(RADIUS, new Index(100, 100), "Mover");
        createSyncItem(RADIUS, new Index(215, 85), "Blocker 1");
        createSyncItem(RADIUS, new Index(200, 115), "Blocker 2");
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        getMovingModel().getCollisionService().findPath(syncItem1, new Index(400, 100));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
