package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbContentDynamicHtml;
import com.btxtech.game.services.cms.DbContentStaticHtml;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;


/**
 * User: beat
 * Date: 21.06.2011
 * Time: 17:46:43
 */
public class ContentDynamicHtmlEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentDynamicHtml> ruServiceHelper;

    public ContentDynamicHtmlEditor(DbContentDynamicHtml dbContentDynamicHtml) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentDynamicHtml> form = new Form<DbContentDynamicHtml>("form", new CompoundPropertyModel<DbContentDynamicHtml>(new RuModel<DbContentDynamicHtml>(dbContentDynamicHtml, DbContentDynamicHtml.class) {
            @Override
            protected RuServiceHelper<DbContentDynamicHtml> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new CheckBox("readRestricted"));
        form.add(new CheckBox("writeRestricted"));
        form.add(new TextField("cssClass"));
        form.add(new CheckBox("escapeMarkup"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });        
    }
}
