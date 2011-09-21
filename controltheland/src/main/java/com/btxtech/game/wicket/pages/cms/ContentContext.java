package com.btxtech.game.wicket.pages.cms;

import org.apache.wicket.PageParameters;

import java.io.Serializable;

/**
 * User: beat
 * Date: 21.09.2011
 * Time: 11:45:29
 */
public class ContentContext implements Serializable {
    private PageParameters pageParameters;

    public ContentContext(PageParameters pageParameters) {
        this.pageParameters = pageParameters;
    }

    public PageParameters getPageParameters() {
        return pageParameters;
    }
}
