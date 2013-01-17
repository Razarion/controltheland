package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentBook;
import com.btxtech.game.services.cms.layout.DbContentRow;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.CrudListChildTableHelper;
import com.btxtech.game.wicket.uiservices.I18nStringEditor;
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
 * Time: 23:51:23
 */
public class ContentBookEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentBook> ruServiceHelper;

    public ContentBookEditor(DbContentBook dbContentBook) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentBook> form = new Form<>("form", new CompoundPropertyModel<DbContentBook>(new RuModel<DbContentBook>(dbContentBook, DbContentBook.class) {
            @Override
            protected RuServiceHelper<DbContentBook> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, true, true, true));
        form.add(new TextField("className"));
        form.add(new TextField("springBeanName"));
        form.add(new TextField("contentProviderGetter"));
        form.add(new CheckBox("showName"));
        form.add(new TextField("hiddenMethodName"));

        form.add(new CheckBox("navigationVisible"));
        form.add(new TextField("upNavigationName"));
        form.add(new TextField("previousNavigationName"));
        form.add(new TextField("nextNavigationName"));
        form.add(new TextField("navigationCssClass"));

        new CrudListChildTableHelper<DbContentBook, DbContentRow>("rows", null, "createRow", true, form, true) {

            @Override
            protected void extendedPopulateItem(Item<DbContentRow> dbContentItem) {
                dbContentItem.add(new I18nStringEditor("dbI18nName"));
                displayId(dbContentItem);
            }

            @Override
            protected RuServiceHelper<DbContentBook> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbContentBook getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudListChildServiceHelper<DbContentRow> getCrudListChildServiceHelperImpl() {
                return getParent().getRowCrud();
            }

            @Override
            protected void onEditSubmit(DbContentRow dbContentRow) {
                setResponsePage(ContentEditorFactory.createContentEditor(dbContentRow.getDbContent()));
            }

            @Override
            protected void setupCreate(WebMarkupContainer markupContainer, String createId) {
                markupContainer.add(new CreateDbContentPanel(createId) {

                    @Override
                    public void onDbContentSelected(Class<? extends DbContent> selected) {
                        try {
                            // Should not be here -> put to service class
                            DbContentRow dbContentRow = createDbChild();
                            DbContent dbContent = selected.getConstructor().newInstance();
                            dbContent.init(null);
                            dbContent.setParent(dbContentRow);
                            dbContentRow.setDbContent(dbContent);
                            getRuServiceHelper().updateDbEntity(form.getModelObject());
                            refresh();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
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
