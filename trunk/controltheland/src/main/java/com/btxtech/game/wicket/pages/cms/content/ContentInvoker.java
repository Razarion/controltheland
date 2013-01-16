package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentInvoker;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.WysiwygEditor;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
    private HashMap<String, String> parameters = new HashMap<>();
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
        add(new Button("invoke", new Model<>(dbContentInvoker.getInvokeButtonName())) {
            @Override
            public void onSubmit() {
                try {
                    cmsUiService.invoke((DbContentInvoker) ContentInvoker.this.getDefaultModelObject(), parameters);
                    cmsUiService.setParentResponsePage(ContentInvoker.this, (DbContent) ContentInvoker.this.getDefaultModelObject(), beanIdPathElement);
                } catch (InvocationTargetException e) {
                    cmsUiService.setMessageResponsePage(ContentInvoker.this, "error", e.getTargetException().getMessage());
                }
            }
        });
        add(new Button("cancel", new Model<>(dbContentInvoker.getCancelButtonName())) {
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
        final String expression = dbExpressionProperty.getExpression();
        switch (dbExpressionProperty.getEditorType()) {
            case PLAIN_TEXT_FILED:
                dbExpressionPropertyListItem.add(new PlainTextField("editor", new IModel<String>() {

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
                }));
                break;
            case PLAIN_TEXT_AREA:
                dbExpressionPropertyListItem.add(new PlainTextArea("editor", new IModel<String>() {

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
                }));
                break;
            case HTML_AREA:
                dbExpressionPropertyListItem.add(new WysiwygEditor("editor", new IModel<String>() {

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
                }));
                break;
            default:
                throw new IllegalArgumentException("Unsupported EditorType: " + dbExpressionProperty.getEditorType());
        }
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
