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
    private SyncItem protagonist;
    //private SyncItem mover1;

    @Override
    public void addItems() {
        protagonist = createSyncItem(RADIUS, new Index(100, 100), "Mover", true);
        createSyncItem(RADIUS, new Index(200, 80), "Blocker 1", false);
        //createSyncItem(RADIUS, new Index(200, 120), "Blocker 2", false);
        //createSyncItem(RADIUS, new Index(160, 130), "Blocker 3", false);
        //mover1 = createSyncItem(RADIUS, new Index(160, 60), "Mover 1", false);
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        getMovingModel().getCollisionService().findPath(protagonist, new Index(400, 100));
        //getMovingModel().getCollisionService().findPath(mover1, new Index(160, 200));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
