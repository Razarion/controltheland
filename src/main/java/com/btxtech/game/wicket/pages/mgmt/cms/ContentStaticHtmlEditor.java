package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentStaticHtml;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.I18nStringAreaEditor;
import com.btxtech.game.wicket.uiservices.I18nStringWYSIWYGEditor;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;


/**
 * User: beat
 * Date: 21.06.2011
 * Time: 17:46:43
 */
public class ContentStaticHtmlEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentStaticHtml> ruServiceHelper;

    public ContentStaticHtmlEditor(DbContentStaticHtml dbContentStaticHtml) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentStaticHtml> form = new Form<DbContentStaticHtml>("form", new CompoundPropertyModel<DbContentStaticHtml>(new RuModel<DbContentStaticHtml>(dbContentStaticHtml, DbContentStaticHtml.class) {
            @Override
            protected RuServiceHelper<DbContentStaticHtml> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, false, false, false));
        form.add(new I18nStringAreaEditor("dbI18nHtml"));
        form.add(new DropDownChoice<>("editorType", Arrays.asList(DbExpressionProperty.EditorType.values())));
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }
}
