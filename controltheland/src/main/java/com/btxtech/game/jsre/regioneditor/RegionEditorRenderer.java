package com.btxtech.game.jsre.regioneditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 15:36
 */
public class RegionEditorRenderer {
    private Logger log = Logger.getLogger(RegionEditorRenderer.class.getName());
    private AnimationScheduler.AnimationCallback animationCallback;
    private List<AbstractRegionEditorRenderTask> renderTaskAbstract = new ArrayList<AbstractRegionEditorRenderTask>();
    private Canvas canvas;
    private RegionEditorModel regionEditorModel;

    public RegionEditorRenderer(RegionEditorModel regionEditorModel) {
        this.regionEditorModel = regionEditorModel;
        renderTaskAbstract.add(new RegionTileRenderTask(regionEditorModel));
        renderTaskAbstract.add(new GridRenderTask(regionEditorModel));
        renderTaskAbstract.add(new MouseRenderTask(regionEditorModel));
    }

    public void start(final Canvas canvas) {
        this.canvas = canvas;
        animationCallback = new AnimationScheduler.AnimationCallback() {
            @Override
            public void execute(double timestamp) {
                // timestamp can not be converted to a long in google chrome
                try {
                    doRender(System.currentTimeMillis());
                } catch (Exception e) {
                    log.log(Level.SEVERE, "RegionEditorRenderer Callback", e);
                }
                AnimationScheduler.get().requestAnimationFrame(animationCallback, canvas.getElement());
            }
        };
        AnimationScheduler.get().requestAnimationFrame(animationCallback, canvas.getElement());
    }

    public void stop() {
        animationCallback = null;
    }

    private void doRender(long timeStamp) {
        Rectangle displayRectangle = regionEditorModel.getDisplayRectangle();
        double scale = regionEditorModel.getScale();
        Index viewOriginTerrain = regionEditorModel.getViewOriginTerrain();
        Context2d context2d = canvas.getContext2d();
        context2d.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
        for (AbstractRegionEditorRenderTask renderTaskAbstract : this.renderTaskAbstract) {
            try {
                renderTaskAbstract.render(context2d, timeStamp, scale, displayRectangle, viewOriginTerrain);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Renderer.doRender()", e);
            }
        }
    }
}
