package scenario.moving;

import com.btxtech.game.jsre.client.common.DecimalPosition;
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
public class Frontal3 extends Scenario {
    public static final int RADIUS = 10;
    private List<SyncItem> left2Right = new ArrayList<>();
    private List<SyncItem> right2Left = new ArrayList<>();


    @Override
    public void addItems() {
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                left2Right.add(createSyncItem(RADIUS, new DecimalPosition(100 + x * 2.5 * RADIUS, 100 + y * 2.5 * RADIUS).getPosition(), "SyncItem 1", false));
            }
        }

  /*      for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                right2Left.add(createSyncItem(RADIUS, new DecimalPosition(400 + x * 2.5 * RADIUS, 100 + y * 2.5 * RADIUS).getPosition(), "SyncItem 1", false));
            }
        }*/
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        for (SyncItem syncItem : left2Right) {
            getMovingModel().getCollisionService().findPath(syncItem, syncItem.getDecimalPosition().add(400, 0).getPosition());
        }
        for (SyncItem syncItem : right2Left) {
            getMovingModel().getCollisionService().findPath(syncItem, syncItem.getDecimalPosition().sub(400, 0).getPosition());
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }
}
