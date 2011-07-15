package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.CmsService;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentCreateEdit;
import com.btxtech.game.services.cms.DbContentList;
import com.btxtech.game.services.cms.DbExpressionProperty;
import com.btxtech.game.wicket.pages.cms.CmsPage;
import com.btxtech.game.wicket.pages.cms.WritePanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.PageParameters;
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
                jumpToPage(beanIdPathElement);
            }
        });
        add(new Button("cancel") {
            @Override
            public void onSubmit() {
                jumpToPage(beanIdPathElement);
            }
        });

    }

    private void jumpToPage(BeanIdPathElement beanIdPathElement) {
        PageParameters parameters = new PageParameters();
        // Find out if parent was a detail link
        DbContent dbContent = cmsService.getDbContent(contentId);
        if (dbContent.getParent() instanceof DbContentList) {
            // ??? Why -> dbContent.getParent().getParent().getParent().getParent()
            parameters.put(CmsPage.DETAIL_CONTENT_ID, Integer.toString(dbContent.getParent().getParent().getParent().getParent().getId()));
        }
        ContentDetailLink.fillBeanIdPathUrlParameters(beanIdPathElement, parameters);
        setResponsePage(CmsPage.class, parameters);
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

}
