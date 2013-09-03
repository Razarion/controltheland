package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbPageStyle;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 20:58:03
 */
public class StyleSelector extends Panel {
    @SpringBean
    private CmsService cmsService;

    public StyleSelector(String id) {
        super(id);
        add(new TextField<Integer>("styleId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbPageStyle dbPageStyle = (DbPageStyle) getDefaultModelObject();
                if (dbPageStyle != null) {
                    return dbPageStyle.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbPageStyle dbPageStyle = cmsService.getPageStyleCrudRootServiceHelper().readDbChild(integer);
                        setDefaultModelObject(dbPageStyle);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, StyleSelector.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
