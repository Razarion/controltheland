package com.btxtech.game.jsre.client.renderer;

import com.btxtech.game.jsre.client.ClientExceptionHandler;
import com.btxtech.game.jsre.client.Game;
import com.btxtech.game.jsre.client.GwtCommon;
import com.btxtech.game.jsre.client.cockpit.SideCockpit;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.item.ItemContainer;
import com.btxtech.game.jsre.client.terrain.TerrainView;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainUtil;
import com.btxtech.game.jsre.common.gameengine.syncObjects.SyncItem;
import com.btxtech.game.jsre.common.perfmon.Perfmon;
import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.dom.client.CanvasElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: beat
 * Date: 27.07.12
 * Time: 22:51
 */
public class Renderer {
    private static final Renderer INSTANCE = new Renderer();
    private int frameCount = 0;
    private long nextFrameCountCalculation = 0;
    private int renderTime = 0;
    private AnimationScheduler.AnimationCallback gameAnimationCallback;
    private AnimationScheduler.AnimationCallback overlayAnimationCallback;
    private List<AbstractRenderTask> overlayRenderTasks = new ArrayList<AbstractRenderTask>();
    private List<AbstractRenderTask> gameRenderTasks = new ArrayList<AbstractRenderTask>();
    private int itemRenderTaskIndex;

    // TODO native implementation for all browser available AnimationSchedulerImpl: (IE & Opera)

    public static Renderer getInstance() {
        return INSTANCE;
    }

    private Renderer() {
        gameRenderTasks.add(new TerrainRenderTask(TerrainView.getInstance().getTerrainHandler(), TerrainView.getInstance().getContext2d()));
        if(Game.isDebug()) {
            gameRenderTasks.add(new DebugTerrainOverlayRenderTask(TerrainView.getInstance().getTerrainHandler(), TerrainView.getInstance().getContext2d()));
        }
        gameRenderTasks.add(new InGameTipBottomRenderTask(TerrainView.getInstance().getContext2d()));
        ItemRenderTask itemRenderTask = new ItemRenderTask(TerrainView.getInstance().getContext2d());
        gameRenderTasks.add(itemRenderTask);
        itemRenderTaskIndex = gameRenderTasks.indexOf(itemRenderTask);
        gameRenderTasks.add(new ItemClipRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new AttackHandlerRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new ExplosionRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new SelectionFrameRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new ItemSelectionAndHoverRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new InventoryItemPlacerRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new InGameTipTopRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new InGameQuestVisualisationRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new StartPointItemPlacerRenderTask(TerrainView.getInstance().getContext2d()));
        gameRenderTasks.add(new SplashRenderTask(TerrainView.getInstance().getContext2d()));
    }

    public void start() {
        gameAnimationCallback = new AnimationScheduler.AnimationCallback() {
            @Override
            public void execute(double timestamp) {
                // timestamp can not be converted to a long in google chrome
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
                    doRender(System.currentTimeMillis());
                    // Statistics
                    if (Game.isDebug()) {
                        renderTime += (int) (System.currentTimeMillis() - startTime);
                    }
                } catch (Exception e) {
                    ClientExceptionHandler.handleExceptionOnlyOnce("Renderer Callback", e);
                } finally {
                    Perfmon.getInstance().onLeft(PerfmonEnum.RENDERER);
                    if (gameAnimationCallback != null) {
                        AnimationScheduler.get().requestAnimationFrame(gameAnimationCallback, TerrainView.getInstance().getCanvas().getElement());
                        if (overlayAnimationCallback != null) {
                            AnimationScheduler.get().requestAnimationFrame(overlayAnimationCallback);
                        }
                    }
                }
            }
        };
        AnimationScheduler.get().requestAnimationFrame(gameAnimationCallback, TerrainView.getInstance().getCanvas().getElement());
    }

    public void stop() {
        gameAnimationCallback = null;
    }

    public void startOverlayRenderTask(AbstractRenderTask abstractRenderTask) {
        if (overlayRenderTasks.contains(abstractRenderTask)) {
            return;
        }
        overlayRenderTasks.add(abstractRenderTask);
        if (overlayRenderTasks.size() == 1) {
            overlayAnimationCallback = new AnimationScheduler.AnimationCallback() {
                @Override
                public void execute(double timestamp) {
                    // timestamp can not be converted to a long in google chrome
                    try {
                        Perfmon.getInstance().onEntered(PerfmonEnum.RENDERER_OVERLAY);
                        doOverlayRender(System.currentTimeMillis());
                    } catch (Exception e) {
                        ClientExceptionHandler.handleException("Overlay Renderer Callback", e);
                    } finally {
                        Perfmon.getInstance().onLeft(PerfmonEnum.RENDERER_OVERLAY);
                    }
                }
            };
        }
    }

    public void stopOverlayRenderTask(AbstractRenderTask abstractRenderTask) {
        overlayRenderTasks.remove(abstractRenderTask);
        if (overlayRenderTasks.isEmpty()) {
            overlayAnimationCallback = null;
        }
    }

    private void doRender(long timeStamp) {
        Rectangle viewRect = TerrainView.getInstance().getViewRect();
        Rectangle tileViewRect = TerrainUtil.convertToTilePositionRoundUp(viewRect);
        CanvasElement canvasElement = TerrainView.getInstance().getContext2d().getCanvas();
        // Set canvas size due to chrome crash
        canvasElement.setWidth(viewRect.getWidth());
        canvasElement.setHeight(viewRect.getHeight());
        Collection<SyncItem> itemsInView = ItemContainer.getInstance().getItemsInRectangleFastIncludingDead(viewRect); // TODO clips off items if the middle is no longer in the view rect
        for (AbstractRenderTask renderTask : gameRenderTasks) {
            try {
                renderTask.render(timeStamp, itemsInView, viewRect, tileViewRect);
            } catch (Exception e) {
                ClientExceptionHandler.handleExceptionOnlyOnce("Renderer.doRender()", e);
            }
        }
    }

    private void doOverlayRender(long timeStamp) {
        Rectangle viewRect = TerrainView.getInstance().getViewRect();
        Rectangle tileViewRect = TerrainUtil.convertToTilePositionRoundUp(viewRect);
        for (AbstractRenderTask renderTask : overlayRenderTasks) {
            try {
                renderTask.render(timeStamp, null, viewRect, tileViewRect);
            } catch (Exception e) {
                ClientExceptionHandler.handleExceptionOnlyOnce("Renderer.doOverlayRender()", e);
            }
        }
    }

    public void overrideItemRenderTask(AbstractRenderTask abstractRenderTask) {
        gameRenderTasks.set(itemRenderTaskIndex, abstractRenderTask);
    }
}
