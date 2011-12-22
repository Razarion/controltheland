package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 20:58:03
 */
public class PageSelector extends Panel {
    @SpringBean
    private CmsService cmsService;

    public PageSelector(String id) {
        super(id);
        add(new TextField<Integer>("pageId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbPage dbPage = (DbPage) getDefaultModelObject();
                if (dbPage != null) {
                    return dbPage.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbPage dbPage = cmsService.getPageCrudRootServiceHelper().readDbChild(integer);
                        setDefaultModelObject(dbPage);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, PageSelector.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
