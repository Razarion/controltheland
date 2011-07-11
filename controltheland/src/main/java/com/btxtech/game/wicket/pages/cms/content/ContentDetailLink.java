package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DbContentDetailLink;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(CmsPage.DETAIL_CONTENT_ID, Integer.toString(dbContentDetailLink.getParent().getId()));
        pageParameters.put(CmsPage.ID, Integer.toString(beanIdPathElement.getPageId()));
        fillBeanIdPathUrlParameters(beanIdPathElement, pageParameters);
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", dbContentDetailLink.getName()));
        add(link);
        if (dbContentDetailLink.getCssClass() != null) {
            add(new SimpleAttributeModifier("class", dbContentDetailLink.getCssClass()));
        }
    }

    private void fillBeanIdPathUrlParameters(BeanIdPathElement beanIdPathElement, PageParameters pageParameters) {
        List<Serializable> beanIds = new ArrayList<Serializable>();
        BeanIdPathElement tmpBeanIdPathElement = beanIdPathElement;
        while (tmpBeanIdPathElement != null) {
            if (tmpBeanIdPathElement.hasBeanId()) {
                if (beanIds.size() >= CmsPage.MAX_LEVELS) {
                    throw new IllegalStateException("Max level reached");
                }
                beanIds.add(tmpBeanIdPathElement.getBeanId());
            }
            if (tmpBeanIdPathElement.hasParent() && !tmpBeanIdPathElement.getParent().hasSpringBeanName()) {
                tmpBeanIdPathElement = tmpBeanIdPathElement.getParent();
            } else {
                tmpBeanIdPathElement = null;
            }
        }
        Collections.reverse(beanIds);
        for (int level = 0, beanIdsSize = beanIds.size(); level < beanIdsSize; level++) {
            Serializable beanId = beanIds.get(level);
            pageParameters.put(CmsPage.getChildUrlParameter(level), beanId);
        }
    }


    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
