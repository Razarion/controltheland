package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DbBeanTable;
import com.btxtech.game.services.cms.DbProperty;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: beat
 * Date: 09.06.2011
 * Time: 12:29:43
 */
public class BeanTable extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private Integer childId;

    public BeanTable(String id, DbBeanTable dbBeanTable, Integer childId) {
        super(id);
        this.childId = childId;
        final int contentId = dbBeanTable.getId();
        setDefaultModel(new LoadableDetachableModel<DbBeanTable>() {
            @Override
            protected DbBeanTable load() {
                return cmsUiService.getContentStructure(contentId);
            }
        });
        setupDetailTable(dbBeanTable);
    }

    private void setupDetailTable(DbBeanTable dbBeanTable) {

        List<IColumn> columns = new ArrayList<IColumn>();
        for (DbProperty dbProperty : dbBeanTable.getDbPropertyColumns()) {
            final int contentId = dbProperty.getId();
            columns.add(new AbstractColumn<Object>(new Model<String>("????1")) {

                @Override
                public void populateItem(Item<ICellPopulator<Object>> cellItem, String componentId, IModel<Object> rowModel) {
                    cellItem.add(cmsUiService.getContent(contentId, rowModel.getObject(), componentId, childId));
                }
            });
        }

        DetachHashListProvider detachHashListProvider = new DetachHashListProvider() {
            @Override
            protected List createList() {
                DbBeanTable dbBeanTable = (DbBeanTable) getDefaultModelObject();
                return cmsUiService.getChildContentDataProviderBeans(dbBeanTable, childId);
            }
        };

        @SuppressWarnings("unchecked")
        IColumn[] columnsArray = columns.toArray(new IColumn[columns.size()]);

        int rowsPerPage = Integer.MAX_VALUE;
        if (dbBeanTable.isPageable()) {
            rowsPerPage = dbBeanTable.getRowsPerPage();
        }

        DataTable dataTable = new DataTable("dataTable", columnsArray, detachHashListProvider, rowsPerPage);
        dataTable.addTopToolbar(new HeadersToolbar(dataTable, null));
        dataTable.addBottomToolbar(new NoRecordsToolbar(dataTable, new Model<String>("Nothing here")));
        if (dbBeanTable.isPageable()) {
            dataTable.addBottomToolbar(new NavigationToolbar(dataTable));
        }
        add(dataTable);
    }
}
