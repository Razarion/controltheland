package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.common.gameengine.services.collision.NoPreferredVelocityFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.VelocityObstacleManager;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by beat
 * on 18.07.2014.
 */
public class TestVelocityObstacleManager {

    @Test
    public void test() throws NoPreferredVelocityFoundException {
        SyncItem protagonistMock = createMockSyncItem(10, new DecimalPosition(233.07530821713456, 101.79156113367335), new DecimalPosition(0.43428581883054196, 0.24778181443093375), new DecimalPosition(400, 101));
        SyncItem otherMock = createMockSyncItem(10, new DecimalPosition(267.92361864951323, 98.64374839477341), new DecimalPosition(-0.4343489129008951, -0.24767119707751778), null);

        VelocityObstacleManager velocityObstacleManager = new VelocityObstacleManager(protagonistMock);
        velocityObstacleManager.inspect(otherMock);
        DecimalPosition optimizedVelocity = velocityObstacleManager.getOptimalVelocity();
        // new VelocityManagerVisualizer(velocityObstacleManager, optimizedVelocity);
        assertDecimalPosition(new DecimalPosition(0.43428, 0.2477818), optimizedVelocity);
    }

    @Test
    public void justStarted() throws NoPreferredVelocityFoundException {
        SyncItem protagonistMock = createMockSyncItem(10, new DecimalPosition(100.0, 150.0), new DecimalPosition(0.0, 0.0), new DecimalPosition(500.0, 150.0));
        Collection<SyncItem> others = new ArrayList<>();
        others.add(createMockSyncItem(10, new DecimalPosition(100.0, 100.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(100.0, 125.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(100.0, 175.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(100.0, 200.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(125.0, 100.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(125.0, 125.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(125.0, 150.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(125.0, 175.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(125.0, 200.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(150.0, 100.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(150.0, 125.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(150.0, 150.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(150.0, 175.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(150.0, 200.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(175.0, 100.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(175.0, 125.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(175.0, 150.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(175.0, 175.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(175.0, 200.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(200.0, 100.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(200.0, 125.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(200.0, 150.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(200.0, 175.0), new DecimalPosition(0.0, 0.0), null));
        others.add(createMockSyncItem(10, new DecimalPosition(200.0, 200.0), new DecimalPosition(0.0, 0.0), null));
        VelocityObstacleManager velocityObstacleManager = createVelocityObstacleManager(protagonistMock, others);
        DecimalPosition optimizedVelocity = velocityObstacleManager.getOptimalVelocity();
        // VelocityManagerVisualizer.startAndWaitForClose(velocityObstacleManager, optimizedVelocity);
        assertDecimalPosition(new DecimalPosition(-0.499565, -0.0208333), optimizedVelocity);
    }

    @Test
    public void frontalNoCollision() throws NoPreferredVelocityFoundException {
        SyncItem protagonistMock = createMockSyncItem(10, new DecimalPosition(100.0, 100.0), new DecimalPosition(0.5, 0.0), new DecimalPosition(400.0, 100.0));
        Collection<SyncItem> others = new ArrayList<>();
        others.add(createMockSyncItem(10, new DecimalPosition(221.0, 100.0), new DecimalPosition(-0.5, 0.0), null));
        VelocityObstacleManager velocityObstacleManager = createVelocityObstacleManager(protagonistMock, others);
        Assert.assertTrue(velocityObstacleManager.getOrcaLines().isEmpty());
        DecimalPosition optimizedVelocity = velocityObstacleManager.getOptimalVelocity();
        assertDecimalPosition(new DecimalPosition(0.5, 0.0), optimizedVelocity);
    }

    @Test
    public void frontal1() throws NoPreferredVelocityFoundException {
        SyncItem protagonistMock = createMockSyncItem(10, new DecimalPosition(100.0, 100.0), new DecimalPosition(0.5, 0.0), new DecimalPosition(400.0, 100.0));
        Collection<SyncItem> others = new ArrayList<>();
        others.add(createMockSyncItem(10, new DecimalPosition(220.0, 100.0), new DecimalPosition(-0.5, 0.0), null));
        VelocityObstacleManager velocityObstacleManager = createVelocityObstacleManager(protagonistMock, others);
        DecimalPosition optimizedVelocity = velocityObstacleManager.getOptimalVelocity();
        VelocityManagerVisualizer.startAndWaitForClose(velocityObstacleManager, optimizedVelocity);
        // TODO assertDecimalPosition(new DecimalPosition(xxx, yyy), optimizedVelocity);
    }

    @Test
    public void frontal2() throws NoPreferredVelocityFoundException {
        SyncItem protagonistMock = createMockSyncItem(10, new DecimalPosition(400.0, 100.0), new DecimalPosition(-0.5, 0.0), new DecimalPosition(100.0, 100.0));
        Collection<SyncItem> others = new ArrayList<>();
        others.add(createMockSyncItem(10, new DecimalPosition(280.0, 100.0), new DecimalPosition(0.5, 0.0), null));
        VelocityObstacleManager velocityObstacleManager = createVelocityObstacleManager(protagonistMock, others);
        DecimalPosition optimizedVelocity = velocityObstacleManager.getOptimalVelocity();
        VelocityManagerVisualizer.startAndWaitForClose(velocityObstacleManager, optimizedVelocity);
        // TODO assertDecimalPosition(new DecimalPosition(xxx, yyy), optimizedVelocity);
    }

    private VelocityObstacleManager createVelocityObstacleManager(SyncItem protagonist, Collection<SyncItem> others) {
        VelocityObstacleManager velocityObstacleManager = new VelocityObstacleManager(protagonist);
        for (SyncItem other : others) {
            velocityObstacleManager.inspect(other);
        }
        return velocityObstacleManager;
    }

    private void assertDecimalPosition(DecimalPosition expected, DecimalPosition actual) {
        Assert.assertEquals("DecimalPosition.x", expected.getX(), actual.getX(), 0.0001);
        Assert.assertEquals("DecimalPosition.y", expected.getY(), actual.getY(), 0.0001);
    }

    private SyncItem createMockSyncItem(int radius, DecimalPosition decimalPosition, DecimalPosition velocity, DecimalPosition target) {
        SyncItem syncItemMock = EasyMock.createMock(SyncItem.class);
        EasyMock.expect(syncItemMock.getRadius()).andReturn(radius).anyTimes();
        EasyMock.expect(syncItemMock.getDecimalPosition()).andReturn(decimalPosition).anyTimes();
        EasyMock.expect(syncItemMock.getVelocity()).andReturn(velocity).anyTimes();
        EasyMock.expect(syncItemMock.getTargetPosition()).andReturn(target).anyTimes();
        EasyMock.replay(syncItemMock);
        return syncItemMock;
    }
}
