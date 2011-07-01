package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.DbPage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 01.07.2011
 * Time: 23:33:55
 */
public class Ads extends Panel {
    private boolean adsVisible;

    public Ads(String id, DbPage dbPage) {
        super(id);
        adsVisible = dbPage.isAdsVisible();
    }

    @Override
    public boolean isVisible() {
        return adsVisible;
    }
}
