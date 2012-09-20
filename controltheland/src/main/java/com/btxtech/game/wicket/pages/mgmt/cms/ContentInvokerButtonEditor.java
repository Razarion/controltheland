package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.layout.DbContentInvoker;
import com.btxtech.game.services.cms.layout.DbContentInvokerButton;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.services.user.UserService;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.RuModel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * User: beat
 * Date: 27.07.2011
 * Time: 00:30:37
 */
public class ContentInvokerButtonEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbContentInvokerButton> ruServiceHelper;
    @SpringBean
    private UserService userService;

    public ContentInvokerButtonEditor(DbContentInvokerButton dbContentInvokerButton) {
        add(new FeedbackPanel("msgs"));

        final Form<DbContentInvokerButton> form = new Form<DbContentInvokerButton>("form", new CompoundPropertyModel<DbContentInvokerButton>(new RuModel<DbContentInvokerButton>(dbContentInvokerButton, DbContentInvokerButton.class) {
            @Override
            protected RuServiceHelper<DbContentInvokerButton> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);

        form.add(new ContentCommonPanel("commonPanel", true, false, false, false));
        form.add(new Button("createDbContentInvoker") {
            @Override
            public void onSubmit() {
                try {
                    // Should be in a service
                    DbContentInvoker dbContentInvoker = new DbContentInvoker();
                    dbContentInvoker.init(userService);
                    dbContentInvoker.setParent(form.getModelObject());
                    form.getModelObject().setDbContentInvoker(dbContentInvoker);
                    ruServiceHelper.updateDbEntity(form.getModelObject());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getDbContentInvoker() == null;
            }
        });
        form.add(new Button("editDbContentInvoker") {
            @Override
            public void onSubmit() {
                setResponsePage(new ContentInvokerEditor(form.getModelObject().getDbContentInvoker()));
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getDbContentInvoker() != null;
            }
        });
        form.add(new Button("deleteDbContentInvoker") {
            @Override
            public void onSubmit() {
                form.getModelObject().setDbContentInvoker(null);
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getDbContentInvoker() != null;
            }
        });


        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
    }

}
