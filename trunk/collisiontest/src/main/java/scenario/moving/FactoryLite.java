package scenario.moving;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import scenario.Scenario;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 12:46
 */
public class FactoryLite extends Scenario {
    public static final int TIMER_DELAY = 1000;
    private static final int MAX_UNITS = 2;
    private Timer timer;
    private int count;

    @Override
    public void addItems(final List<SyncItem> syncItems, CollisionService collisionService) {
   /*     stop();
        count = 0;
        timer = new Timer("ItemTimerTask", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (syncItems) {
                    if (count < MAX_UNITS) {
                        SyncItem syncItem = new SyncItem(10, new Index(100, 100), "undefined");
                        syncItem.setTargetPosition(new Index(200, 200));
                        syncItems.add(syncItem);
                        count++;
                    }
                }
            }
        }, 0, TIMER_DELAY);
         */
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
