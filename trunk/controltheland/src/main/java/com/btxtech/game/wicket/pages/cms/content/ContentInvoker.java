package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentInvoker;
import com.btxtech.game.services.cms.DbExpressionProperty;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

/**
 * User: beat
 * Date: 26.07.2011
 * Time: 12:20:09
 */
public class ContentInvoker extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private int contentId;
    private HashMap<String, String> parameters = new HashMap<String, String>();
    private BeanIdPathElement beanIdPathElement;

    public ContentInvoker(String componentId, DbContentInvoker dbContentInvoker, BeanIdPathElement beanIdPathElement) {
        super(componentId);
        this.beanIdPathElement = beanIdPathElement;
        contentId = dbContentInvoker.getId();
        setDefaultModel(new LoadableDetachableModel<DbContentInvoker>() {
            @Override
            protected DbContentInvoker load() {
                return cmsUiService.getDbContent(contentId);
            }

            @Override
            protected void onDetach() {
                parameters.clear();
            }
        });
        setupCreateFields();
        setupButtons(dbContentInvoker);
    }

    private void setupButtons(DbContentInvoker dbContentInvoker) {
        add(new Button("invoke", new Model<String>(dbContentInvoker.getInvokeButtonName())) {
            @Override
            public void onSubmit() {
                try {
                    cmsUiService.invoke((DbContentInvoker) ContentInvoker.this.getDefaultModelObject(), parameters);
                    cmsUiService.setParentResponsePage(ContentInvoker.this, (DbContent) ContentInvoker.this.getDefaultModelObject(), beanIdPathElement);
                } catch (InvocationTargetException e) {
                    cmsUiService.setMessageResponsePage(ContentInvoker.this, e.getTargetException().getMessage());
                }
            }
        });
        add(new Button("cancel", new Model<String>(dbContentInvoker.getCancelButtonName())) {
            @Override
            public void onSubmit() {
                cmsUiService.setParentResponsePage(ContentInvoker.this, (DbContent) ContentInvoker.this.getDefaultModelObject(), beanIdPathElement);
            }
        });
    }

    private void setupCreateFields() {
        ListView<DbExpressionProperty> listView = new ListView<DbExpressionProperty>("listView", new LoadableDetachableModel<List<DbExpressionProperty>>() {

            @Override
            protected List<DbExpressionProperty> load() {
                DbContentInvoker dbContentInvoker = (DbContentInvoker) ContentInvoker.this.getDefaultModelObject();
                return dbContentInvoker.getValueCrud().readDbChildren();
            }
        }) {

            @Override
            protected void populateItem(ListItem<DbExpressionProperty> dbExpressionPropertyListItem) {
                DbExpressionProperty dbExpressionProperty = dbExpressionPropertyListItem.getModelObject();
                dbExpressionPropertyListItem.add(new Label("name", dbExpressionProperty.getName()));
                setupEditFields(dbExpressionPropertyListItem, dbExpressionProperty);
            }
        };
        add(listView);
    }

    private void setupEditFields(ListItem<DbExpressionProperty> dbExpressionPropertyListItem, DbExpressionProperty dbExpressionProperty) {
        final boolean escapeModel = dbExpressionProperty.getEscapeMarkup();
        final String expression = dbExpressionProperty.getExpression();
        dbExpressionPropertyListItem.add(new TextField<String>("field", new IModel<String>() {

            @Override
            public String getObject() {
                return null;
            }

            @Override
            public void setObject(String string) {
                parameters.put(expression, string);
            }

            @Override
            public void detach() {
            }
        }) {
            @Override
            public boolean isVisible() {
                return escapeModel;
            }
        });
        TextArea<String> contentArea = new TextArea<String>("textArea", new IModel<String>() {

            @Override
            public String getObject() {
                return null;
            }

            @Override
            public void setObject(String string) {
                parameters.put(expression, string);
            }

            @Override
            public void detach() {
            }
        }) {
            @Override
            public boolean isVisible() {
                return !escapeModel;
            }
        };
        TinyMCESettings tinyMCESettings = new TinyMCESettings(TinyMCESettings.Theme.advanced);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.link, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        tinyMCESettings.add(wicket.contrib.tinymce.settings.Button.unlink, TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
        contentArea.add(new TinyMceBehavior(tinyMCESettings));
        dbExpressionPropertyListItem.add(contentArea);
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
