package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import scenario.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 13:25
 */
public class MoveToPosition extends Scenario {
    private static final int RADIUS = 10;
    private List<SyncItem> mySyncItems = new ArrayList<SyncItem>();

    @Override
    public void addItems() {
        for (int y = 100; y < 500; y += 50) {
            for (int x = 100; x < 500; x += 50) {
                mySyncItems.add(createSyncItem(RADIUS, new Index(x, y), "undefined"));
            }
        }
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        for (SyncItem syncItem : mySyncItems) {
            getMovingModel().getCollisionService().findPath(syncItem, new Index(700, 700));
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
