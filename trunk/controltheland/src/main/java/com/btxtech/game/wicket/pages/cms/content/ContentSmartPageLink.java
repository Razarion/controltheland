package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContentSmartPageLink;
import com.btxtech.game.wicket.pages.cms.CmsStringGenerator;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 24.07.2011
 * Time: 17:47:09
 */
public class ContentSmartPageLink extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;

    public ContentSmartPageLink(String id, DbContentSmartPageLink dbContentSmartPageLink) {
        super(id);
        contentId = dbContentSmartPageLink.getId();
        if (cmsUiService.isAllowedGeneric(dbContentSmartPageLink.getEnableAccess())) {
            final String springBeanName = dbContentSmartPageLink.getSpringBeanName();
            final String propertyExpression = dbContentSmartPageLink.getPropertyExpression();
            final String string0 = dbContentSmartPageLink.getString0();
            final String string1 = dbContentSmartPageLink.getString1();
            final String stringN = dbContentSmartPageLink.getStringN();


            add(new Label("label", new IModel<String>() {

                @Override
                public String getObject() {
                    Integer value = (Integer) cmsUiService.getValue(springBeanName, propertyExpression);
                    return CmsStringGenerator.createNumberString(value, string0, string1, stringN);
                }

                @Override
                public void setObject(String object) {
                }

                @Override
                public void detach() {
                }
            }));
            final int dbPageId = dbContentSmartPageLink.getDbPage().getId();
            Button button = new Button("button", new Model<String>(dbContentSmartPageLink.getButtonName())) {
                @Override
                public void onSubmit() {
                    cmsUiService.setResponsePage(ContentSmartPageLink.this, dbPageId);
                }
            };
            add(button);
        } else {
            add(new Label("label", dbContentSmartPageLink.getAccessDeniedString()));
            Button button = new Button("button", new Model<String>(dbContentSmartPageLink.getButtonName()));
            button.setEnabled(false);
            add(button);
        }

        if (dbContentSmartPageLink.getCssClass() != null) {
            add(new AttributeModifier("class", dbContentSmartPageLink.getCssClass()));
        }
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
