package com.btxtech.game.jsre.mapeditor.render;

import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.mapeditor.MapEditorModel;
import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: beat
 * Date: 16.09.12
 * Time: 12:21
 */
public class MapEditorRenderer {
    private Logger log = Logger.getLogger(MapEditorRenderer.class.getName());
    private AnimationScheduler.AnimationCallback animationCallback;
    private List<AbstractMapEditorRenderTask> renderTaskAbstract = new ArrayList<AbstractMapEditorRenderTask>();
    private Canvas canvas;
    private MapEditorModel mapEditorModel;

    public MapEditorRenderer() {
        renderTaskAbstract.add(new TerrainEditorRenderTask());
        renderTaskAbstract.add(new MouseOverRenderTask());
        renderTaskAbstract.add(new SurfaceModifyRenderTask());
        renderTaskAbstract.add(new TerrainImageModifyRenderTask());
        renderTaskAbstract.add(new SelectionAreaRenderTask());
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setMapEditorModel(MapEditorModel mapEditorModel) {
        this.mapEditorModel = mapEditorModel;
    }

    public void start() {
        animationCallback = new AnimationScheduler.AnimationCallback() {
            @Override
            public void execute(double timestamp) {
                // timestamp can not be converted to a long in google chrome
                try {
                    doRender(System.currentTimeMillis());
                } catch (Exception e) {
                    log.log(Level.SEVERE, "MapEditorRenderer Callback", e);
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
        Context2d context2d = canvas.getContext2d();
        Rectangle viewRectangle = mapEditorModel.getViewRectangle();
        for (AbstractMapEditorRenderTask renderTaskAbstract : this.renderTaskAbstract) {
            try {
                renderTaskAbstract.setViewRectangle(viewRectangle);
                renderTaskAbstract.render(timeStamp, context2d, mapEditorModel);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Renderer.doRender()", e);
            }
        }
    }
}
