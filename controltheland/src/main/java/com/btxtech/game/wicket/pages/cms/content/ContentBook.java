package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.DataProviderInfo;
import com.btxtech.game.services.cms.DbContentBook;
import com.btxtech.game.services.cms.DbContentRow;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
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
public class ContentBook extends Panel {
    @SpringBean
    private CmsUiService cmsUiService;
    private Object bean;
    private BeanIdPathElement beanIdPathElement;
    private int contentId;

    public ContentBook(String id, DbContentBook dbContentBook, final BeanIdPathElement beanIdPathElement) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        contentId = dbContentBook.getId();
        setDefaultModel(new LoadableDetachableModel<DbContentBook>() {
            @Override
            protected DbContentBook load() {
                bean = cmsUiService.getDataProviderBean(beanIdPathElement);
                return cmsUiService.getDbContent(contentId);
            }

            @Override
            protected void onDetach() {
                bean = null;
            }
        });
        add(new EditPanel("edit", dbContentBook, contentId, beanIdPathElement, false, false));
        setupPropertyBook();
        if (dbContentBook.getCssClass() != null) {
            add(new SimpleAttributeModifier("class", dbContentBook.getCssClass()));
        }
    }

    private void setupPropertyBook() {

        List<IColumn<DbContentRow>> columns = new ArrayList<IColumn<DbContentRow>>();

        // Label
        columns.add(new HeaderlessColumn<DbContentRow>() {
            @Override
            public void populateItem(Item<ICellPopulator<DbContentRow>> cellItem, String componentId, IModel<DbContentRow> rowModel) {
                cellItem.add(new Label(componentId, rowModel.getObject().getName()));
            }
        });

        // Property
        columns.add(new HeaderlessColumn<DbContentRow>() {
            @Override
            public void populateItem(Item<ICellPopulator<DbContentRow>> cellItem, String componentId, IModel<DbContentRow> rowModel) {
                BeanIdPathElement childBeanIdPathElement = null;
                if (rowModel.getObject().getDbContent() instanceof DataProviderInfo) {
                    childBeanIdPathElement = beanIdPathElement.createChildFromDataProviderInfo((DataProviderInfo) rowModel.getObject().getDbContent());
                }
                cellItem.add(cmsUiService.getComponent(rowModel.getObject().getDbContent(), bean, componentId, childBeanIdPathElement));
            }
        });

        DetachHashListProvider<DbContentRow> detachHashListProvider = new DetachHashListProvider<DbContentRow>() {
            @Override
            protected List<DbContentRow> createList() {
                DbContentBook dbContentBook = (DbContentBook) getDefaultModelObject();
                return dbContentBook.getRowCrud().readDbChildren();
            }
        };

        @SuppressWarnings("unchecked")
        IColumn<DbContentRow>[] columnsArray = columns.toArray(new IColumn[columns.size()]);

        DataTable<DbContentRow> dataTable = new DataTable<DbContentRow>("dataTable", columnsArray, detachHashListProvider, Integer.MAX_VALUE);
        dataTable.addTopToolbar(new HeadersToolbar(dataTable, null));
        dataTable.addBottomToolbar(new NoRecordsToolbar(dataTable, new Model<String>("Nothing here")));
        add(dataTable);
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
