package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbContent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 20:58:03
 */
public class DbContentSelector extends Panel {
    @SpringBean
    private CmsService cmsService;

    public DbContentSelector(String id) {
        super(id);
        add(new TextField<Integer>("contentId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbContent dbContent = (DbContent) getDefaultModelObject();
                if (dbContent != null) {
                    return dbContent.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbContent dbContent = cmsService.getContentCrud().readDbChild(integer);
                        setDefaultModelObject(dbContent);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, DbContentSelector.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
