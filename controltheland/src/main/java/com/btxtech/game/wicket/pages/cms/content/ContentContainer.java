package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentContainer;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:29:43
 */
public class ContentContainer extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private BeanIdPathElement beanIdPathElement;
    private ContentContext contentContext;
    private int contentId;

    public ContentContainer(String id, DbContentContainer dbContentContainer, final BeanIdPathElement beanIdPathElement, ContentContext contentContext) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        this.contentContext = contentContext;
        contentId = dbContentContainer.getId();
        setupContainer(dbContentContainer);
        if (dbContentContainer.getCssClass() != null) {
            add(new AttributeModifier("class", dbContentContainer.getCssClass()));
        }
    }

    private void setupContainer(DbContentContainer dbContentContainer) {
        Object bean = cmsUiService.getDataProviderBean(beanIdPathElement);
        RepeatingView view = new RepeatingView("container");
        for (DbContent dbContent : dbContentContainer.getContentCrud().readDbChildren()) {
            BeanIdPathElement childBeanIdPathElement = cmsUiService.createChildBeanIdPathElement(dbContent, beanIdPathElement, null);
            view.add(cmsUiService.getComponent(dbContent, bean, view.newChildId(), childBeanIdPathElement, contentContext));
        }
        add(view);
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
