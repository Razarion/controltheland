package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentActionButton;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
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
 * Date: 06.07.2011
 * Time: 17:46:43
 */
public class ContentActionButtonEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentActionButton> ruServiceHelper;

    public ContentActionButtonEditor(DbContentActionButton dbContentActionButton) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentActionButton> form = new Form<DbContentActionButton>("form", new CompoundPropertyModel<DbContentActionButton>(new RuModel<DbContentActionButton>(dbContentActionButton, DbContentActionButton.class) {
            @Override
            protected RuServiceHelper<DbContentActionButton> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, false, false, false));
        form.add(new TextField("unfilledHtml"));
        form.add(new CheckBox("unfilledHtmlEscapeMarkup"));
        form.add(new TextField("methodName"));
        form.add(new TextField("parameterExpression"));
        form.add(new TextField("springBeanName"));
        form.add(new TextField("leftSideSpringBeanName"));
        form.add(new TextField("leftSideOperandExpression"));
        form.add(new TextField("rightSideOperandExpression"));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }
}
