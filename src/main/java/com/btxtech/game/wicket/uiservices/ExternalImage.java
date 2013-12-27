package com.btxtech.game.wicket.uiservices;

import com.googlecode.charts4j.GChart;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 17:47
 */
public class ExternalImage extends MarkupContainer {
    private String url;

    public ExternalImage(String id, String url) {
        super(id);
        this.url = url;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", url);
    }

    public String getUrl() {
        return url;
    }
}
