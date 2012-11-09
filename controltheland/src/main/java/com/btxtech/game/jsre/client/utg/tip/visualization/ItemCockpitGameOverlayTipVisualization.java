package com.btxtech.game.jsre.client.utg.tip.visualization;

import com.btxtech.game.jsre.client.cockpit.item.ItemCockpit;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.client.common.Rectangle;
import com.btxtech.game.jsre.client.renderer.CanvasElementLibrary;
import com.btxtech.game.jsre.client.utg.tip.OverlayTipPanel;
import com.btxtech.game.jsre.common.MathHelper;

/**
 * User: beat
 * Date: 22.08.12
 * Time: 13:54
 */
public class ItemCockpitGameOverlayTipVisualization implements GameTipVisualization {
    private int toBeBuiltTypeId;
    private Index absoluteBuildupPositionHotSpot;
    private Index buildupPositionHotSpot;
    private Index mousePosition;

    public ItemCockpitGameOverlayTipVisualization(int toBeBuiltTypeId) {
        this.toBeBuiltTypeId = toBeBuiltTypeId;
        absoluteBuildupPositionHotSpot = ItemCockpit.getInstance().getAbsoluteMiddleTopPositionFromBuildupPanel(toBeBuiltTypeId);
        buildupPositionHotSpot = new Index(CanvasElementLibrary.ARROW_WIDTH_TOTAL / 2, OverlayTipPanel.HEIGHT);
        mousePosition = new Index(buildupPositionHotSpot.getX() + CanvasElementLibrary.ARROW_LINE_WIDTH,0);
    }

    @Override
    public Index getArrowHotSpot(Rectangle viewRect, long timeStamp) {
        return buildupPositionHotSpot;
    }

    @Override
    public Index getMousePosition(Rectangle viewRect, long timeStamp) {
        return mousePosition;
    }

    @Override
    public double getArrowAngel() {
        return MathHelper.HALF_RADIANT;
    }

    public int getToBeBuiltTypeId() {
        return toBeBuiltTypeId;
    }

    public Index getAbsoluteBuildupPositionHotSpot() {
        return absoluteBuildupPositionHotSpot;
    }
}
