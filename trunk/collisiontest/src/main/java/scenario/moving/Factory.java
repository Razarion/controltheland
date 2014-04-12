package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.Path;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import scenario.Scenario;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 12:46
 */
public class Factory extends Scenario {
    public static final int TIMER_DELAY = 1000;

    private Timer timer;

    @Override
    public void addItems(final List<SyncItem> syncItems, final CollisionService collisionService) {
        stop();
    }

    @Override
    public void start(final List<SyncItem> syncItems, final CollisionService collisionService) throws NoBetterPathFoundException {
        timer = new Timer("ItemTimerTask", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (syncItems) {
                    try {
                        SyncItem syncItem = createSyncItem(collisionService, syncItems, 10, new Index(100, 100), "undefined");
                        collisionService.findPath(syncItem, new Index(400, 400));
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, TIMER_DELAY);

    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void tick(List<SyncItem> syncItemLists) {
    }
}
