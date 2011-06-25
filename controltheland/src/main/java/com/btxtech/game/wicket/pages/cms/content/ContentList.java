package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DataProviderInfo;
import com.btxtech.game.services.cms.DbContent;
import com.btxtech.game.services.cms.DbContentDetailLink;
import com.btxtech.game.services.cms.DbContentList;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
        setupDetailTable(dbContentList);
    }

    private void setupDetailTable(DbContentList dbContentList) {

        List<IColumn> columns = new ArrayList<IColumn>();
        for (DbContent dbContent : dbContentList.getColumnsCrud().readDbChildren()) {
            final Integer dbContentId = dbContent.getId();
            columns.add(new HeaderlessColumn<Object>() {

                @Override
                public void populateItem(Item<ICellPopulator<Object>> cellItem, String componentId, IModel<Object> rowModel) {
                    DbContent dbContent = cmsUiService.getDbContent(dbContentId);
                    BeanIdPathElement childBeanIdPathElement = null;
                    if (dbContent instanceof DataProviderInfo) {
                        childBeanIdPathElement = beanIdPathElement.createChild((DataProviderInfo) dbContent, rowModel.getObject());
                    } else if (dbContent instanceof DbContentDetailLink) {
                        childBeanIdPathElement = beanIdPathElement.createChild(((CrudChild) rowModel.getObject()).getId());
                    }
                    cellItem.add(cmsUiService.getComponent(dbContent, rowModel.getObject(), componentId, childBeanIdPathElement));
                }
            });
        }

        // Edit stuff
        add(new EditPanel("edit", contentId, beanIdPathElement, true, false));
        columns.add(new HeaderlessColumn<Object>() {

            @Override
            public void populateItem(Item<ICellPopulator<Object>> cellItem, String componentId, IModel<Object> rowModel) {
                BeanIdPathElement childBeanIdPathElement = beanIdPathElement.createChild(((CrudChild) rowModel.getObject()).getId());
                cellItem.add(new EditPanel(componentId, contentId, childBeanIdPathElement, false, true));
            }
        });


        DetachHashListProvider detachHashListProvider = new DetachHashListProvider() {
            @Override
            protected List createList() {
                return cmsUiService.getDataProviderBeans(beanIdPathElement);
            }
        };

        @SuppressWarnings("unchecked")
        IColumn[] columnsArray = columns.toArray(new IColumn[columns.size()]);

        int rowsPerPage = Integer.MAX_VALUE;
        if (dbContentList.isPageable()) {
            rowsPerPage = dbContentList.getRowsPerPage();
        }

        @SuppressWarnings("unchecked")
        DataTable dataTable = new DataTable("dataTable", columnsArray, detachHashListProvider, rowsPerPage);
        dataTable.addTopToolbar(new HeadersToolbar(dataTable, null));
        dataTable.addBottomToolbar(new NoRecordsToolbar(dataTable, new Model<String>("Nothing here")));
        if (dbContentList.isPageable()) {
            dataTable.addBottomToolbar(new NavigationToolbar(dataTable));
        }
        add(dataTable);
    }
}
