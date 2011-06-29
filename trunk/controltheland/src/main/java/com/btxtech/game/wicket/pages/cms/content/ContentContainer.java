package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DataProviderInfo;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentContainer;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:29:43
 */
public class ContentContainer extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private Object bean;
    private BeanIdPathElement beanIdPathElement;
    private int contentId;

    public ContentContainer(String id, DbContentContainer dbContentContentContainer, final BeanIdPathElement beanIdPathElement) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        contentId = dbContentContentContainer.getId();
        setDefaultModel(new LoadableDetachableModel<DbContentContainer>() {
            @Override
            protected DbContentContainer load() {
                bean = cmsUiService.getDataProviderBean(beanIdPathElement);
                return cmsUiService.getDbContent(contentId);
            }

            @Override
            protected void onDetach() {
                bean = null;
            }
        });
        setupPropertyBook();
        if(dbContentContentContainer.getCssClass() != null) {
            add(new SimpleAttributeModifier("class", dbContentContentContainer.getCssClass()));
        }
    }

    private void setupPropertyBook() {
        ListView<DbContent> listView = new ListView<DbContent>("listView", new LoadableDetachableModel<List<DbContent>>() {

            @Override
            protected List<DbContent> load() {
                DbContentContainer dbContentContentContainer = (DbContentContainer) getDefaultModelObject();
                return dbContentContentContainer.getContentCrud().readDbChildren();
            }
        }) {

            @Override
            protected void populateItem(ListItem<DbContent> dbContentListItem) {
                BeanIdPathElement childBeanIdPathElement = null;
                if (dbContentListItem.getModelObject() instanceof DataProviderInfo) {
                    childBeanIdPathElement = beanIdPathElement.createChild((DataProviderInfo) dbContentListItem.getModelObject(), null);
                }
                dbContentListItem.add(cmsUiService.getComponent(dbContentListItem.getModelObject(), bean, "content", childBeanIdPathElement));
            }
        };
        add(listView);
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
