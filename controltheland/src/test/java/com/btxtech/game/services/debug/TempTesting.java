package com.btxtech.game.services.debug;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.itemType.BoundingBox;
import com.btxtech.game.services.AbstractServiceTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;

/**
 * User: beat
 * Date: 16.10.2011
 * Time: 22:53:22
 */
@Ignore
public class TempTesting extends AbstractServiceTest {
    @Autowired
    private DebugService debugService;

    @Test
    public void run() throws Exception {
        configureMinimalGame();

        debugService.drawSyncItemArea(new BoundingBox(180, 130, 182, 120, 1).createSyntheticSyncItemArea(new Index(2820, 2626)), Color.DARK_GRAY);
        debugService.drawSyncItemArea(new BoundingBox(80, 80, 54, 60, 24).createSyntheticSyncItemArea(new Index(2940, 2609), 0.2053953891897674), Color.RED);

        debugService.waitForClose();
    }
}
