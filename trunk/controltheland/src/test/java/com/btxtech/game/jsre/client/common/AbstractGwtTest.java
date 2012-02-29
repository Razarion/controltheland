package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupProgressListener;
import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import org.junit.Before;

import java.util.List;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 15:27:27
 */
public abstract class AbstractGwtTest extends GWTTestCase {
    public final static int MY_BASE_ID = 1;
    public final static SimpleBase MY_BASE = new SimpleBase(MY_BASE_ID);
    public final static int BOT_BASE_ID = 2;
    public final static SimpleBase BOT_BASE = new SimpleBase(BOT_BASE_ID);
    public final static int ITEM_MOVABLE = 1;
    public final static int ITEM_ATTACKER = 2;
    public final static int ITEM_DEFENSE_TOWER = 3;

    @Override
    public String getModuleName() {
        return "com.btxtech.game.jsre.Game";
    }

    // @Before  -> does not work
    public void init(GameStartupSeq gameStartupSeq, Integer taskId) {
        setNativeCtlStartTime();
        setupStartupSeq(gameStartupSeq, taskId);
        setupStartScreen();
    }

    private void setupStartScreen() {
        AbsolutePanel div = new AbsolutePanel();
        div.getElement().setId("startScreen");
        RootPanel.get().add(div);
    }

    private void setupStartupSeq(GameStartupSeq gameStartupSeq, Integer taskId) {
        AbsolutePanel div = new AbsolutePanel();
        div.getElement().setId(Game.STARTUP_SEQ_ID);
        div.getElement().setAttribute(Game.LEVEL_TASK_ID, taskId.toString());
        div.getElement().setAttribute(Game.STARTUP_SEQ_ID, gameStartupSeq.name());
        RootPanel.get().add(div);
    }

    private native double setNativeCtlStartTime() /*-{
      return $wnd.ctlStartTime = 0;
    }-*/;

}
