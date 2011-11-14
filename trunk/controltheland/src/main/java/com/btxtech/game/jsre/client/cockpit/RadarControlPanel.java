package com.btxtech.game.jsre.client.cockpit;

import com.btxtech.game.jsre.client.cockpit.radar.RadarPanel;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: beat
 * Date: 07.11.2011
 * Time: 20:11:19
 */
public class RadarControlPanel extends AbstractControlPanel {
    public RadarControlPanel(int width, int height) {
        super(width, height);
    }

    @Override
    protected Widget createBody() {
        AbsolutePanel absolutePanel = RadarPanel.getInstance().createWidget(getContentWidth(), getContentHeight());
        absolutePanel.setTitle(ToolTips.TOOL_TIP_RADAR);
        return absolutePanel;
    }
}
