package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.DbContentDetailLink;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 00:17:38
 */
public class ContentDetailLink extends Panel {
    public ContentDetailLink(String id, DbContentDetailLink dbContentDetailLink, BeanIdPathElement beanIdPathElement) {
        super(id);
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(CmsPage.ID, Integer.toString(beanIdPathElement.getPageId()));
        pageParameters.put(CmsPage.CHILD_ID, beanIdPathElement.getBeanId());
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", dbContentDetailLink.getName()));
        add(link);
        if (dbContentDetailLink.getCssClass() != null) {
            add(new SimpleAttributeModifier("class", dbContentDetailLink.getCssClass()));
        }
    }
}
