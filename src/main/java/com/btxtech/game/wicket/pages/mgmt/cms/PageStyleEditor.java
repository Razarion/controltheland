package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.page.DbPageStyle;
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
 * Date: 06.06.2011
 * Time: 00:12:05
 */
public class PageStyleEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbPageStyle> ruServiceHelper;

    public PageStyleEditor(DbPageStyle dbPageStyle) {
        add(new FeedbackPanel("msgs"));

        final Form<DbPageStyle> form = new Form<DbPageStyle>("form", new CompoundPropertyModel<DbPageStyle>(new RuModel<DbPageStyle>(dbPageStyle, DbPageStyle.class) {
            @Override
            protected RuServiceHelper<DbPageStyle> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new TextArea("css"));

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
