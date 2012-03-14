package com.btxtech.game.jsre.client.simulation;

import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.common.CommonJava;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncBaseItem;
import org.junit.Ignore;

/**
 * User: beat
 * Date: 12.03.2012
 * Time: 16:21:56
 * <p/>
 * <p/>
 * -Dgwt.args="-runStyle Manual:1"
 * <p/>
 * _@DoNotRunWith(Platform.HtmlUnitUnknown)
 */
@Ignore
public class TestSimulation extends AbstractGwtTest {
    // old JUnit @Test

    // Mock the Movable directly and not via Servlet
    // Test never finished due to ClientLevelHandler Window.open()
    public void _testUnknown() throws Exception {
        startColdSimulated(new Runnable() {
            @Override
            public void run() {
                assertEquals(1, ItemContainer.getInstance().getItems().size());
                SyncBaseItem myJeep = CommonJava.getFirst(ItemContainer.getInstance().getItems()).getSyncBaseItem();
                ActionHandler.getInstance().move(myJeep, new Index(700, 700));
            }
        });
    }
}
