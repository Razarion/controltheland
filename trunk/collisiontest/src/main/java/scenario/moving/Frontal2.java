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
public class Frontal2 extends Scenario {
    public static final int RADIUS = 10;
    private SyncItem syncItem1;
    private SyncItem syncItem2;
    private SyncItem syncItem3;
    private SyncItem syncItem4;
    private SyncItem syncItem5;
    private SyncItem syncItem6;
    private SyncItem syncItem7;
    private SyncItem syncItem8;

    @Override
    public void addItems() {
        syncItem1 = createSyncItem(RADIUS, new Index(100, 100), "SyncItem 1", false);
        syncItem2 = createSyncItem(RADIUS, new Index(100, 150), "SyncItem 2", true);
        syncItem3 = createSyncItem(RADIUS, new Index(150, 100), "SyncItem 3", false);
        syncItem4 = createSyncItem(RADIUS, new Index(150, 150), "SyncItem 4", false);
        //
        syncItem5 = createSyncItem(RADIUS, new Index(400, 100), "SyncItem 1", false);
        syncItem6 = createSyncItem(RADIUS, new Index(400, 150), "SyncItem 2", false);
        syncItem7 = createSyncItem(RADIUS, new Index(450, 100), "SyncItem 3", false);
        syncItem8 = createSyncItem(RADIUS, new Index(450, 150), "SyncItem 4", false);
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        getMovingModel().getCollisionService().findPath(syncItem1, new Index(400, 100));
        getMovingModel().getCollisionService().findPath(syncItem2, new Index(400, 150));
        getMovingModel().getCollisionService().findPath(syncItem3, new Index(450, 100));
        getMovingModel().getCollisionService().findPath(syncItem4, new Index(450, 150));
        //
        getMovingModel().getCollisionService().findPath(syncItem5, new Index(100, 100));
        getMovingModel().getCollisionService().findPath(syncItem6, new Index(100, 150));
        getMovingModel().getCollisionService().findPath(syncItem7, new Index(150, 100));
        getMovingModel().getCollisionService().findPath(syncItem8, new Index(150, 150));
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
