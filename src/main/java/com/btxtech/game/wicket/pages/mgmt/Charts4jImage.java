package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.wicket.uiservices.ExternalImage;
import com.googlecode.charts4j.GChart;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 17:47
 */
public class Charts4jImage extends ExternalImage {
    public Charts4jImage(GChart chart) {
        super("image", chart.toURLString());
    }
}
