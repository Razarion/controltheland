package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentActivateQuestButton;
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
 * Date: 13.04.12
 * Time: 17:06
 */
public class ContentActivateQuestButtonEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentActivateQuestButton> ruServiceHelper;

    public ContentActivateQuestButtonEditor(DbContentActivateQuestButton dbContentActivateQuestButton) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentActivateQuestButton> form = new Form<>("form", new CompoundPropertyModel<DbContentActivateQuestButton>(new RuModel<DbContentActivateQuestButton>(dbContentActivateQuestButton, DbContentActivateQuestButton.class) {
            @Override
            protected RuServiceHelper<DbContentActivateQuestButton> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, true, true, true));
        form.add(new TextField("expression"));
        form.add(new CmsImageSelector("startImage"));
        form.add(new TextField("doneExpression"));
        form.add(new CmsImageSelector("doneImage"));
        form.add(new TextField("activeExpression"));
        form.add(new CmsImageSelector("abortImage"));
        form.add(new TextField("blockedExpression"));
        form.add(new CmsImageSelector("blockedImage"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }
}
