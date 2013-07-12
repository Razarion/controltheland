package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentInvokerButton;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 26.07.2011
 * Time: 12:20:09
 */
public class ContentInvokerButton extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentInvokerButton(String componentId, DbContentInvokerButton dbContentInvokerButton, final BeanIdPathElement beanIdPathElement) {
        super(componentId);
        contentId = dbContentInvokerButton.getId();
        setDefaultModel(new LoadableDetachableModel<DbContentInvokerButton>() {
            @Override
            protected DbContentInvokerButton load() {
                return cmsUiService.getDbContent(contentId);
            }
        });

        add(new Button("button", new Model<String>(dbContentInvokerButton.getName())) {
            @Override
            public void onSubmit() {
                cmsUiService.setInvokerResponsePage(this, beanIdPathElement.getPageId(), ((DbContentInvokerButton) ContentInvokerButton.this.getDefaultModelObject()).getDbContentInvoker());
            }
        });

        if (dbContentInvokerButton.getCssClass() != null) {
            add(new AttributeModifier("class", dbContentInvokerButton.getCssClass()));
        }
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }

}
