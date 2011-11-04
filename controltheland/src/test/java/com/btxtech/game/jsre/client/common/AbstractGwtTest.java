package com.btxtech.game.jsre.client.common;

import com.btxtech.game.jsre.client.ClientServices;
import com.btxtech.game.jsre.client.control.GameStartupSeq;
import com.btxtech.game.jsre.client.control.StartupProgressListener;
import com.btxtech.game.jsre.client.control.StartupSeq;
import com.btxtech.game.jsre.client.control.StartupTaskEnum;
import com.btxtech.game.jsre.client.control.task.AbstractStartupTask;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.SimpleBase;
import com.btxtech.game.jsre.common.StartupTaskInfo;
import com.google.gwt.junit.client.GWTTestCase;

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

    protected void configureMinimalGame(final Runnable runnable) {
        ClientServices.getInstance().getClientRunner().addStartupProgressListener(new StartupProgressListener() {
            @Override
            public void onStart(StartupSeq startupSeq) {
                System.out.println("onStart: " + startupSeq);
            }

            @Override
            public void onNextTask(StartupTaskEnum taskEnum) {
                System.out.println("onNextTask: " + taskEnum);
            }

            @Override
            public void onTaskFinished(AbstractStartupTask task) {
                System.out.println("onTaskFinished: " + task);
            }

            @Override
            public void onTaskFailed(AbstractStartupTask task, String error, Throwable t) {
                t.printStackTrace();
                System.out.println("onTaskFailed: " + error);
            }

            @Override
            public void onStartupFinished(List<StartupTaskInfo> taskInfo, long totalTime) {
                runnable.run();
            }

            @Override
            public void onStartupFailed(List<StartupTaskInfo> taskInfo, long totalTime) {
                System.out.println("onStartupFailed");
            }
        });
        ClientServices.getInstance().getClientRunner().start(GameStartupSeq.COLD_SIMULATED);
    }

}
