package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentPlugin;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.cms.content.plugin.PluginEnum;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Arrays;

/**
 * User: beat
 * Date: 30.06.2011
 * Time: 17:46:43
 */
public class ContentPluginEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentPlugin> ruServiceHelper;

    public ContentPluginEditor(DbContentPlugin dbContentPlugin) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentPlugin> form = new Form<DbContentPlugin>("form", new CompoundPropertyModel<DbContentPlugin>(new RuModel<DbContentPlugin>(dbContentPlugin, DbContentPlugin.class) {
            @Override
            protected RuServiceHelper<DbContentPlugin> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new ContentCommonPanel("commonPanel", true, false, false, false));
        form.add(new DropDownChoice<PluginEnum>("pluginEnum", Arrays.asList(PluginEnum.values()), new IChoiceRenderer<PluginEnum>() {

            @Override
            public Object getDisplayValue(PluginEnum pluginEnum) {
                return pluginEnum.getDisplayName();
            }

            @Override
            public String getIdValue(PluginEnum pluginEnum, int index) {
                return pluginEnum.name();
            }
        }));

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

    }
}
