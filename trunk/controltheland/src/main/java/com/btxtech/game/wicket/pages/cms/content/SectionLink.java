package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.CmsSectionInfo;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 08.06.2011
 * Time: 00:17:38
 */
public class SectionLink extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private CmsService cmsService;
    private int contentId;

    public SectionLink(String id, String value, CmsSectionInfo sectionName, DbExpressionProperty dbExpressionProperty, Object bean) {
        super(id);
        contentId = dbExpressionProperty.getId();
        PageParameters pageParameters = new PageParameters();
        pageParameters.put(CmsUtil.ID, Integer.toString(sectionName.getPageId()));
        pageParameters.put(CmsUtil.SECTION_ID, sectionName.getSectionName());
        pageParameters.put(CmsUtil.CHILD_ID, ((CrudChild) bean).getId());
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", value));
        add(link);
        if (dbExpressionProperty.getCssClass() != null) {
            link.add(new SimpleAttributeModifier("class", dbExpressionProperty.getCssClass()));
        }
    }


    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
