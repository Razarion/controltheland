package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentCreateEdit;
import com.btxtech.game.services.cms.layout.DbExpressionProperty;
import com.btxtech.game.wicket.pages.cms.WritePanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: 12.07.2011
 * Time: 21:03:14
 */
public class ContentCreateEdit extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    @SpringBean
    private CmsService cmsService;
    private int contentId;

    public ContentCreateEdit(String componentId, DbContentCreateEdit dbContentCreateEdit, final BeanIdPathElement beanIdPathElement) {
        super(componentId);
        contentId = dbContentCreateEdit.getId();
        setDefaultModel(new LoadableDetachableModel<DbContentCreateEdit>() {
            @Override
            protected DbContentCreateEdit load() {
                return cmsUiService.getDbContent(contentId);
            }
        });
        setupCreateFields(beanIdPathElement);
        setupButtons(beanIdPathElement);
    }

    private void setupButtons(final BeanIdPathElement beanIdPathElement) {
        add(new Button("submit") {
            @Override
            public void onSubmit() {
                cmsUiService.createAndFillBean(beanIdPathElement);
                cmsUiService.setParentResponsePage(ContentCreateEdit.this, (DbContent) ContentCreateEdit.this.getDefaultModelObject(), beanIdPathElement);
            }
        });
        add(new Button("cancel") {
            @Override
            public void onSubmit() {
                cmsUiService.setParentResponsePage(ContentCreateEdit.this, (DbContent) ContentCreateEdit.this.getDefaultModelObject(), beanIdPathElement);
            }
        });

    }

    private void setupCreateFields(final BeanIdPathElement beanIdPathElement) {
        ListView<DbExpressionProperty> listView = new ListView<DbExpressionProperty>("listView", new LoadableDetachableModel<List<DbExpressionProperty>>() {

            @Override
            protected List<DbExpressionProperty> load() {
                DbContentCreateEdit dbContentCreateEdit = (DbContentCreateEdit) getDefaultModelObject();
                return dbContentCreateEdit.getValueCrud().readDbChildren();
            }
        }) {

            @Override
            protected void populateItem(ListItem<DbExpressionProperty> dbExpressionPropertyListItem) {
                dbExpressionPropertyListItem.add(new Label("name", dbExpressionPropertyListItem.getModelObject().getName()));
                BeanIdPathElement childBeanIdPathElement = beanIdPathElement.createChildFromDataProviderInfo(dbExpressionPropertyListItem.getModelObject());
                dbExpressionPropertyListItem.add(new WritePanel("content", null, childBeanIdPathElement, dbExpressionPropertyListItem.getModelObject()));
            }
        };
        add(listView);
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
