package scenario;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import model.MovingModel;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 12:46
 */
public abstract class Scenario {
    private MovingModel movingModel;

    abstract protected void addItems() throws NoBetterPathFoundException;

    public abstract void tick();

    public abstract void stop();

    public abstract void start() throws NoBetterPathFoundException;

    public void setupTerrain(Terrain upTerrain) {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public void init(MovingModel movingModel, Terrain terrain) {
        this.movingModel = movingModel;
        terrain.init(getTileXCount(), getTileYCount());
        setupTerrain(terrain);
        movingModel.getCollisionService().init(terrain);
        try {
            addItems();
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

    protected SyncItem createSyncItem(int radius, Index position, String debugName) {
        return movingModel.createSyncItem(radius, position, debugName);
    }

    protected void deleteSyncItem(SyncItem syncItem) {
        movingModel.deleteSyncItem(syncItem);
    }

    public MovingModel getMovingModel() {
        return movingModel;
    }
}
