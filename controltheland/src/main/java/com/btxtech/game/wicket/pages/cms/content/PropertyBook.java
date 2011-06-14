package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DbPropertyBook;
import com.btxtech.game.services.cms.DbPropertyRow;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.html.basic.Label;
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
public class PropertyBook extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private Object bean;
    private Integer childId;

    public PropertyBook(String id, DbPropertyBook dbPropertyBook, final Integer childId) {
        super(id);
        this.childId = childId;
        final int contentId = dbPropertyBook.getId();
        setDefaultModel(new LoadableDetachableModel<DbPropertyBook>() {
            @Override
            protected DbPropertyBook load() {
                DbPropertyBook dbPropertyBook = cmsUiService.getContentStructure(contentId);
                bean = cmsUiService.getContentDataProviderBean(dbPropertyBook, childId);
                return dbPropertyBook;
            }

            @Override
            protected void onDetach() {
                bean = null;
            }
        });
        setupPropertyBook();
    }

    private void setupPropertyBook() {

        List<IColumn<DbPropertyRow>> columns = new ArrayList<IColumn<DbPropertyRow>>();

        // Label
        columns.add(new AbstractColumn<DbPropertyRow>(new Model<String>("????1")) {
            @Override
            public void populateItem(Item<ICellPopulator<DbPropertyRow>> cellItem, String componentId, IModel<DbPropertyRow> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getName()));
            }
        });

        // Property
        columns.add(new AbstractColumn<DbPropertyRow>(new Model<String>("????1")) {
            @Override
            public void populateItem(Item<ICellPopulator<DbPropertyRow>> cellItem, String componentId, IModel<DbPropertyRow> rowModel) {
                cellItem.add(cmsUiService.getContent(rowModel.getObject().getDbProperty(), bean, componentId, childId));
            }
        });

        DetachHashListProvider<DbPropertyRow> detachHashListProvider = new DetachHashListProvider<DbPropertyRow>() {
            @Override
            protected List<DbPropertyRow> createList() {
                DbPropertyBook dbPropertyBook = (DbPropertyBook) getDefaultModelObject();
                return dbPropertyBook.getDbPropertyRows();
            }
        };

        @SuppressWarnings("unchecked")
        IColumn<DbPropertyRow>[] columnsArray = columns.toArray(new IColumn[columns.size()]);

        DataTable<DbPropertyRow> dataTable = new DataTable<DbPropertyRow>("dataTable", columnsArray, detachHashListProvider, Integer.MAX_VALUE);
        dataTable.addTopToolbar(new HeadersToolbar(dataTable, null));
        dataTable.addBottomToolbar(new NoRecordsToolbar(dataTable, new Model<String>("Nothing here")));
        add(dataTable);
    }
}
