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
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.List;

/**
 * User: beat
 * Date: 04.11.2011
 * Time: 15:27:27
 */
public abstract class AbstractGwtTest extends GWTTestCase implements StartupProgressListener {
    public final static int MY_BASE_ID = 1;
    public final static SimpleBase MY_BASE = new SimpleBase(MY_BASE_ID);
    public final static int BOT_BASE_ID = 2;
    public final static SimpleBase BOT_BASE = new SimpleBase(BOT_BASE_ID);
    public final static int ITEM_MOVABLE = 1;
    public final static int ITEM_ATTACKER = 2;
    public final static int ITEM_DEFENSE_TOWER = 3;
    private Runnable afterStartupRunnable;

    @Override
    public String getModuleName() {
        return "com.btxtech.game.jsre.Game";
    }

    // @Before  -> does not work

    protected void startColdSimulated(Runnable runnable) {
        afterStartupRunnable = runnable;
        TerrainView.uglySuppressRadar = true;
        init(GameStartupSeq.COLD_SIMULATED, 1);
        ClientServices.getInstance().getClientRunner().addStartupProgressListener(this);
        Game game = new Game();
        game.onModuleLoad();
        delayTestFinish(10000);
    }

    protected void init(GameStartupSeq gameStartupSeq, Integer taskId) {
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

    // ---------- Helpers ----------

//    protected void setupTerrain() {
//        TerrainView.uglySuppressRadar = true;
//        TerrainView.getInstance().addToParent(new AbsolutePanel());
//        Collection<TerrainImage> terrainImages = new ArrayList<TerrainImage>();
//        Collection<TerrainImagePosition> terrainImagePositions = new ArrayList<TerrainImagePosition>();
//        Collection<SurfaceImage> surfaceImages = new ArrayList<SurfaceImage>();
//        surfaceImages.add(new SurfaceImage(SurfaceType.LAND, 1, "test-bg-color"));
//        Collection<SurfaceRect> surfaceRects = new ArrayList<SurfaceRect>();
//        surfaceRects.add(new SurfaceRect(new Rectangle(0, 0, 100, 100), 1));
//        TerrainView.getInstance().setupTerrain(new TerrainSettings(100, 100, 100, 100), terrainImagePositions, surfaceRects, surfaceImages, terrainImages);
//        ClientCollisionService.getInstance().setup();
//    }

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
        if (afterStartupRunnable != null)
            try {
                afterStartupRunnable.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
    }

    @Override
    public void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime) {
        // Ignore
    }

}

