package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.cms.DbPageStyle;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.uiservices.MenuSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.StyleSelector;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 06.06.2011
 * Time: 02:02:56
 */
public class PageEditor extends WebPage {
    @SpringBean
    private RuServiceHelper<DbPage> ruServiceHelper;

    public PageEditor(DbPage dbPage) {
        add(new FeedbackPanel("msgs"));

        final Form<DbPage> form = new Form<DbPage>("form", new CompoundPropertyModel<DbPage>(new RuModel<DbPage>(dbPage, DbPage.class) {
            @Override
            protected RuServiceHelper<DbPage> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new CheckBox("home"));
        form.add(new MenuSelector("menu"));
        form.add(new StyleSelector("style"));

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
