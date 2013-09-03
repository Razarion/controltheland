package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CmsImageSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 20.06.2011
 * Time: 17:46:43
 */
public class ExpressionPropertyEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbExpressionProperty> ruServiceHelper;

    public ExpressionPropertyEditor(DbExpressionProperty dbExpressionProperty) {
        add(new FeedbackPanel("msgs"));

        final Form<DbExpressionProperty> form = new Form<DbExpressionProperty>("form", new CompoundPropertyModel<DbExpressionProperty>(new RuModel<DbExpressionProperty>(dbExpressionProperty, DbExpressionProperty.class) {
            @Override
            protected RuServiceHelper<DbExpressionProperty> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, true, false, false));
        form.add(new TextField("springBeanName"));
        form.add(new TextField("expression"));
        form.add(new DropDownChoice<>("editorType", Arrays.asList(DbExpressionProperty.EditorType.values())));
        form.add(new DropDownChoice<>("optionalType", Arrays.asList(DbExpressionProperty.Type.values())));
        form.add(new CheckBox("link"));
        form.add(new TextField("linkCssClass"));
        form.add(new CheckBox("sortable"));
        form.add(new TextField("sortHintExpression"));
        form.add(new CheckBox("defaultSortable"));
        form.add(new CheckBox("defaultSortableAsc"));
        form.add(new TextField("sortLinkCssClass"));
        form.add(new TextField("sortLinkCssClassActive"));
        form.add(new CmsImageSelector("sortAscActiveImage"));
        form.add(new CmsImageSelector("sortDescActiveImage"));


        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }
}
