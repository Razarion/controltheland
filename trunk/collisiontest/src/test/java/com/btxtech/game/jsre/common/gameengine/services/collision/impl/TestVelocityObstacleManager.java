package com.btxtech.game.jsre.common.gameengine.services.collision.impl;

import com.btxtech.game.jsre.client.common.DecimalPosition;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.NoPreferredVelocityFoundException;
import com.btxtech.game.jsre.common.gameengine.services.collision.VelocityObstacleManager;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

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
        System.out.println("optimalVelocity: " + optimizedVelocity);
        new VelocityManagerVisualizer(velocityObstacleManager, optimizedVelocity);
//        assertDecimalPosition(new DecimalPosition(233.07530821713456, 101.79156113367335), optimizedVelocity);
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
