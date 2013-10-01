package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.page.DbPage;
import com.btxtech.game.wicket.pages.cms.content.plugin.login.LoggedinBox;
import com.btxtech.game.wicket.pages.cms.content.plugin.login.LoginBox;
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
        add(new LoggedinBox("loggedinBox"));
        add(new LoginBox("loginBox", true));
    }

    @Override
    public boolean isVisible() {
        return headerVisible;
    }
}
