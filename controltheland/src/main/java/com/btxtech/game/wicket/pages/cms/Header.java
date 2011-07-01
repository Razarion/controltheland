package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.DbPage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 01.07.2011
 * Time: 23:33:55
 */
public class Header extends Panel {
    private boolean headerVisible;

    public Header(String id, DbPage dbPage) {
        super(id);
        headerVisible = dbPage.isHeaderVisible();
    }

    @Override
    public boolean isVisible() {
        return headerVisible;
    }
}
