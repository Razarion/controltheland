package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentStartLevelTaskButton;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CmsImageSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 25.07.2011
 * Time: 17:51:24
 */
public class ContentStartLevelTaskButtonEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentStartLevelTaskButton> ruServiceHelper;

    public ContentStartLevelTaskButtonEditor(DbContentStartLevelTaskButton dbContentStartLevelTaskButton) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentStartLevelTaskButton> form = new Form<DbContentStartLevelTaskButton>("form", new CompoundPropertyModel<DbContentStartLevelTaskButton>(new RuModel<DbContentStartLevelTaskButton>(dbContentStartLevelTaskButton, DbContentStartLevelTaskButton.class) {
            @Override
            protected RuServiceHelper<DbContentStartLevelTaskButton> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentAccessPanel("accessPanel", true, true, true, true));
        form.add(new TextField("cssClass"));
        form.add(new TextField("expression"));
        form.add(new TextField("doneExpression"));
        form.add(new CmsImageSelector("startImage"));
        form.add(new CmsImageSelector("doneImage"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }

}
