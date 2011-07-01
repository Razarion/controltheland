package com.btxtech.game.wicket.pages.mgmt.cms;

import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbPage;
import com.btxtech.game.services.common.RuServiceHelper;
import com.btxtech.game.wicket.pages.mgmt.MgmtWebPage;
import com.btxtech.game.wicket.uiservices.MenuSelector;
import com.btxtech.game.wicket.uiservices.RuModel;
import com.btxtech.game.wicket.uiservices.StyleSelector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.lang.reflect.Constructor;

/**
 * User: beat
 * Date: 06.06.2011
 * Time: 02:02:56
 */
public class PageEditor extends MgmtWebPage {
    @SpringBean
    private RuServiceHelper<DbPage> ruServiceHelper;

    public PageEditor(DbPage dbPage) {
        add(new FeedbackPanel("msgs"));

        Form<DbPage> form = new Form<DbPage>("form", new CompoundPropertyModel<DbPage>(new RuModel<DbPage>(dbPage, DbPage.class) {
            @Override
            protected RuServiceHelper<DbPage> getRuServiceHelper() {
                return ruServiceHelper;
            }
        }));
        add(form);
        form.add(new CheckBox("headerVisible"));
        form.add(new CheckBox("footerVisible"));
        form.add(new CheckBox("adsVisible"));
        form.add(new CheckBox("accessRestricted"));
        setupStyle(form);
        setupContent(form);
        setupButtons(form);
    }

    private void setupContent(final Form<DbPage> form) {
        form.add(new CreateDbContentPanel("create") {

            @Override
            public void onDbContentSelected(Class<? extends DbContent> selected) {
                try {
                    Constructor<? extends DbContent> constructor = selected.getConstructor();
                    DbContent dbContent = constructor.newInstance();
                    form.getModelObject().setContent(dbContent);
                    ruServiceHelper.updateDbEntity(form.getModelObject());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getContent() == null;
            }
        });
        form.add(new Button("edit") {

            @Override
            public void onSubmit() {
                setResponsePage(ContentEditorFactory.createContentEditor(form.getModelObject().getContent()));
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getContent() != null;
            }
        });

        form.add(new Button("delete") {

            @Override
            public void onSubmit() {
                form.getModelObject().setContent(null);
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }

            @Override
            public boolean isVisible() {
                return form.getModelObject().getContent() != null;
            }
        });

        form.add(new Label("contentInfo", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                DbContent dbContent = form.getModelObject().getContent();
                if (dbContent != null) {
                    return dbContent.getClass().getSimpleName() + " Id: " + dbContent.getId();
                } else {
                    return "-";
                }
            }
        }) {
            @Override
            public boolean isVisible() {
                return form.getModelObject().getContent() != null;
            }
        });
    }

    private void setupButtons(final Form<DbPage> form) {
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                ruServiceHelper.updateDbEntity(form.getModelObject());
            }
        });
        form.add(new Button("cms") {

            @Override
            public void onSubmit() {
                setResponsePage(Cms.class);
            }
        });
    }

    private void setupStyle(Form<DbPage> form) {
        form.add(new CheckBox("home"));
        form.add(new MenuSelector("menu"));
        form.add(new StyleSelector("style"));
    }
}
