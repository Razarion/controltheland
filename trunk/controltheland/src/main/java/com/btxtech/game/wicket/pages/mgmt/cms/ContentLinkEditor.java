package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbContentLink;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CmsImageSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
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
public class ContentLinkEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentLink> ruServiceHelper;

    public ContentLinkEditor(DbContentLink dbContentLink) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentLink> form = new Form<DbContentLink>("form", new CompoundPropertyModel<DbContentLink>(new RuModel<DbContentLink>(dbContentLink, DbContentLink.class) {
            @Override
            protected RuServiceHelper<DbContentLink> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new ContentAccessPanel("accessPanel", false, false));
        form.add(new TextField("cssClass"));
        form.add(new TextField("url"));
        form.add(new CmsImageSelector("dbCmsImage"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

    }
}
