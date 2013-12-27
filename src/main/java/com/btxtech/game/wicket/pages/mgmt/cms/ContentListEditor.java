package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentCreateEdit;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudChildTableHelper;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 20.06.2011
 * Time: 14:09:37
 */
public class ContentListEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentList> ruServiceHelper;

    public ContentListEditor(DbContentList dbContentList) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentList> form = new Form<DbContentList>("form", new CompoundPropertyModel<DbContentList>(new RuModel<DbContentList>(dbContentList, DbContentList.class) {
            @Override
            protected RuServiceHelper<DbContentList> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, true, true, true));
        form.add(new CreateCreateEditPanel("dbContentCreateEdit"){
            @Override
            protected void createDbContentCreateEdit() {
                // Should not be here -> put to service class
                DbContentCreateEdit dbContentCreateEdit = new DbContentCreateEdit();
                dbContentCreateEdit.init(null);
                form.getModelObject().setDbContentCreateEdit(dbContentCreateEdit);
                dbContentCreateEdit.setParent(form.getModelObject());
                ruServiceHelper.updateDbEntity(form.getModelObject());                
            }

            @Override
            protected void deleteDbContentCreateEdit(DbContentCreateEdit dbContentCreateEdit) {
                dbContentCreateEdit.setParent(null);
                form.getModelObject().setDbContentCreateEdit(null);
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new TextField("springBeanName"));
        form.add(new TextField("contentProviderGetter"));
        form.add(new TextField("rowsPerPage"));
        form.add(new CheckBox("showHead"));
        form.add(new TextField("cssClassHead"));
        form.add(new TextField("columnCountSingleCell"));

        new CrudListChildTableHelper<DbContentList, DbContent>("columns", null, "createColumn", true, form, true) {

            @Override
            protected void extendedPopulateItem(Item<DbContent> dbContentItem) {
                super.extendedPopulateItem(dbContentItem);
                displayId(dbContentItem);
            }

            @Override
            protected RuServiceHelper<DbContentList> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbContentList getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudListChildServiceHelper<DbContent> getCrudListChildServiceHelperImpl() {
                return getParent().getColumnsCrud();
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new CreateDbContentPanel(createId) {

                    @Override
                    public void onDbContentSelected(Class<? extends DbContent> selected) {
                        createDbChild(selected);
                        refresh();
                    }
                });
            }

            @Override
            protected void onEditSubmit(DbContent dbContent) {
                setResponsePage(ContentEditorFactory.createContentEditor(dbContent));
            }
        };

        new CrudChildTableHelper<DbContentList, DbContentBook>("contentBooks", null, "createContentBook", true, form, false) {

            @Override
            protected void extendedPopulateItem(Item<DbContentBook> contentBookItem) {
                super.extendedPopulateItem(contentBookItem);
                displayId(contentBookItem);
            }

            @Override
            protected RuServiceHelper<DbContentList> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbContentList getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudChildServiceHelper<DbContentBook> getCrudChildServiceHelperImpl() {
                return getParent().getContentBookCrud();
            }

            @Override
            protected void onEditSubmit(DbContentBook dbContent) {
                setResponsePage(ContentEditorFactory.createContentEditor(dbContent));
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
