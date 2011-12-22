package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.07.2011
 * Time: 23:33:55
 */
public class Ads extends Panel {
    @SpringBean
    private CmsService cmsService;

    private boolean adsVisible;

    public Ads(String id, DbPage dbPage) {
        super(id);
        adsVisible = dbPage.isAdsVisible();
        add(new Label("label", cmsService.getAdsCode()).setEscapeModelStrings(false));
    }

    @Override
    public boolean isVisible() {
        return adsVisible;
    }
}
