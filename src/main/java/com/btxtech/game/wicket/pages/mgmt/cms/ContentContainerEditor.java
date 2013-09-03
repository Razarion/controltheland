package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentContainer;
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
 * Date: 20.06.2011
 * Time: 14:09:37
 */
public class ContentContainerEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentContainer> ruServiceHelper;

    public ContentContainerEditor(DbContentContainer dbContentContainer) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentContainer> form = new Form<DbContentContainer>("form", new CompoundPropertyModel<DbContentContainer>(new RuModel<DbContentContainer>(dbContentContainer, DbContentContainer.class) {
            @Override
            protected RuServiceHelper<DbContentContainer> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, true, true, true));
        form.add(new TextField("springBeanName"));
        form.add(new TextField("contentProviderGetter"));
        form.add(new TextField("expression"));

        new CrudListChildTableHelper<DbContentContainer, DbContent>("children", null, "createChild", true, form, true) {

            @Override
            protected void extendedPopulateItem(Item<DbContent> dbContentItem) {
                super.extendedPopulateItem(dbContentItem);
                displayId(dbContentItem);
            }

            @Override
            protected RuServiceHelper<DbContentContainer> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbContentContainer getParent() {
                return form.getModelObject();
            }

            @Override
            protected CrudListChildServiceHelper<DbContent> getCrudListChildServiceHelperImpl() {
                return getParent().getContentCrud();
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

        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });

    }

}
