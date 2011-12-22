package com.btxtech.game.wicket.uiservices;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbCmsImage;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 01.04.2011
 * Time: 20:58:03
 */
public class CmsImageSelector extends Panel {
    @SpringBean
    private CmsService cmsService;

    public CmsImageSelector(String id) {
        super(id);
        add(new TextField<Integer>("cmsImageId", new IModel<Integer>() {

            @Override
            public Integer getObject() {
                DbCmsImage cmsImage = (DbCmsImage) getDefaultModelObject();
                if (cmsImage != null) {
                    return cmsImage.getId();
                } else {
                    return null;
                }
            }

            @Override
            public void setObject(Integer integer) {
                try {
                    if (integer != null) {
                        DbCmsImage dbCmsImage = cmsService.getImageCrudRootServiceHelper().readDbChild(integer);
                        setDefaultModelObject(dbCmsImage);
                    } else {
                        setDefaultModelObject(null);
                    }
                } catch (Throwable t) {
                    UiExceptionHandler.handleException(t, CmsImageSelector.this);
                }
            }

            @Override
            public void detach() {
                // Ignore
            }
        }, Integer.class));

    }
}
