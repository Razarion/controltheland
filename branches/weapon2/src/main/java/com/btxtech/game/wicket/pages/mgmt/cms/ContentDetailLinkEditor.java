package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentDetailLink;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 21.06.2011
 * Time: 17:46:43
 */
public class ContentDetailLinkEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentDetailLink> ruServiceHelper;

    public ContentDetailLinkEditor(DbContentDetailLink dbContentDetailLink) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentDetailLink> form = new Form<DbContentDetailLink>("form", new CompoundPropertyModel<DbContentDetailLink>(new RuModel<DbContentDetailLink>(dbContentDetailLink, DbContentDetailLink.class) {
            @Override
            protected RuServiceHelper<DbContentDetailLink> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new ContentCommonPanel("commonPanel", true, true, false, false));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }
}
