package com.btxtech.game.wicket.pages.mgmt;

import com.googlecode.charts4j.GChart;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;

/**
* User: beat
* Date: 26.07.12
* Time: 17:47
*/
public class Charts4jImage extends MarkupContainer {
    private String chartString;

    public Charts4jImage(GChart chart) {
        super("image");
        chartString = chart.toURLString();
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", chartString);
    }

}
