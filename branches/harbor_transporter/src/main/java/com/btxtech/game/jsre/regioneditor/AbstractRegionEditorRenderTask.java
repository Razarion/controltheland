package com.btxtech.game.jsre.regioneditor;

import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * User: beat
 * Date: 09.09.12
 * Time: 15:47
 */
public abstract class AbstractRegionEditorRenderTask {
    private RegionEditorModel regionEditorModel;

    public AbstractRegionEditorRenderTask(RegionEditorModel regionEditorModel) {
        this.regionEditorModel = regionEditorModel;
    }

    public abstract void render(Context2d context2d, long timeStamp, double scale, Rectangle displayRectangle, Index viewOriginTerrain);

    public RegionEditorModel getRegionEditorModel() {
        return regionEditorModel;
    }
}
