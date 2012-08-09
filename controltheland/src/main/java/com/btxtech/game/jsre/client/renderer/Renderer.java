package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.google.gwt.animation.client.AnimationScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 27.07.12
 * Time: 22:51
 */
public class Renderer {
    private static final Renderer INSTANCE = new Renderer();
    private Logger log = Logger.getLogger(Renderer.class.getName());
    private int frameCount = 0;
    private long nextFrameCountCalculation = 0;
    private int renderTime = 0;
    private AnimationScheduler.AnimationCallback animationCallback;
    private List<AbstractRenderTask> renderTasks = new ArrayList<AbstractRenderTask>();
    private int itemRenderTaskIndex;

    // TODO Items Animation
    // TODO Buildup

    // TODO alles was animiert werden muss
    // TODO native implementation for all browser available AnimationSchedulerImpl: (IE & Opera)

    public static Renderer getInstance() {
        return INSTANCE;
    }

    private Renderer() {
        renderTasks.add(new TerrainRenderTask(TerrainView.getInstance().getTerrainHandler(), TerrainView.getInstance().getContext2d()));
        ItemRenderTask itemRenderTask = new ItemRenderTask(TerrainView.getInstance().getContext2d());
        renderTasks.add(itemRenderTask);
        itemRenderTaskIndex = renderTasks.indexOf(itemRenderTask);
        renderTasks.add(new MuzzleFlashRenderTask(TerrainView.getInstance().getContext2d()));
        renderTasks.add(new SelectionFrameRenderTask(TerrainView.getInstance().getContext2d()));
        renderTasks.add(new InventoryItemPlacerRenderTask(TerrainView.getInstance().getContext2d()));
        renderTasks.add(new ToBeBuildPlacerRenderTask(TerrainView.getInstance().getContext2d()));
        renderTasks.add(new ExplosionRenderTask(TerrainView.getInstance().getContext2d()));
    }

    public void start() {
        animationCallback = new AnimationScheduler.AnimationCallback() {
            @Override
            public void execute(double timestamp) {
                try {
                    Perfmon.getInstance().onEntered(PerfmonEnum.RENDERER);
                    // Statistics
                    long startTime = 0;
                    if (Game.isDebug()) {
                        startTime = System.currentTimeMillis();
                        if (nextFrameCountCalculation == 0) {
                            nextFrameCountCalculation = System.currentTimeMillis() + 1000;
                        } else if (nextFrameCountCalculation < System.currentTimeMillis()) {
                            nextFrameCountCalculation = System.currentTimeMillis() + 1000;
                            SideCockpit.getInstance().debugFrameRate(frameCount, renderTime / frameCount);
                            frameCount = 0;
                            renderTime = 0;
                        }
                        frameCount++;
                    }
                    // Main work
                    doRender((long) timestamp);
                    // Statistics
                    if (Game.isDebug()) {
                        renderTime += (int) (System.currentTimeMillis() - startTime);
                    }
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Renderer Timer", e);
                } finally {
                    Perfmon.getInstance().onLeft(PerfmonEnum.RENDERER);
                    if (animationCallback != null) {
                        AnimationScheduler.get().requestAnimationFrame(animationCallback, TerrainView.getInstance().getCanvas().getElement());
                    }
                }
            }
        };
        AnimationScheduler.get().requestAnimationFrame(animationCallback, TerrainView.getInstance().getCanvas().getElement());
    }

    public void stop() {
        animationCallback = null;
    }

    private void doRender(long timeStamp) {
        Rectangle viewRect = TerrainView.getInstance().getViewRect();
        Rectangle tileViewRect = TerrainView.getInstance().getTerrainHandler().convertToTilePositionRoundUp(viewRect);
        for (AbstractRenderTask renderTask : renderTasks) {
            try {
                renderTask.render(timeStamp, viewRect, tileViewRect);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Renderer.doRender()", e);
            }
        }
    }

    public void overrideItemRenderTask(AbstractRenderTask abstractRenderTask) {
        renderTasks.set(itemRenderTaskIndex, abstractRenderTask);
    }
}
