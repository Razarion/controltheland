package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentPageLink;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CmsImageSelector;
import com.btxtech.game.wicket.uiservices.I18nStringEditor;
import com.btxtech.game.wicket.uiservices.PageSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 30.06.2011
 * Time: 17:46:43
 */
public class ContentPageLinkEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentPageLink> ruServiceHelper;

    public ContentPageLinkEditor(DbContentPageLink dbContentPageLink) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentPageLink> form = new Form<DbContentPageLink>("form", new CompoundPropertyModel<DbContentPageLink>(new RuModel<DbContentPageLink>(dbContentPageLink, DbContentPageLink.class) {
            @Override
            protected RuServiceHelper<DbContentPageLink> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new ContentCommonPanel("commonPanel", true, false, false, false));
        form.add(new PageSelector("dbPage"));
        form.add(new CmsImageSelector("dbCmsImage"));
        form.add(new I18nStringEditor("dbI18nName"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

    }
}
