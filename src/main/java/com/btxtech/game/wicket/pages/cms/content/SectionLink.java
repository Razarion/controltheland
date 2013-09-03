package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.jsre.common.CmsUtil;
import com.btxtech.game.services.cms.CmsSectionInfo;
import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.AttributeModifier;
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
public class SectionLink extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private CmsService cmsService;
    private int contentId;

    public SectionLink(String id, String value, CmsSectionInfo sectionName, DbExpressionProperty dbExpressionProperty, CrudChild crudChild) {
        super(id);
        contentId = dbExpressionProperty.getId();
        PageParameters pageParameters = new PageParameters();
        pageParameters.set(CmsUtil.ID, Integer.toString(sectionName.getPageId()));
        pageParameters.set(CmsUtil.SECTION_ID, sectionName.getSectionName());
        ////
        CrudChild tmpCrudChild = crudChild;
        List<Serializable> ids = new ArrayList<Serializable>();
        ids.add(crudChild.getId());
        while (tmpCrudChild.getParent() != null && tmpCrudChild.getParent() instanceof CrudChild) {
            tmpCrudChild = (CrudChild) tmpCrudChild.getParent();
            ids.add(tmpCrudChild.getId());
        }
        Collections.reverse(ids);
        for (int i = 0; i < ids.size(); i++) {
            Serializable serializable = ids.get(i);
            pageParameters.set(CmsUtil.getChildUrlParameter(i), serializable);
        }
        ////
        BookmarkablePageLink<CmsPage> link = new BookmarkablePageLink<CmsPage>("link", CmsPage.class, pageParameters);
        link.add(new Label("label", value));
        add(link);
        if (dbExpressionProperty.getLinkCssClass() != null) {
            link.add(new AttributeModifier("class", dbExpressionProperty.getLinkCssClass()));
        }
    }


    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
