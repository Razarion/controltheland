package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentInvoker;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.services.common.CrudListChildServiceHelper;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.user.UserService;
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
 * Date: 27.07.2011
 * Time: 00:30:37
 */
public class ContentInvokerEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentInvoker> ruServiceHelper;
    @SpringBean
    private UserService userService;

    public ContentInvokerEditor(DbContentInvoker dbContentInvoker) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentInvoker> form = new Form<DbContentInvoker>("form", new CompoundPropertyModel<DbContentInvoker>(new RuModel<DbContentInvoker>(dbContentInvoker, DbContentInvoker.class) {
            @Override
            protected RuServiceHelper<DbContentInvoker> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, false, false, false));
        form.add(new TextField("springBeanName"));
        form.add(new TextField("methodName"));
        form.add(new TextField("invokeButtonName"));
        form.add(new TextField("cancelButtonName"));

        new CrudListChildTableHelper<DbContentInvoker, DbExpressionProperty>("columns", null, "createColumn", true, form, true) {

            @Override
            protected void extendedPopulateItem(Item<DbExpressionProperty> dbContentItem) {
                super.extendedPopulateItem(dbContentItem);
                displayId(dbContentItem);
            }

            @Override
            protected RuServiceHelper<DbContentInvoker> getRuServiceHelper() {
                return ruServiceHelper;
            }

            @Override
            protected DbContentInvoker getParent() {
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
