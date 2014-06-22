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
public class FollowPath extends Scenario {
    private SyncItem syncItem;

    @Override
    public void addItems() {
        syncItem = createSyncItem(10, new Index(100, 180), "Mover", false);
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        List<Index> wayPoints = new ArrayList<Index>();
        wayPoints.add(new Index(200, 500));
        wayPoints.add(new Index(500, 200));
        wayPoints.add(new Index(500, 500));
        syncItem.moveTo(wayPoints);
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
