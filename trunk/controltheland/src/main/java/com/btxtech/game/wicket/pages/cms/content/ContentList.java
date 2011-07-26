package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentList;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.TableHead;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:29:43
 */
public class ContentList extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private BeanIdPathElement beanIdPathElement;
    private int contentId;

    public ContentList(String id, DbContentList dbContentList, BeanIdPathElement beanIdPathElement) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        contentId = dbContentList.getId();
        WebMarkupContainer table = new WebMarkupContainer("table");
        add(table);
        setupDetailTable(table, dbContentList);
        if (dbContentList.getCssClass() != null) {
            table.add(new SimpleAttributeModifier("class", dbContentList.getCssClass()));
        }
    }

    private void setupDetailTable(WebMarkupContainer table, DbContentList dbContentList) {

        List<IColumn> columns = new ArrayList<IColumn>();
        for (DbContent dbContent : dbContentList.getColumnsCrud().readDbChildren()) {
            final Integer dbContentId = dbContent.getId();
            columns.add(new HeaderlessColumn<Object>() {

                @Override
                public void populateItem(Item<ICellPopulator<Object>> cellItem, String componentId, IModel<Object> rowModel) {
                    DbContent dbContent = cmsUiService.getDbContent(dbContentId);
                    BeanIdPathElement childBeanIdPathElement = cmsUiService.createChildBeanIdPathElement(dbContent, beanIdPathElement, (CrudChild) rowModel.getObject());
                    cellItem.add(cmsUiService.getComponent(dbContent, rowModel.getObject(), componentId, childBeanIdPathElement));
                }
            });
        }

        // Edit stuff
        add(new EditPanel("edit", dbContentList, contentId, beanIdPathElement, true, false));
        if (cmsUiService.isEnterEditModeAllowed(contentId, beanIdPathElement)) {
            columns.add(new HeaderlessColumn<Object>() {

                @Override
                public void populateItem(Item<ICellPopulator<Object>> cellItem, String componentId, IModel<Object> rowModel) {
                    BeanIdPathElement childBeanIdPathElement = beanIdPathElement.createChildFromBeanId(((CrudChild) rowModel.getObject()).getId());
                    cellItem.add(new EditPanel(componentId, null, contentId, childBeanIdPathElement, false, true));
                }
            });
        }

        // Head
        table.add(new TableHead("tHead", getHeaderNames(dbContentList), dbContentList.getCssClassHead()));

        DetachHashListProvider detachHashListProvider = new DetachHashListProvider() {
            @Override
            protected List createList() {
                return cmsUiService.getDataProviderBeans(beanIdPathElement);
            }
        };

        @SuppressWarnings("unchecked")
        DataGridView dataGridView = new DataGridView("rows", columns, detachHashListProvider);
        table.add(dataGridView);
        PagingNavigator pagingNavigator = new PagingNavigator("navigator", dataGridView);
        if (dbContentList.isPageable()) {
            dataGridView.setRowsPerPage(dbContentList.getRowsPerPage());
        }
        pagingNavigator.setVisible(dbContentList.isPageable());
        add(pagingNavigator);
    }

    private List<String> getHeaderNames(DbContentList dbContentList) {
        if (!dbContentList.isShowHead()) {
            return null;
        }
        List<String> name = new ArrayList<String>();
        for (DbContent dbContent : dbContentList.getColumnsCrud().readDbChildren()) {
            name.add(dbContent.getName());
        }
        return name;
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
