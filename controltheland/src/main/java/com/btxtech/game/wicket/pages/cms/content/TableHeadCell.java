package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;


/**
 * User: beat
 * Date: 23.07.2011
 * Time: 11:54:22
 */
public class TableHeadCell extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;

    public TableHeadCell(String id, int contentListId, String columnName, BeanIdPathElement beanIdPathElement, ContentContext contentContext, String sortLinkCssClass) {
        super(id);
        PageParameters pageParameters = cmsUiService.createPageParametersFromBeanId(beanIdPathElement);
        pageParameters.put(ContentContext.generateSortInfoKey(contentListId), cmsUiService.getSortInfo(columnName, contentListId, contentContext));
        BookmarkablePageLink<Game> link = new BookmarkablePageLink<Game>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", columnName));
        if (sortLinkCssClass != null) {
            link.add(new SimpleAttributeModifier("class", sortLinkCssClass));
        }
        add(link);
    }
}
