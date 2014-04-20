package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.BlockingStateException;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import scenario.Scenario;

import java.util.Timer;
import java.util.TimerTask;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 12:46
 */
public class Factory extends Scenario {
    public static final int TIMER_DELAY = 2000;

    private Timer timer;

    @Override
    public void addItems() {
        stop();
    }

    @Override
    public void start() throws NoBetterPathFoundException {
        timer = new Timer("ItemTimerTask", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SyncItem syncItem = createSyncItem(10, new Index(100, 100), "undefined");
                    getMovingModel().getCollisionService().findPath(syncItem, new Index(400, 400));
                } catch (BlockingStateException e) {
                    // Ignore
                } catch (Exception e) {
                    e.printStackTrace();
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
    public void tick() {
    }
}
