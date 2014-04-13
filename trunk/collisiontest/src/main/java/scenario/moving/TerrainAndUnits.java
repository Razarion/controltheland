package scenario.moving;

import com.btxtech.game.jsre.common.gameengine.services.collision.impl.NoBetterPathFoundException;
import com.btxtech.game.jsre.common.gameengine.services.terrain.SurfaceType;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainTile;
import scenario.Scenario;

/**
 * User: beat
 * Date: 22.03.13
 * Time: 13:25
 */
public class TerrainAndUnits extends Scenario {
    @Override
    public void addItems() throws NoBetterPathFoundException {
 /*       syncItems.add(new SyncItem(50, new Index(150, 250), "undefined"));
        SyncItem syncItem = new SyncItem(10, new Index(50, 50), "undefined");
        // TODO move to start syncItem.setPathToDestination(collisionService.findPath(syncItem, new Index(600, 200)), null);
        syncItems.add(syncItem);   */
    }

    @Override
    public void start() throws NoBetterPathFoundException {
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
    }

    @Override
    public void setupTerrain(Terrain terrain) {
        terrain.overrideTerrainTile(1, 1, new TerrainTile(SurfaceType.BLOCKED));
        terrain.overrideTerrainTile(1, 0, new TerrainTile(SurfaceType.BLOCKED));
        terrain.overrideTerrainTile(3, 0, new TerrainTile(SurfaceType.BLOCKED));
        terrain.overrideTerrainTile(3, 1, new TerrainTile(SurfaceType.BLOCKED));
        terrain.overrideTerrainTile(3, 2, new TerrainTile(SurfaceType.BLOCKED));
        terrain.overrideTerrainTile(2, 3, new TerrainTile(SurfaceType.BLOCKED));
        terrain.overrideTerrainTile(3, 3, new TerrainTile(SurfaceType.BLOCKED));
    }
}
