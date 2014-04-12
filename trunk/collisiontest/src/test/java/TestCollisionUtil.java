import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionUtil;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Collection;

/**
 * User: beat
 * Date: 06.01.14
 * Time: 22:37
 */
public class TestCollisionUtil {
    //@Test
    public void getCoveringTiles() {
        Collection<Index> indexes = CollisionUtil.getCoveringTilesAbsolute(new Index(100, 100), 15);
        assertCollection(indexes, new Index(9, 9), new Index(9, 10), new Index(10, 9), new Index(10, 10));
    }

    private void assertCollection(Collection<Index> actual, Index... expected) {
        Assert.assertEquals(expected.length, actual.size());
        Assert.assertTrue(actual.containsAll(Arrays.asList(expected)));
    }

}
