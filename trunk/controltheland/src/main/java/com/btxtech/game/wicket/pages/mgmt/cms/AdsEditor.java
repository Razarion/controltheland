package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.page.DbAds;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 28.07.2011
 * Time: 14:34:23
 */
public class AdsEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbAds> ruServiceHelper;

    public AdsEditor(DbAds dbAds) {
        add(new FeedbackPanel("msgs"));

        final Form<DbAds> form = new Form<DbAds>("form", new CompoundPropertyModel<DbAds>(new RuModel<DbAds>(dbAds, DbAds.class) {
            @Override
            protected RuServiceHelper<DbAds> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new TextArea("code"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new Button("cms") {

            @Override
            public void onSubmit() {
                setResponsePage(Cms.class);
            }
        });
    }

}
