package com.btxtech.game.jsre.client;

import com.btxtech.game.jsre.client.common.AbstractGwtTest;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import org.junit.Ignore;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 12:46:34
 *
 * -Dgwt.args="-runStyle Manual:1"
 *
 */
//@DoNotRunWith(Platform.HtmlUnitUnknown)
@Ignore
public class TestStartup extends AbstractGwtTest {
    @Test
    public void testActionHandler() throws Exception {
        init(GameStartupSeq.COLD_REAL, 1);
        Game game = new Game();
        game.onModuleLoad();
        delayTestFinish(20000);
    }

}
