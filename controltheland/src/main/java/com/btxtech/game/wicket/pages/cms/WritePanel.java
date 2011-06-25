package com.btxtech.game.wicket.pages.cms;

import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 23.06.2011
 * Time: 10:53:23
 */
public class WritePanel extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;

    public WritePanel(String id, Object value, final BeanIdPathElement beanIdPathElement) {
        super(id);
        add(new TextField("field", new LoadableDetachableModel(value) {

            @Override
            public void setObject(Object object) {
                super.setObject(object);
                cmsUiService.setDataProviderBean(object, beanIdPathElement);
            }

            @Override
            protected Object load() {
                return cmsUiService.getDataProviderBean(beanIdPathElement);
            }
        }));
    }
}
