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
public class Frontal extends Scenario {
    public static final int RADIUS = 10;
    private SyncItem syncItem1;
    private SyncItem syncItem2;

    @Override
    public void addItems() {
        syncItem1 = createSyncItem(RADIUS, new Index(100, 100), "SyncItem 1", true);
        syncItem2 = createSyncItem(RADIUS, new Index(400, 100), "SyncItem 2", false);
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        getMovingModel().getCollisionService().findPath(syncItem1, new Index(400, 100));
        getMovingModel().getCollisionService().findPath(syncItem2, new Index(100, 100));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
