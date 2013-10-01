package com.btxtech.game.wicket.pages.cms.content;

import com.btxtech.game.services.cms.layout.DbContent;
import com.btxtech.game.services.cms.layout.DbContentList;
import com.btxtech.game.services.common.CrudChild;
import com.btxtech.game.wicket.pages.cms.ContentContext;
import com.btxtech.game.wicket.pages.cms.EditPanel;
import com.btxtech.game.wicket.uiservices.BeanIdPathElement;
import com.btxtech.game.wicket.uiservices.DetachHashListProvider;
import com.btxtech.game.wicket.uiservices.cms.CmsUiService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeaderlessColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
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
    private ContentContext contentContext;
    private int contentId;

    public ContentList(String id, DbContentList dbContentList, BeanIdPathElement beanIdPathElement, ContentContext contentContext) {
        super(id);
        this.beanIdPathElement = beanIdPathElement;
        this.contentContext = contentContext;
        contentId = dbContentList.getId();
        WebMarkupContainer table = new WebMarkupContainer("table");
        add(table);
        setupDetailTable(table, dbContentList);
        if (dbContentList.getCssClass() != null) {
            table.add(new AttributeModifier("class", dbContentList.getCssClass()));
        }
    }

    private void setupDetailTable(WebMarkupContainer table, DbContentList dbContentList) {
        add(new EditPanel("edit", dbContentList, contentId, beanIdPathElement, true, false));
        if (dbContentList.getColumnsCrud().readDbChildren().size() == 1 && dbContentList.getColumnCountSingleCell() != null) {
            setupDetailTableSingleCellMode(table, dbContentList);
        } else {
            setupDetailTableNormalColumns(table, dbContentList);
        }
    }

    private void setupDetailTableNormalColumns(WebMarkupContainer table, DbContentList dbContentList) {

        List<IColumn> columns = new ArrayList<>();
        for (DbContent dbContent : dbContentList.getColumnsCrud().readDbChildren()) {
            final Integer dbContentId = dbContent.getId();
            columns.add(new HeaderlessColumn<Object, Object>() {

                @Override
                public void populateItem(Item<ICellPopulator<Object>> cellItem, String componentId, IModel<Object> rowModel) {
                    DbContent dbContent = cmsUiService.getDbContent(dbContentId);
                    BeanIdPathElement childBeanIdPathElement = cmsUiService.createChildBeanIdPathElement(dbContent, beanIdPathElement, (CrudChild) rowModel.getObject());
                    cellItem.add(cmsUiService.getComponent(dbContent, rowModel.getObject(), componentId, childBeanIdPathElement, contentContext));
                }
            });
        }

        // Edit stuff
        if (cmsUiService.isEnterEditModeAllowed(contentId, beanIdPathElement)) {
            columns.add(new HeaderlessColumn<Object, Object>() {

                @Override
                public void populateItem(Item<ICellPopulator<Object>> cellItem, String componentId, IModel<Object> rowModel) {
                    BeanIdPathElement childBeanIdPathElement = beanIdPathElement.createChildFromBeanId(((CrudChild) rowModel.getObject()).getId());
                    cellItem.add(new EditPanel(componentId, null, contentId, childBeanIdPathElement, false, true));
                }
            });
        }


        DetachHashListProvider detachHashListProvider = new DetachHashListProvider() {
            @Override
            protected List createList() {
                return cmsUiService.getDataProviderBeans(beanIdPathElement, contentId, contentContext);
            }
        };

        @SuppressWarnings("unchecked")
        final DataGridView dataGridView = new DataGridView("rows", columns, detachHashListProvider);
        // Head
        table.add(new TableHead("tHead", dbContentList, dbContentList.getCssClassHead(), beanIdPathElement, contentContext));
        table.add(dataGridView);
        BookmarkablePagingNavigator pagingNavigator = new BookmarkablePagingNavigator("navigator", contentId, dataGridView, beanIdPathElement);

        if (dbContentList.isPageable()) {
            dataGridView.setItemsPerPage(dbContentList.getRowsPerPage());
        }
        if (contentContext.hasContentPagingNumber(contentId)) {
            dataGridView.setCurrentPage(contentContext.getContentPagingNumber(contentId));
        }
        pagingNavigator.setVisible(dbContentList.isPageable());
        add(pagingNavigator);
    }

    private void setupDetailTableSingleCellMode(WebMarkupContainer table, DbContentList dbContentList) {
        DbContent dbContent = dbContentList.getColumnsCrud().readDbChildren().get(0);

        List list = cmsUiService.getDataProviderBeans(beanIdPathElement, contentId, contentContext);

        RepeatingView row = new RepeatingView("rows");
        while (!list.isEmpty()) {
            WebMarkupContainer rowContainer = new WebMarkupContainer(row.newChildId());
            row.add(rowContainer);
            RepeatingView cells = new RepeatingView("cells");
            rowContainer.add(cells);
            for (int cellNumber = 0; cellNumber < dbContentList.getColumnCountSingleCell(); cellNumber++) {
                WebMarkupContainer cellContainer = new WebMarkupContainer(cells.newChildId());
                cells.add(cellContainer);
                if (list.isEmpty()) {
                    cellContainer.add(new Label("cell", ""));
                } else {
                    Object o = list.remove(0);
                    BeanIdPathElement childBeanIdPathElement = cmsUiService.createChildBeanIdPathElement(dbContent, beanIdPathElement, (CrudChild) o);
                    cellContainer.add(cmsUiService.getComponent(dbContent, o, "cell", childBeanIdPathElement, contentContext));
                }
            }
        }
        table.add(row);

        table.add(new Label("tHead", "").setVisible(false));
        add(new Label("navigator", "").setVisible(false));
    }

    @Override
    public boolean isVisible() {
        return cmsUiService.isReadAllowed(contentId);
    }
}
