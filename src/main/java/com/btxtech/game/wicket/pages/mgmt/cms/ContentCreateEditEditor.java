package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentCreateEdit;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 15.07.2011
 * Time: 11:42:27
 */
public class ContentCreateEditEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentCreateEdit> ruServiceHelper;

    public ContentCreateEditEditor(DbContentCreateEdit dbContentCreateEdit) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentCreateEdit> form = new Form<DbContentCreateEdit>("form", new CompoundPropertyModel<DbContentCreateEdit>(new RuModel<DbContentCreateEdit>(dbContentCreateEdit, DbContentCreateEdit.class) {
            @Override
            protected RuServiceHelper<DbContentCreateEdit> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new TextField("name"));        
        form.add(new ContentCommonPanel("commonPanel", false, false, true, false));

        new CrudListChildTableHelper<DbContentCreateEdit, DbExpressionProperty>("columns", null, "createColumn", true, form, true) {

            @Override
            protected void extendedPopulateItem(Item<DbExpressionProperty> dbContentItem) {
                super.extendedPopulateItem(dbContentItem);
                displayId(dbContentItem);
            }

            @Override
            protected RuServiceHelper<DbContentCreateEdit> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbContentCreateEdit getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudListChildServiceHelper<DbExpressionProperty> getCrudListChildServiceHelperImpl() {
                return getParent().getValueCrud();
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new CreateDbContentPanel(createId) {

                    @Override
                    public void onDbContentSelected(Class<? extends DbContent> selected) {
                        //createDbChild(selected);
                        createDbChild();
                        refresh();
                    }
                });
            }

            @Override
            protected void onEditSubmit(DbExpressionProperty dbExpressionProperty) {
                setResponsePage(ContentEditorFactory.createContentEditor(dbExpressionProperty));
            }
        };

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

    }
}

