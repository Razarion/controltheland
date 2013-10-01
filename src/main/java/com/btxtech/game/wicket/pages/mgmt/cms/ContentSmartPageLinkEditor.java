package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentSmartPageLink;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.PageSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 30.06.2011
 * Time: 17:46:43
 */
public class ContentSmartPageLinkEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentSmartPageLink> ruServiceHelper;

    public ContentSmartPageLinkEditor(DbContentSmartPageLink dbContentSmartPageLink) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentSmartPageLink> form = new Form<DbContentSmartPageLink>("form", new CompoundPropertyModel<DbContentSmartPageLink>(new RuModel<DbContentSmartPageLink>(dbContentSmartPageLink, DbContentSmartPageLink.class) {
            @Override
            protected RuServiceHelper<DbContentSmartPageLink> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new ContentCommonPanel("commonPanel", true, false, false, false));
        form.add(new PageSelector("dbPage"));
        form.add(ContentCommonPanel.createSelectField("enableAccess"));
        form.add(new TextField("buttonName"));
        form.add(new TextField("springBeanName"));
        form.add(new TextField("propertyExpression"));
        form.add(new TextField("accessDeniedString"));
        form.add(new TextField("string0"));
        form.add(new TextField("string1"));
        form.add(new TextField("stringN"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

    }
}
