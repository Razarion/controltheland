package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.page.DbMenu;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 20:58:03
 */
public class MenuSelector extends Panel {
    @SpringBean
    private CmsService cmsService;

    public MenuSelector(String id) {
        super(id);
        add(new TextField<Integer>("menuId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbMenu dbMenu = (DbMenu) getDefaultModelObject();
                if (dbMenu != null) {
                    return dbMenu.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbMenu dbMenu = cmsService.getMenuCrudRootServiceHelper().readDbChild(integer);
                        setDefaultModelObject(dbMenu);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, MenuSelector.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
