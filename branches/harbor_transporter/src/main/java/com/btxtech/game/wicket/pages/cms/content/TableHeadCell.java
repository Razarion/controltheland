package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.wicket.pages.Game;
import com.btxtech.game.wicket.pages.cms.CmsImageResource;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
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

    public TableHeadCell(String id, int contentListId, DbExpressionProperty columnDbExpressionProperty, BeanIdPathElement beanIdPathElement, ContentContext contentContext) {
        super(id);
        PageParameters pageParameters = cmsUiService.createPageParametersFromBeanId(beanIdPathElement);
        pageParameters.set(ContentContext.generateSortInfoKey(contentListId), ContentContext.getSortInfo(columnDbExpressionProperty.getName(), contentListId, contentContext));
        BookmarkablePageLink<Game> link = new BookmarkablePageLink<Game>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", columnDbExpressionProperty.getName()));
        if (contentContext.isSortColumnActive(contentListId, columnDbExpressionProperty)) {
            if (columnDbExpressionProperty.getSortLinkCssClassActive() != null) {
                link.add(new AttributeModifier("class", columnDbExpressionProperty.getSortLinkCssClassActive()));
            }
            if (contentContext.isAscSorting(contentListId) && columnDbExpressionProperty.getSortAscActiveImage() != null) {
                link.add(CmsImageResource.createImage("image", columnDbExpressionProperty.getSortAscActiveImage()));
            } else if (!contentContext.isAscSorting(contentListId) && columnDbExpressionProperty.getSortDescActiveImage() != null) {
                link.add(CmsImageResource.createImage("image", columnDbExpressionProperty.getSortDescActiveImage()));
            } else {
                link.add(new Image("image", "").setVisible(false));
            }
        } else {
            if (columnDbExpressionProperty.getSortLinkCssClass() != null) {
                link.add(new AttributeModifier("class", columnDbExpressionProperty.getSortLinkCssClass()));
            }
            Image image = new Image("image", "");
            image.setVisible(false);
            link.add(image);
        }
        add(link);
    }
}
