package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.client.ClientGlobalServices;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GameCommon;
import com.btxtech.game.jsre.client.action.ActionHandler;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupProgressListener;
import com.btxtech.game.jsre.client.control.StartupScreen;
import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.simulation.GwtTestRunnable;
import com.btxtech.game.jsre.client.terrain.MapWindow;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 15:27:27
 */
public abstract class AbstractGwtTest extends GWTTestCase implements StartupProgressListener {
    public final static int MY_BASE_ID = 1;
    public final static SimpleBase MY_BASE = new SimpleBase(MY_BASE_ID, 1);
    public final static int BOT_BASE_ID = 2;
    public final static SimpleBase BOT_BASE = new SimpleBase(BOT_BASE_ID, 1);
    public final static int ITEM_MOVABLE = 1;
    public final static int ITEM_CONTAINER = 2;
    public final static int ITEM_ATTACKER = 3;
    public final static int ITEM_DEFENSE_TOWER = 4;
    private GwtTestRunnable afterStartupRunnable;

    @Override
    public String getModuleName() {
        return "com.btxtech.game.jsre.Game";
    }

    // @Before  -> does not work

    // --------- Startup ---------

    protected void startColdSimulated(GwtTestRunnable runnable) {
        GameCommon.clearGame();
        ClientGlobalServices.getInstance().getClientRunner().cleanupBeforeTest();
        LogConfiguration logConfiguration = new LogConfiguration();
        logConfiguration.onModuleLoad();
        afterStartupRunnable = runnable;
        TerrainView.uglySuppressRadar = true;
        init(GameStartupSeq.COLD_SIMULATED, 1);
        ClientGlobalServices.getInstance().getClientRunner().addStartupProgressListener(this);
        Game game = new Game();
        MapWindow.getAbsolutePanel().setPixelSize(1920, 1024);
        game.onModuleLoad();
        delayTestFinish(20000);
    }

    protected void init(GameStartupSeq gameStartupSeq, Integer taskId) {
        setNativeCtlStartTime();
        setupStartupSeq(gameStartupSeq, taskId);
        setupStartScreen();
    }

    private void setupStartScreen() {
        AbsolutePanel div = new AbsolutePanel();
        div.getElement().setId("startScreen");
        Label progressText = new Label();
        progressText.getElement().setId(StartupScreen.PROGRESS_TEXT_ID);
        div.add(progressText);
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

    // ---------- Helpers ----------

    /*   public ClientSyncItemView getFirstClientSyncItemView(int itemTypeId) throws Exception {
      ItemType itemType = ItemContainer.getInstance().getItemType(itemTypeId);
      Collection<? extends SyncItem> items = ItemContainer.getInstance().getItems(itemType, null);
      if (items.isEmpty()) {
          throw new IllegalArgumentException("No such item of item type id available: " + itemTypeId);
      }
      SyncItem syncItem = CommonJava.getFirst(items);
      ClientSyncItem clientSyncItem = ItemContainer.getInstance().getClientSyncItem(syncItem);
      //ClientSyncItemView clientSyncItemView = clientSyncItem.getClientSyncItemView();
//       if (clientSyncItemView == null) {
          throw new IllegalArgumentException("Item not visible: " + syncItem);
      }
      return clientSyncItemView;
  }  */

    public Element getDebugElement(String debugId) {
        Element element = DOM.getElementById(UIObject.DEBUG_ID_PREFIX + debugId);
        if (element == null) {
            throw new IllegalArgumentException("DebugId can not be found via DOM.getElementById(): " + debugId);
        }
        return element;
    }

    public UIObject getDebugUIObject(String debugId) {
        Element element = getDebugElement(debugId);
        UIObject result = getDebugUIObject(RootPanel.get(), element);
        if (result == null) {
            throw new IllegalArgumentException("DebugId can not be found in any of the children of RootPanel: " + debugId);
        }
        return result;
    }

    public void assertDebugUIObjectNotAvailableOrInvisible(String debugId) {
        if (DOM.getElementById(UIObject.DEBUG_ID_PREFIX + debugId) == null) {
            return;
        }

        UIObject uiObject = getDebugUIObject(debugId);
        assertFalse("Is expected to be invisible: " + debugId, uiObject.isVisible());
    }

    private UIObject getDebugUIObject(UIObject uiObject, Element element) {
        if (uiObject.getElement().equals(element)) {
            return uiObject;
        }

        if (uiObject instanceof HasWidgets) {
            HasWidgets hasWidgets = (HasWidgets) uiObject;
            for (Widget child : hasWidgets) {
                UIObject result = getDebugUIObject(child, element);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public void assertCursor(String cursorName, UIObject uiObject) {
        assertEquals(cursorName, uiObject.getElement().getStyle().getCursor());
    }
    // ---------- Async methods ----------

    public void executeIfActionServiceIdle(final GwtTestRunnable runnable) {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if (ActionHandler.getInstance().isBusy()) {
                    return true;
                } else {
                    try {
                        runnable.run();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    return false;
                }
            }
        }, 500);
    }

    public void sleep(int sleepTime, final GwtTestRunnable runnable) {
        Timer timer = new Timer() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        timer.schedule(sleepTime);
    }

    // ---------- StartupProgressListener ----------

    @Override
    public void onStart(StartupSeq startupSeq) {
        // Ignore
    }

    @Override
    public void onNextTask(StartupTaskEnum taskEnum) {
        // Ignore
    }

    @Override
    public void onTaskFinished(AbstractStartupTask task) {
        // Ignore
    }

    @Override
    public void onTaskFailed(AbstractStartupTask task, String error, Throwable t) {
        // Ignore
    }

    @Override
    public void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime) {
        if (afterStartupRunnable != null) {
            try {
                afterStartupRunnable.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime) {
        // Ignore
    }

}

