package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.layout.DbContentPageLink;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 00:17:38
 */
public class ContentPageLink extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentPageLink(String id, DbContentPageLink dbContentPageLink) {
        super(id);
        contentId = dbContentPageLink.getId();
        PageParameters pageParameters = new PageParameters();
        pageParameters.add(CmsUtil.ID, Integer.toString(dbContentPageLink.getDbPage().getId()));
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<>("link", CmsPage.class, pageParameters);
        if (dbContentPageLink.getDbCmsImage() != null) {
            link.add(new Label("label", "").setVisible(false));
            link.add(CmsImageResource.createImage("image", dbContentPageLink.getDbCmsImage()));
        } else {
            link.add(new Label("label", dbContentPageLink.getDbI18nName().getString(getLocale())));
            link.add(new Image("image", "").setVisible(false));
        }
        add(link);
        if (dbContentPageLink.getCssClass() != null) {
            link.add(new AttributeModifier("class", dbContentPageLink.getCssClass()));
        }
    }


    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
