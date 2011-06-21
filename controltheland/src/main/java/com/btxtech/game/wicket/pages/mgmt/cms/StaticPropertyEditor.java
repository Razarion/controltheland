package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbStaticProperty;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;


/**
 * User: beat
 * Date: 21.06.2011
 * Time: 17:46:43
 */
public class StaticPropertyEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbStaticProperty> ruServiceHelper;

    public StaticPropertyEditor(DbStaticProperty dbStaticProperty) {
        add(new FeedbackPanel("msgs"));

        final Form<DbStaticProperty> form = new Form<DbStaticProperty>("form", new CompoundPropertyModel<DbStaticProperty>(new RuModel<DbStaticProperty>(dbStaticProperty, DbStaticProperty.class) {
            @Override
            protected RuServiceHelper<DbStaticProperty> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new TextArea("html"));
        form.add(new CheckBox("escapeMarkup"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });        
    }
}
