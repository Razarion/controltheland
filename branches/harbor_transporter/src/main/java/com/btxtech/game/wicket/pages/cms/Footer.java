package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.page.DbPage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 01.07.2011
 * Time: 23:33:55
 */
public class Footer extends Panel {
    private boolean footerVisible;

    public Footer(String id, DbPage dbPage) {
        super(id);
        footerVisible = dbPage.isFooterVisible();
    }

    @Override
    public boolean isVisible() {
        return footerVisible;
    }
}
