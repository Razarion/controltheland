package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.services.cms.DbPropertyBookLink;
import com.btxtech.game.services.common.CrudChild;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 00:17:38
 */
public class CmsPageLinkPanel extends Panel {
    public CmsPageLinkPanel(String id, DbPropertyBookLink dbPropertyBookLink, Object bean) {
        super(id);
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(CmsPage.ID, Integer.toString(dbPropertyBookLink.getPage().getId()));
        pageParameters.put(CmsPage.CHILD_ID, ((CrudChild) bean).getId());
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", dbPropertyBookLink.getLabel()));
        add(link);
    }
}
