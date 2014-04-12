package scenario;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;

import java.util.List;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 12:46
 */
public abstract class Scenario {

    protected abstract void addItems(List<SyncItem> syncItems, CollisionService collisionService) throws NoBetterPathFoundException;

    public abstract void tick(List<SyncItem> collisionService);

    public abstract void stop();

    public /*abstract*/ void start(List<SyncItem> syncItems, CollisionService collisionService) throws NoBetterPathFoundException {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public void init(List<SyncItem> syncItems, CollisionService collisionService, Terrain terrain) {
        terrain.init(getTileXCount(), getTileYCount());
        setupTerrain(terrain);
        collisionService.init(terrain);
        try {
            addItems(syncItems, collisionService);
        } catch (NoBetterPathFoundException e) {
            e.printStackTrace();
        }
    }

    public int getTileXCount() {
        return 50;
    }

    public int getTileYCount() {
        return 50;
    }

    public void setupTerrain(Terrain upTerrain) {
    }

    protected SyncItem createSyncItem(CollisionService collisionService, List<SyncItem> syncItems, int radius, Index position, String debugName) {
        SyncItem syncItem = new SyncItem(radius, position, debugName);
        collisionService.addSyncItem(syncItem);
        syncItems.add(syncItem);
        return syncItem;
    }
}
