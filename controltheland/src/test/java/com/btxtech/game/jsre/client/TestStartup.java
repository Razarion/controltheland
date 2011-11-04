package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 12:46:34
 */
@Ignore
public class TestStartup extends AbstractGwtTest {

    @Test
    public void testActionHandler() throws Exception {
        Logger.getLogger("").severe("--------------------- If this is mission, logging will not work ---------------------");
        configureMinimalGame(new Runnable() {
            @Override
            public void run() {
                finishTest();
            }
        });

        delayTestFinish(20000);

    }

}
