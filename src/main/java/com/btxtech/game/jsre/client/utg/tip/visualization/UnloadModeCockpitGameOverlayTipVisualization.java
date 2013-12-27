package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.renderer.CanvasElementLibrary;
import com.btxtech.game.jsre.client.utg.tip.OverlayTipPanel;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:54
 */
public class UnloadModeCockpitGameOverlayTipVisualization extends OverlayTipVisualization {
    private Index absoluteUnloadButtonHotSpot;
    private Index unloadButtonHotSpot;

    public UnloadModeCockpitGameOverlayTipVisualization() {
        absoluteUnloadButtonHotSpot = ItemCockpit.getInstance().getAbsoluteUnloadButtonTopPositionFromSpecialFunctionPanel();
        unloadButtonHotSpot = new Index(CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, OverlayTipPanel.HEIGHT);
    }

    @Override
    public Index getArrowHotSpot(Rectangle viewRect, long timeStamp) {
        return unloadButtonHotSpot;
    }

    @Override
    public Index getAbsoluteArrowHotSpot() {
        return absoluteUnloadButtonHotSpot;
    }
}
