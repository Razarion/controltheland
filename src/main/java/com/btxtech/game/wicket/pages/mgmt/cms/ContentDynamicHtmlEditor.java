package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentDynamicHtml;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;


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

        form.add(new ContentCommonPanel("commonPanel", true, true, false, false));
        form.add(new DropDownChoice<>("editorType", Arrays.asList(DbExpressionProperty.EditorType.values())));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }
}
