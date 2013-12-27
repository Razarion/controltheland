package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentDetailLink;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 00:17:38
 */
public class ContentDetailLink extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentDetailLink(String id, DbContentDetailLink dbContentDetailLink, BeanIdPathElement beanIdPathElement) {
        super(id);
        contentId = dbContentDetailLink.getId();
        PageParameters pageParameters = cmsUiService.createPageParametersFromBeanId(beanIdPathElement);
        pageParameters.set(CmsPage.DETAIL_CONTENT_ID, Integer.toString(dbContentDetailLink.getParent().getId()));
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", dbContentDetailLink.getName()));
        add(link);
        if (dbContentDetailLink.getCssClass() != null) {
            link.add(new AttributeModifier("class", dbContentDetailLink.getCssClass()));
        }
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
